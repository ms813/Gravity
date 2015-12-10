package GameObjects.Colliders;

import GameObjects.GameObject;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;

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
    public void calculateCollision(GameObject object) {
        //deliberately blank
    }

    @Override
    public void applyCollision() {
        //deliberately blank
    }

    @Override
    public void update() {
        hitbox.setPosition(parent.getPosition());
    }
}
