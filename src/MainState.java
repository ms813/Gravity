import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class MainState extends GameState {

    private List<GameObject> dustList = new ArrayList<>();

    private GravityQuadTree gravityQuad;
    private final float APPROXIMATION_CUTOFF = 50f;

    public MainState(Game game) {
        super(game);

        //set quad origin to (0,0) and size to (0,0)
        gravityQuad = new GravityQuadTree(0, new FloatRect(Vector2f.ZERO, new Vector2f(game.getWindow().getSize())), null);

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

        gravityQuad.clear();
        for (GameObject o : dustList) {
            gravityQuad.insert(o);
        }

        //this scales the quad for the first time
        gravityQuad.setBounds(getQuadBounds(dustList));
    }

    @Override
    public void draw(float dt) {
        for (GameObject a : dustList) {
            a.draw(game.getWindow());
        }
        gravityQuad.draw(game.getWindow());
    }

    @Override
    public void update(float dt) {
        System.out.println("Frame start");

        gravityQuad.clear();
        for (GameObject o : dustList) {
            gravityQuad.insert(o);
        }

        gravityQuad.setBounds(getQuadBounds(dustList));

        //gravityQuad.printStatus();


        List<GameObject> returnObjects = new ArrayList<>();
        List<GameObject> merging = new ArrayList<>();

        for(GameObject o : dustList){

            returnObjects.clear();
            gravityQuad.retrieve(returnObjects, o);
            if(merging.contains(o)) continue;
            for (GameObject x : returnObjects) {
                if(x == o) continue;
                if(merging.contains(x)) continue;
                //CoreGameObject x is in same quad as o

                //determine the larger and smaller particle
                GameObject smaller, larger;
                if (o.getMass() < x.getMass()) {
                    smaller = o;
                    larger = x;
                } else {
                    smaller = x;
                    larger = o;
                }

                //check if circles overlap
                Vector2f dir = Vector2f.sub(smaller.getPosition(), larger.getPosition());
                float distance = VectorMath.magnitude(dir);


                //only need to check width as all particles are circles at present
                if (distance < larger.getBounds().width/2) {
                    larger.merge(smaller);
                    merging.add(smaller);
                }
            }
        }
      //  System.out.println("Removing " + merging);
        dustList.removeAll(merging);


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

    public FloatRect getQuadBounds(List<GameObject> list) {

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
