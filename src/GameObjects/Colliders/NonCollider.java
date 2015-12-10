package GameObjects.Colliders;

import GameObjects.GameObject;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 10/12/2015.
 */
public class NonCollider extends Collider {

    /*
    *   This collider should be used for objects that do not
    *   exhibit collision behaviour
    */


    public NonCollider(GameObject parent) {
        super(parent);
        hitbox = new CircleShape(5);
        hitbox.setOutlineColor(Color.RED);
        hitbox.setOutlineThickness(-1f);
        hitbox.setFillColor(Color.TRANSPARENT);
    }

    @Override
    public boolean isColliding(GameObject object) {
        return false;
    }

    @Override
    public CollisionEvent createCollisionEvent(GameObject object) {
        //deliberately blank
        return new CollisionEvent(object, Vector2f.ZERO, Vector2f.ZERO, 0);
    }


    @Override
    public void update() {
        hitbox.setPosition(parent.getPosition());
    }
}
