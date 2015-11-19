package GameObjects;

import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 18/11/15.
 */
public class Thruster {

    private double rotation = 0; //between -π and π, 0 is towards the right of the screen
    private float throttle = 1; //between 0 and 1. Fraction of thrust to be applied
    private float maxThrust = 0;

    public Vector2f getThrustVector() {
        float x = (float) Math.cos(rotation) * maxThrust * throttle;
        float y = (float) Math.sin(rotation) * maxThrust * throttle;
        return new Vector2f(x, y);
    }

    public void setRotation(double rotation) {

        if (rotation > Math.PI) {
            rotation -= 2 * Math.PI;
        } else if (rotation < -Math.PI) {
            rotation += 2 * Math.PI;
        }

        this.rotation = rotation;
    }
}
