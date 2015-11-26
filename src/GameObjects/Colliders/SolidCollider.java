package GameObjects.Colliders;

import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 04/11/2015.
 */
public interface SolidCollider extends Collider {
    float getBreakForce();
}
