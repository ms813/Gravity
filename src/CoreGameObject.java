import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 26/10/2015.
 */
public abstract class CoreGameObject implements iCoreGameObject {

    protected Sprite sprite;
    protected Shape shape = new CircleShape();

    protected Vector2f velocity = Vector2f.ZERO;
    protected Vector2f appliedForce = Vector2f.ZERO;

    protected float mass;

    public void draw(RenderWindow window) {
        if (sprite != null) {
            window.draw(sprite);
        } else {
            window.draw(shape);
        }
    }

    public Vector2f getPosition() {
        if (sprite != null) {
            return sprite.getPosition();
        } else {
            return shape.getPosition();
        }
    }

    public IntRect getBounds() {
        if (sprite != null) {
            return new IntRect(sprite.getGlobalBounds());
        } else {
            return new IntRect(shape.getGlobalBounds());
        }
    }

    public Vector2f getSize() {
        if (sprite != null) {
            return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
        } else {
            return new Vector2f(shape.getGlobalBounds().width, shape.getGlobalBounds().height);
        }
    }
}
