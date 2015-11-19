package GameObjects.Colliders;

import Core.GlobalConstants;
import Core.VectorMath;
import GameObjects.GameObject;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 04/11/15.
 */
public class CircleCollider implements SolidCollider {

    GameObject parent;

    /*
    *   1.0 is perfectly elastic (all velocity maintained after collision, no heat increase)
    *   0.0 is perfectly inelastic (no velocity maintained after collision, all energy converted to temperature increase)
    */
    float efficiency = 1.0f;

    private CircleShape hitbox = new CircleShape();

    Vector2f collisionVelocity = Vector2f.ZERO;
    Vector2f collisionOffset = Vector2f.ZERO;
    float tempChange;

    public CircleCollider(GameObject parent, float efficiency) {
        this.parent = parent;
        this.efficiency = efficiency;
        hitbox.setRadius((parent.getSize().x + parent.getSize().y) / 4);
        hitbox.setPosition(parent.getPosition());
        hitbox.setFillColor(Color.TRANSPARENT);
        hitbox.setOutlineColor(Color.CYAN);
        hitbox.setOutlineThickness(-1.0f);
    }

    @Override
    public float getBreakForce() {
        //2D gravitational binding energy = (2/3) * Gm^2/r
        return (2f / 3f) * GlobalConstants.GRAVITATIONAL_CONSTANT * parent.getMass() * parent.getMass() / hitbox.getGlobalBounds().width / 2;
    }

    @Override
    public boolean isColliding(GameObject obj) {
        if (obj.getCollider() instanceof CircleCollider) {
            CircleCollider col = (CircleCollider) obj.getCollider();

            float dist = VectorMath.magnitude(Vector2f.sub(this.getCenter(), col.getCenter()));

            return dist < (this.getRadius() + col.getRadius());
        } else {
            return obj.isSolid() && parent.getBounds().intersection(obj.getBounds()) != null;
        }
    }

    @Override
    public void calculateCollision(GameObject object) {

        if (object.isSolid()) {

            float partialMass = 2 * object.getMass() / (parent.getMass() + object.getMass());

            float dot = VectorMath.dot(Vector2f.sub(parent.getVelocity(), object.getVelocity()), Vector2f.sub(parent.getCenter(), object.getCenter()));
            float mag = VectorMath.magnitude(Vector2f.sub(parent.getCenter(), object.getCenter()));

            Vector2f vel = Vector2f.sub(parent.getVelocity(), Vector2f.mul(Vector2f.sub(parent.getCenter(), object.getCenter()), partialMass * (dot / (mag * mag))));
            collisionVelocity = Vector2f.add(collisionVelocity, vel);

            if (object.getCollider() instanceof CircleCollider) {
                CircleCollider col = (CircleCollider) object.getCollider();
                Vector2f dif = Vector2f.sub(col.getCenter(), this.getCenter());
                Vector2f dir = VectorMath.normalize(dif);
                float dist = VectorMath.magnitude(dif);
                float overlap = this.getRadius() + col.getRadius() - dist;

                collisionOffset = Vector2f.mul(dir, overlap*-1.01f);

            } else {

                //calculate offset to move object out of collision
                FloatRect intersect = this.getBounds().intersection(object.getCollider().getBounds());
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

        //calculate the amount of kinetic energy the object will have after the collision
        //Kinetic energy E = 1/2 mv^2
        float kE_after = 0.5f * parent.getMass() * (float) Math.pow(VectorMath.magnitude(collisionVelocity), 2);

        //spend the waste energy from the collision on creating heat
        tempChange = parent.getTemperatureChange(kE_after * (1f - efficiency));

        //reduce the velocity proportionally to the energy lost
        //Kinetic energy E = 1/2 mv^2 => v = sqrt(2E/m)

        float velRemaining = (float) Math.sqrt((2.0f * kE_after * efficiency) / parent.getMass());
        float fractionalVel = velRemaining / VectorMath.magnitude(collisionVelocity);

        collisionVelocity = Vector2f.mul(collisionVelocity, fractionalVel);


    }

    @Override
    public void applyCollision() {
        parent.move(collisionOffset);
        parent.move(collisionVelocity);
        parent.setVelocity(collisionVelocity);

        parent.setTemperature(parent.getTemperature() + tempChange);

        collisionVelocity = Vector2f.ZERO;
        collisionOffset = Vector2f.ZERO;
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(hitbox);
    }

    @Override
    public void update() {
        hitbox.setRadius((parent.getSize().x + parent.getSize().y) / 4);
        hitbox.setPosition(parent.getCenter().x  - getRadius(), parent.getCenter().y - getRadius());
    }

    public float getRadius() {
        return hitbox.getRadius();
    }

    @Override
    public Vector2f getCenter() {
        return new Vector2f(hitbox.getGlobalBounds().left + hitbox.getGlobalBounds().width / 2, hitbox.getGlobalBounds().top + hitbox.getGlobalBounds().height / 2);
    }

    @Override
    public Vector2f getPosition(){
        return hitbox.getPosition();
    }

    @Override
    public Vector2f getSize(){
        return new Vector2f(hitbox.getGlobalBounds().width, hitbox.getGlobalBounds().height);
    }

    @Override
    public FloatRect getBounds() {
        return hitbox.getGlobalBounds();
    }

    public GameObject getParent() {
        return parent;
    }
}
