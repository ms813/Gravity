package GameObjects.Colliders;

import Core.GlobalConstants;
import GameObjects.GameObject;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 04/11/15.
 */
public interface Collider  {

    void calculateCollision(Collider collider);
    void applyCollision();

    void rescale(float size);
    FloatRect getBounds();

    Vector2f getVelocity();
    void setVelocity(Vector2f velocity);
    void move(Vector2f offset);

    void draw(RenderWindow window);
    void update();
    GameObject getParent();
}
