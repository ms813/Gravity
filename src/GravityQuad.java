import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 28/10/15.
 */
public class GravityQuad {
    List<GameObject> objects = new ArrayList<>();
    Vector2f centerOfMass = Vector2f.ZERO;
    float totalMass;

    public GravityQuad(List<GameObject> objects){
        this.objects = objects;

        float x = 0, y = 0;
        for(GameObject o : objects){
            totalMass += o.getMass();
            x += o.getPosition().x * o.getMass();
            y += o.getPosition().y * o.getMass();
        }

        centerOfMass = new Vector2f(x / totalMass, y / totalMass);
    }

    public float objectCount(){
        return objects.size();
    }

    public Vector2f getCenterOfMass(){
        return centerOfMass;
    }

    public float getTotalMass(){
        return totalMass;
    }

    public boolean contains(GameObject o){
        return objects.contains(o);
    }

    public List<GameObject> getObjects(){
        return objects;
    }
}
