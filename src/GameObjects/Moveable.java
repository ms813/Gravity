package GameObjects;

import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface Moveable extends iCoreGameObject {
    void move(Vector2f offset);
    Vector2f getVelocity();
    float getMass();
    void applyForce(Vector2f force);

}
