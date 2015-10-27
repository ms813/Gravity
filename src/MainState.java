import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.event.Event;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class MainState extends GameState {

    private List<Dust> dustList = new ArrayList<>();

    private PointQuadTree pointQuad;

    public MainState(Game game){
        super(game);

        //set quad origin to (0,0) and size to (0,0)
        pointQuad = new PointQuadTree(0, new FloatRect(Vector2f.ZERO, Vector2f.ZERO));

        for(int i = 0; i < 500; i++){
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(),new Random().nextFloat()*300), new Vector2f(300,300));
            Dust d = new Dust(new Random().nextFloat() + 1, pos);
            d.setVelocity(new Vector2f(0.1f, 0));
            dustList.add(d);
        }

        for(int i = 0; i < 500; i++){
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(),new Random().nextFloat()*300), new Vector2f(500,500));
            Dust d = new Dust(new Random().nextFloat() + 1, pos);
            d.setVelocity(new Vector2f(-0.1f, 0));
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
        pointQuad.draw(game.getWindow());
    }

    @Override
    public void update(float dt) {

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

        pointQuad.clear();
        Vector2f size = new Vector2f(right - left, bottom - top);
        Vector2f pos = new Vector2f(left, top);
        pointQuad.setBounds(new FloatRect(pos, size));
        for(Dust d : dustList){
            pointQuad.insert(d);
        }

        //pointQuad.printStatus();

        List<GameObject> returnObjects = new ArrayList<>();
        for(GameObject o : dustList){
            returnObjects.clear();
            pointQuad.retrieve(returnObjects, o);

            for(GameObject x : returnObjects){
                //GameObject x is in same quad as o
            }
        }

        List<Dust> mergingDust = new ArrayList<>();

        for(Dust a : dustList){

            Vector2f sumForce = Vector2f.ZERO;

            for(Dust b : dustList){

                //don't calculate forces for same shape
                if(a != b){


                    //don't calculate if shapes overlap

                    float distanceApart = VectorMath.magnitude(Vector2f.sub(b.getPosition(), a.getPosition()));
                    Dust larger, smaller;

                    if(a.getRadius() >= b.getRadius()){
                        larger = a;
                        smaller = b;
                    } else {
                        larger = b;
                        smaller = a;
                    }

                    if(distanceApart > larger.getRadius()){
                        Vector2f dif = Vector2f.sub(b.getPosition(), a.getPosition());

                        //F = GmM/r^2

                        float G = (float) GlobalConstants.GRAVITATIONAL_CONSTANT;
                        float m = a.getMass();
                        float M = b.getMass();
                        float r = VectorMath.magnitude(dif);


                        float F = (G * m * M) / (float) Math.pow(r, 2);
                        Vector2f direction = VectorMath.normalize(dif);

                        Vector2f forceVector = Vector2f.mul(direction, F);

                        sumForce = Vector2f.add(sumForce, forceVector);
                    } else{
                        System.out.println(a + " merge " + b);
                        if(!mergingDust.contains(smaller)) {
                            mergingDust.add(smaller);
                            larger.merge(smaller);
                        }
                    }
                }
            }
            a.applyForce(sumForce);
        }
        if(mergingDust.size() > 0){
            dustList.removeAll(mergingDust);
            System.out.println("removed " + mergingDust);
            mergingDust.clear();
            System.out.println("particles remainaing: " + dustList.size());
        }

        for(GameObject a : dustList){
            a.update(dt);
        }
    }

    @Override
    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            }
        }
    }
}
