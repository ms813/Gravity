import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.event.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class MainState extends GameState {

    private List<GameObject> dustList = new ArrayList<>();
    private Grid grid = new Grid(Vector2f.ZERO, Vector2f.ZERO, 50.0f);

    private final float APPROXIMATION_CUTOFF = 50f;

    public MainState(Game game) {
        super(game);

        //set quad origin to (0,0) and size to (0,0)

        for (int i = 0; i < 500; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(300, 300));
            Dust d = new Dust(new Random().nextFloat() * 3 + 1, pos);
            d.setVelocity(new Vector2f(0.2f, 0));
            dustList.add(d);
        }

        for (int i = 0; i < 500; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(500, 500));
            Dust d = new Dust(new Random().nextFloat() * 3 + 1, pos);
            d.setVelocity(new Vector2f(-0.2f, 0));
            dustList.add(d);
        }

        grid.setBounds(getGridBounds(dustList));
        for(GameObject o : dustList){
            grid.insert(o);
        }
    }

    @Override
    public void draw(float dt) {
        for (GameObject a : dustList) {
            a.draw(game.getWindow());
        }
        grid.draw(game.getWindow());
    }

    @Override
    public void update(float dt) {
        System.out.println("Frame start");

        //sort the dust so that the smaller particles are at the front
        dustList.sort(new MassComparator());

        /*
        *   Populate the spatial hash grid
        */
        long startTime = System.nanoTime();
        grid.clear();
        for(GameObject o : dustList){
            grid.insert(o);
        }
        long endTime = System.nanoTime();
        System.out.println("Building grid took: " + (endTime - startTime) / 1000000 + " millis");

        /*
        *   Collision detection
        */
        List<GameObject> mergingObjects = new ArrayList<>();
        HashMap<Vector2i, ArrayList<GameObject>> cells = grid.getCells();

        for(ArrayList<GameObject> cellObjects : cells.values()){
            for(GameObject o : cellObjects){

                if(mergingObjects.contains(o)) continue;   //ignore objects queued to be merged

                for(GameObject x : cellObjects){
                    if(o == x) continue; //ignore collisions with self
                    if(mergingObjects.contains(x)) continue;          //ignore objects queued to be merged

                    Vector2f dir = Vector2f.sub(x.getPosition(), o.getPosition());
                    float dist = VectorMath.magnitude(dir);

                    GameObject larger, smaller;

                    if(o.getMass() > x.getMass()){
                        larger = o;
                        smaller = x;
                    } else{
                        larger = x;
                        smaller = o;
                    }

                    //only need to check one dimension as all particles are currently symmetrical
                    if(dist < larger.getSize().x / 2){
                        larger.merge(smaller);
                        mergingObjects.add(smaller);
                    }
                }
            }
        }

        dustList.removeAll(mergingObjects);

        //run the update loop on all of the particles
        for (GameObject a : dustList) {
            a.update(dt);
        }
        System.out.println("Frame end");
    }

    @Override
    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            }
        }
    }

    public Vector2f calculateGForce(GameObject o1, GameObject o2) {

        //F = GmM / r^2

        Vector2f dir = Vector2f.sub(o2.getPosition(), o1.getPosition());

        float F, G, m, M, r;
        G = GlobalConstants.GRAVITATIONAL_CONSTANT;
        m = o1.getMass();
        M = o2.getMass();
        r = VectorMath.magnitude(dir);
        F = (G * m * M) / (float) Math.pow(r, 2);

        return Vector2f.mul(VectorMath.normalize(dir), F);
    }

    public FloatRect getGridBounds(List<GameObject> list) {

        float top = list.get(0).getPosition().y,
                bottom = list.get(0).getPosition().y,
                left = list.get(0).getPosition().x,
                right = list.get(0).getPosition().x;

        //find the furthest away particles to scale the quad tree
        for (GameObject d : list) {
            if (d.getPosition().x < left) {
                left = d.getPosition().x;
            }

            if (d.getPosition().x > right) {
                right = d.getPosition().x;
            }

            if (d.getPosition().y < top) {
                top = d.getPosition().y;
            }

            if (d.getPosition().y > bottom) {
                bottom = d.getPosition().y;
            }
        }

        Vector2f size = new Vector2f(right - left, bottom - top);
        Vector2f pos = new Vector2f(left, top);
        return new FloatRect(pos, size);
    }
}
