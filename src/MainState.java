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

    private List<Dust> dustList = new ArrayList<>();

    private GravityQuadTree gravityQuad;
    private final float APPROXIMATION_CUTOFF = 50f;

    public MainState(Game game){
        super(game);

        //set quad origin to (0,0) and size to (0,0)
        gravityQuad = new GravityQuadTree(0, new FloatRect(Vector2f.ZERO, Vector2f.ZERO));

        for(int i = 0; i < 500; i++){
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(),new Random().nextFloat()*300), new Vector2f(300,300));
            Dust d = new Dust(new Random().nextFloat() *3+ 1, pos);
            d.setVelocity(new Vector2f(0, 0));
            dustList.add(d);
        }

        for(int i = 0; i < 500; i++){
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(),new Random().nextFloat()*300), new Vector2f(500,500));
            Dust d = new Dust(new Random().nextFloat() *3+ 1, pos);
            d.setVelocity(new Vector2f(0, 0));
            dustList.add(d);
        }

        //this scales the quad for the first time
        update(0);
    }

    @Override
    public void draw(float dt) {
        for(GameObject a : dustList){
            a.draw(game.getWindow(), dt);
        }
        gravityQuad.draw(game.getWindow());
    }

    @Override
    public void update(float dt) {
        System.out.println("Frame start");
        float top = dustList.get(0).getPosition().y,
                bottom  = dustList.get(0).getPosition().y,
                left = dustList.get(0).getPosition().x,
                right = dustList.get(0).getPosition().x;

        //find the furthest away particles to scale the quad tree
        for(Dust d : dustList){
            if(d.getPosition().x < left){
                left = d.getPosition().x;
            }

            if(d.getPosition().x > right){
                right = d.getPosition().x;
            }

            if(d.getPosition().y < top){
                top = d.getPosition().y;
            }

            if(d.getPosition().y > bottom){
                bottom = d.getPosition().y;
            }
        }

        gravityQuad.clear();
        Vector2f size = new Vector2f(right - left, bottom - top);
        Vector2f pos = new Vector2f(left, top);
        gravityQuad.setBounds(new FloatRect(pos, size));
        for(Dust d : dustList){
            gravityQuad.insert(d);
        }

        //gravityQuad.printStatus();

        /*
        List<GameObject> returnObjects = new ArrayList<>();
        for(GameObject o : dustList){
            returnObjects.clear();
            gravityQuad.retrieve(returnObjects, o);
            for(GameObject x : returnObjects){
                //GameObject x is in same quad as o
                //do collision detection here
            }
        }
        */


        //get a list of all quads on the screen
        //particlesInQuads list is a dirty way to prevent duplicates
        List<GravityQuad> quads = new ArrayList<>();
        List<List<GameObject>> particlesInQuads = new ArrayList<>();

        for(GameObject o : dustList){
            List<GameObject> objs = new ArrayList<>();
            gravityQuad.retrieve(objs, o);

            if(!particlesInQuads.contains(objs)){
                particlesInQuads.add(objs);
                quads.add(new GravityQuad(objs));
            }
        }
        System.out.println("No of quads: " + quads.size());

        int detailedCount = 0, approxCount = 0;
        for(GameObject o : dustList){
            Vector2f sumForce = Vector2f.ZERO;
            if(o.getPosition().x == 0 || o.getPosition().y ==0) System.out.println("stuff = 0");
            for(GravityQuad q : quads){
                //calculate distance between o and q's center of mass
                Vector2f dir = Vector2f.sub(q.getCenterOfMass(), o.getPosition());
                float distance = VectorMath.magnitude(dir);

                //close quads get a detailed calculation involving each object in the quad
                if(distance < APPROXIMATION_CUTOFF){
                    detailedCount++;
                    List<GameObject> objs = q.getObjects();
                    for(GameObject nearObj : objs){
                        sumForce = Vector2f.add(sumForce, calculateGForce(o, nearObj));
                    }
                }
                //further away quads get an approximation
                else{
                    approxCount++;
                    sumForce = Vector2f.add(sumForce, calculateGForce(o, q));
                }
            }
            //System.out.println("Sumforce: " + sumForce);
            o.applyForce(sumForce);
        }

        System.out.println("Detailed count: " + detailedCount + ", approx count: " + approxCount);

        //run the update loop on all of the particles
        for(GameObject a : dustList){
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

    public Vector2f calculateGForce(GameObject o1, GameObject o2){

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

    public Vector2f calculateGForce(GameObject o, GravityQuad q){

        //F = GmM / r^2

        Vector2f dir = Vector2f.sub(q.getCenterOfMass(), o.getPosition());

        float F, G, m, M, r;
        G = GlobalConstants.GRAVITATIONAL_CONSTANT;
        m = o.getMass();
        M = q.getTotalMass();
        r = VectorMath.magnitude(dir);

        F = (G * m * M) / (float) Math.pow(r, 2);

        return Vector2f.mul(VectorMath.normalize(dir), F);
    }
}
