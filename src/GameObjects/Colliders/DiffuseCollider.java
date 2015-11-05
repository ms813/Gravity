package GameObjects.Colliders;

import org.jsfml.graphics.Shape;

/**
 * Created by Matthew on 04/11/2015.
 */
public interface DiffuseCollider extends Collider {
    float getDrag(Collider collider);
    float getDensity();
    float getViscosity();
}
