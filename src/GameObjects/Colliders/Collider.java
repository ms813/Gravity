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

    boolean isColliding(GameObject object);
    void calculateCollision(GameObject object);
    void applyCollision();

    FloatRect getBounds();
    Vector2f getSize();
    Vector2f getCenter();
    Vector2f getPosition();

    void draw(RenderWindow window);
    void update();
    GameObject getParent();
}