package Components;

import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 10/12/2015.
 */
public class CollisionEvent {

    private Collider thatCollider;
    private Vector2f collisionVelocity = Vector2f.ZERO;
    private Vector2f collisionOffset = Vector2f.ZERO;
    private float temperatureChange;

    public CollisionEvent(Collider thatCollider, Vector2f velocityChange, Vector2f collisionOffset, float temperatureChange){
        this.thatCollider = thatCollider;
        this.collisionVelocity = velocityChange;
        this.collisionOffset = collisionOffset;
        this.temperatureChange = temperatureChange;
    }

    public Collider getThatCollider() {
        return thatCollider;
    }

    public Vector2f getCollisionOffset() {
        return collisionOffset;
    }

    public float getTemperatureChange() {
        return temperatureChange;
    }

    public Vector2f getCollisionVelocity() {
        return collisionVelocity;
    }
}
