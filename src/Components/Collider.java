package Components;

import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shape;
import org.jsfml.system.Vector2f;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by smithma on 04/11/15.
 */
public abstract class Collider extends Component {

    protected float mass = 1;
    protected float density = 1;
    protected float heatCapacity = 1;
    protected float temperature = 1;

    protected float efficiency = 1;

    protected Vector2f velocity = Vector2f.ZERO;

    protected Queue<CollisionEvent> collisionEvents = new PriorityQueue<>();

    protected Shape hitbox = new CircleShape();

    public abstract boolean isColliding(Collider collider);

    public abstract CollisionEvent createCollisionEvent(Collider collider);

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

    @Override
    public void draw(RenderWindow window) {
        window.draw(hitbox);
    }

    public void applyCollisions(){
        //TODO apply collisions
    }

    public float getMass(){
        return mass;
    }
}