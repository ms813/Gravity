package Components;

import Core.VectorMath;
import GameObjects.Entity;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.json.simple.JSONObject;

/**
 * Created by smithma on 04/11/15.
 */
public class CircleCollider extends Components.Collider {

    /*
    *   1.0 is perfectly elastic (all velocity maintained after collision, no heat increase)
    *   0.0 is perfectly inelastic (no velocity maintained after collision, all energy converted to temperature increase)
    */

    @Override
    public void initialise(JSONObject attributes, Entity owner) {
        super.initialise(attributes, owner);
        efficiency = ((Long) attributes.get("efficiency")).floatValue();

        ((CircleShape) hitbox).setRadius((owner.getSize().x + owner.getSize().y) / 4);
        hitbox.setPosition(owner.getPosition());
        hitbox.setFillColor(Color.TRANSPARENT);
        hitbox.setOutlineColor(Color.CYAN);
        hitbox.setOutlineThickness(-1.0f);
    }

    @Override
    public boolean isColliding(Components.Collider collider) {
        if (collider instanceof CircleCollider) {
            CircleCollider col = (CircleCollider) collider;

            float dist = VectorMath.magnitude(Vector2f.sub(this.getCenter(), col.getCenter()));

            return dist < (this.getRadius() + col.getRadius());
        } else {
            return owner.getBounds().intersection(collider.getBounds()) != null;
        }
    }



    @Override
    public CollisionEvent createCollisionEvent(Components.Collider collider) {

        Vector2f collisionOffset = Vector2f.ZERO;
        Vector2f collisionVelocity = Vector2f.ZERO;
        float temperatureChange = 0;

        //move objects out of collision
        //only move the less massive of the two colliders
        if (this.mass <= collider.mass) {
            if (collider instanceof CircleCollider) {
                CircleCollider col = (CircleCollider) collider;
                Vector2f dif = Vector2f.sub(col.getCenter(), this.getCenter());
                Vector2f dir = VectorMath.normalize(dif);
                float dist = VectorMath.magnitude(dif);
                float overlap = this.getRadius() + col.getRadius() - dist;

                collisionOffset = Vector2f.mul(dir, overlap * -1.01f);

            } else {

                //calculate offset to move object out of collision
                FloatRect intersect = this.getBounds().intersection(collider.getBounds());
                if (intersect != null) {
                    if (intersect.width > intersect.height) {
                        if (intersect.contains(intersect.left, hitbox.getPosition().y)) {
                            //collision on the top side so move down
                            collisionOffset = new Vector2f(0, intersect.height / 2 + 1);
                        } else {
                            //collision on bottom side so move up
                            collisionOffset = new Vector2f(0, -intersect.height / 2 - 1);
                        }
                    } else /*if (intersect.width < intersect.height)*/ {
                        if (intersect.contains(hitbox.getPosition().x, intersect.top)) {
                            //left side so move right
                            collisionOffset = new Vector2f(intersect.width / 2 + 1, 0);
                        } else {
                            //right side so move left
                            collisionOffset = new Vector2f(-intersect.width / 2 - 1, 0);
                        }
                    }
                }
            }
        }

        //calculate velocities after collision
        float partialMass = 2 * collider.mass / (this.mass + collider.mass);

        float dot = VectorMath.dot(Vector2f.sub(this.velocity, collider.velocity), Vector2f.sub(owner.getCenter(), collider.getCenter()));
        float mag = VectorMath.magnitude(Vector2f.sub(owner.getCenter(), collider.getCenter()));

        Vector2f vel = Vector2f.sub(this.velocity, Vector2f.mul(Vector2f.sub(owner.getCenter(), collider.getCenter()), partialMass * (dot / (mag * mag))));
        collisionVelocity = Vector2f.add(collisionVelocity, vel);

        //calculate the amount of kinetic energy the object will have after the collision
        //Kinetic energy E = 1/2 mv^2
        float kE_after = 0.5f * this.mass* (float) Math.pow(VectorMath.magnitude(collisionVelocity), 2);

        //spend the waste energy from the collision on creating heat
        //temperatureChange = owner.getTemperatureChange(kE_after * (1f - efficiency));

        //reduce the velocity proportionally to the energy lost
        //Kinetic energy E = 1/2 mv^2 => v = sqrt(2E/m)

        float velRemaining = (float) Math.sqrt((2.0f * kE_after * efficiency) / this.mass);
        float fractionalVel = velRemaining / VectorMath.magnitude(collisionVelocity);

        collisionVelocity = Vector2f.mul(collisionVelocity, fractionalVel);

        return new CollisionEvent(collider, collisionVelocity, collisionOffset, temperatureChange);
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(hitbox);
    }

    @Override
    public void update(float dt, boolean VERLET_STATE) {
        ((CircleShape) hitbox).setRadius((owner.getSize().x + owner.getSize().y) / 4);
        hitbox.setPosition(owner.getCenter().x - getRadius(), owner.getCenter().y - getRadius());
    }

    public float getRadius() {
        return ((CircleShape) hitbox).getRadius();
    }

    @Override
    public Vector2f getCenter() {
        return new Vector2f(hitbox.getGlobalBounds().left + hitbox.getGlobalBounds().width / 2, hitbox.getGlobalBounds().top + hitbox.getGlobalBounds().height / 2);
    }

    @Override
    public Vector2f getPosition() {
        return hitbox.getPosition();
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(hitbox.getGlobalBounds().width, hitbox.getGlobalBounds().height);
    }

    @Override
    public FloatRect getBounds() {
        return hitbox.getGlobalBounds();
    }

    public Entity getOwner() {
        return owner;
    }
}
