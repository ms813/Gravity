package GameObjects.Colliders;

import GameObjects.GameObject;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 10/12/2015.
 */
public class CollisionEvent {

    private GameObject collidingObject;
    private Vector2f collisionVelocity = Vector2f.ZERO;
    private Vector2f collisionOffset = Vector2f.ZERO;
    private float temperatureChange;

    public CollisionEvent(GameObject collidingObject, Vector2f velocityChange, Vector2f collisionOffset, float temperatureChange){
        this.collidingObject = collidingObject;
        this.collisionVelocity = velocityChange;
        this.collisionOffset = collisionOffset;
        this.temperatureChange = temperatureChange;
    }

    public GameObject getCollidingObject() {
        return collidingObject;
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
