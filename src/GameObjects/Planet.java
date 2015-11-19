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
 * Created by smithma on 17/11/15.
 */
public class Planet extends GameObject {

    public Planet(float mass, Vector2f position) {
        this.mass = mass;
        this.temperature = 200;
        this.heatCapacity = 1;
        this.density = 10;

        float radius = (float) Math.sqrt(this.mass / (Math.PI * density));

        sprite = new Sprite();
        sprite.setPosition(position);

        collider = new CircleCollider(this, 1.0f);
    }

    @Override
    public void update(float dt) {
        if (active) {

            velocity = getUpdatedVelocity(dt);
            appliedForce = Vector2f.ZERO;
            move(velocity);

            for (GameObject satellite : children) {
                satellite.update(dt);
            }
        }
    }


    public void addSatellite(GameObject satellite) {
        if (!children.contains(satellite)) {
            children.add(satellite);
        } else {
            System.out.println("[Planet.addSatellite()] This satellite already belongs to this body");
        }
    }
}
