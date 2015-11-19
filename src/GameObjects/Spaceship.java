package GameObjects;

import Core.Game;
import Core.GlobalConstants;
import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.SolidCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 18/11/15.
 */
public class Spaceship extends GameObject {

    private Thruster thruster;

    public Spaceship(Vector2f position) {

        this.mass = 10000;
        this.temperature = 200;
        this.heatCapacity = 1;
        this.density = 10;
        
        setPosition(position);

        collider = new CircleCollider(this, 1);
        thruster = new Thruster();
        thruster.setRotation(Math.toRadians(sprite.getRotation()));
    }

    @Override
    public void update(float dt) {
        if (active) {
            appliedForce = Vector2f.add(appliedForce, thruster.getThrustVector());
            velocity = getUpdatedVelocity(dt);
            appliedForce = Vector2f.ZERO;
            move(velocity);
        }
    }
}
