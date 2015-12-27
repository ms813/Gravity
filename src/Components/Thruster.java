package Components;

import GameObjects.Entity;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.json.simple.JSONObject;

/**
 * Created by smithma on 18/11/15.
 */
public class Thruster extends Component{

    private double rotation = 0; //between -π and π, 0 is towards the right of the screen
    private float throttle = 1; //between 0 and 1. Fraction of thrust to be applied
    private float maxThrust = 0;


    @Override
    public void draw(RenderWindow window) {
        //deliberately blank
    }

    @Override
    public void update(float dt, boolean VERLET_STATE) {
        //deliberately blank
    }

    @Override
    public void initialise(JSONObject attributes, Entity owner) {
        super.initialise(owner);
        maxThrust = Float.parseFloat((String) attributes.get("maxThrust"));
    }

    public Vector2f getThrustVector() {
        float x = (float) Math.cos(rotation) * maxThrust * throttle;
        float y = (float) Math.sin(rotation) * maxThrust * throttle;
        return new Vector2f(x, y);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {

        if (rotation > Math.PI) {
            rotation -= 2 * Math.PI;
        } else if (rotation < -Math.PI) {
            rotation += 2 * Math.PI;
        }

        this.rotation = rotation;
    }

    public float getThrottle() {
        return throttle;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public float getMaxThrust() {
        return maxThrust;
    }

    public void setMaxThrust(float maxThrust) {
        this.maxThrust = maxThrust;
    }
}
