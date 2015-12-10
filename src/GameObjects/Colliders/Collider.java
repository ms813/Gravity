package GameObjects.Colliders;

import Core.GlobalConstants;
import GameObjects.GameObject;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shape;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 04/11/15.
 */
public abstract class Collider {

    protected GameObject parent;

    protected Shape hitbox = new CircleShape();

    public Collider(GameObject parent) {
        this.parent = parent;
    }

    public abstract boolean isColliding(GameObject object);

    public abstract void calculateCollision(GameObject object);

    public abstract void applyCollision();

    public abstract void update();

    public FloatRect getBounds() {
        return hitbox.getGlobalBounds();
    }

    public Vector2f getSize() {
        return new Vector2f(hitbox.getGlobalBounds().width, hitbox.getGlobalBounds().height);
    }

    public Vector2f getCenter() {
        float x = hitbox.getGlobalBounds().left + hitbox.getGlobalBounds().width / 2;
        float y = hitbox.getGlobalBounds().top + hitbox.getGlobalBounds().height / 2;
        return new Vector2f(x, y);
    }

    public Vector2f getPosition() {
        return hitbox.getPosition();
    }

    public void draw(RenderWindow window) {
        window.draw(hitbox);
    }

    public GameObject getParent() {
        return parent;
    }
}