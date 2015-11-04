package GameObjects.Colliders;

import Core.GlobalConstants;
import GameObjects.GameObject;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 04/11/15.
 */
public interface Collider  {
    float getBreakForce();
    void calculateCollision(Collider collider);
    void applyCollision();
    Vector2f getCenter();
    float getRadius();
    void rescale(float size);
    float getMass();
    Vector2f getVelocity();
    void move(Vector2f offset);
    void setVelocity(Vector2f velocity);

    void draw(RenderWindow window);
    void update();
    GameObject getParent();

}
