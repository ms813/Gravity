package GameObjects.Colliders;

import Core.VectorMath;
import GameObjects.GameObject;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 04/11/2015.
 */
public class InelasticCircleCollider extends ElasticCircleCollider {

    //fraction of energy kept as kinetic energy after collision
    float efficiency = 0.95f;

    public InelasticCircleCollider(GameObject parent) {
        super(parent);
    }

    @Override
    public void calculateCollision(Collider collider) {

        super.calculateCollision(collider);

        float kE_after = 0.5f * getMass() * (float) Math.pow(VectorMath.magnitude(getVelocity()), 2);

        //spend the waste energy from the collision on creating heat
        tempChange = parent.getTemperatureChange(kE_after * (1f - efficiency));

        //reduce the post-collision velocity by the efficiency factor
        collisionVelocity = Vector2f.mul(collisionVelocity, efficiency);
    }
}
