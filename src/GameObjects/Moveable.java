package GameObjects;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface Moveable extends CoreGameObject {
    void move(Vector2f offset);
    Vector2f getVelocity();
    void setVelocity(Vector2f velocity);
    float getMass();
    void applyForce(Vector2f force);
    Vector2f getKineticEnergy();

    float getCollisionRadius();

    void drawVelocity(RenderWindow w);
    void drawTrail(RenderWindow w);

}
