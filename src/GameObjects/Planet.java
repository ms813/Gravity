package GameObjects;

import GameObjects.Colliders.CircleCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

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

    public void addSatellite(GameObject satellite) {
        if (!children.contains(satellite)) {
            children.add(satellite);
        } else {
            System.out.println("[Planet.addSatellite()] This satellite already belongs to this body");
        }
    }
}
