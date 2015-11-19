package GameObjects;

import Core.TextureManager;
import GameObjects.Colliders.CircleCollider;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

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

        texture = TextureManager.getTexture("spaceship.png");
        sprite.setTexture(texture);

        float radius = (float) Math.sqrt(this.mass / (Math.PI * density));
        sprite.setScale(2 * radius / sprite.getGlobalBounds().width, 2 * radius / sprite.getGlobalBounds().height);

        setPosition(position);

        collider = new CircleCollider(this, 1);
        thruster = new Thruster();
        thruster.setRotation(Math.toRadians(sprite.getRotation()));
    }

    @Override
    public void updateVelocity(float dt) {
        if (active) {
            appliedForce = Vector2f.add(appliedForce, thruster.getThrustVector());
        }
        super.updateVelocity(dt);
        System.out.println(velocity);
    }
}
