package GameObjects;

import Core.TextureManager;
import GameObjects.Colliders.CircleCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

/**
 * Created by smithma on 17/11/15.
 */
public class Planet extends GameObject {

    public Planet(float mass, Vector2f position) {
        this.mass = mass;
        this.temperature = 200;
        this.heatCapacity = 1;
        this.density = 10000;

        float radius = (float) Math.sqrt(this.mass / (Math.PI * density));

        texture = TextureManager.getTexture("earth.png");
        sprite.setTexture(texture);
        sprite.setTextureRect(new IntRect(Vector2i.ZERO, texture.getSize()));

        sprite.setScale(2 * radius / sprite.getTextureRect().width, 2 * radius / sprite.getTextureRect().height);
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
