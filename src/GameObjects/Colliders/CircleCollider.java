package GameObjects.Colliders;

import Core.GlobalConstants;
import Core.VectorMath;
import GameObjects.GameObject;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
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
        hitbox.setFillColor(Color.TRANSPARENT);
        hitbox.setOutlineColor(Color.GREEN);
        hitbox.setOutlineThickness(-1.0f);
    }

    @Override
    public float getRadius() {
        return hitbox.getRadius();
    }

    public void setRadius(float radius) {
        hitbox.setRadius(radius);
    }

    @Override
    public void rescale(float size) {
        hitbox.setRadius(size / 2);
    }

    @Override
    public FloatRect getBounds() {
        return parent.getBounds();
    }

    @Override
    public float getMass() {
        return parent.getMass();
    }

    @Override
    public Vector2f getVelocity() {
        return parent.getVelocity();
    }

    @Override
    public void move(Vector2f offset) {
        parent.move(offset);
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        parent.setVelocity(velocity);
    }

    @Override
    public float getBreakForce() {
        //2D gravitational binding energy = (2/3) * Gm^2/r
        return (2f / 3f) * GlobalConstants.GRAVITATIONAL_CONSTANT * parent.getMass() * parent.getMass() / hitbox.getRadius();
    }

    @Override
    public void calculateCollision(Collider collider) {

        if (collider instanceof SolidCollider) {

            SolidCollider solidCollider = (SolidCollider) collider;

            float partialMass = 2 * solidCollider.getMass() / (getMass() + solidCollider.getMass());

            float dot = VectorMath.dot(Vector2f.sub(getVelocity(), solidCollider.getVelocity()), Vector2f.sub(getCenter(), solidCollider.getCenter()));
            float mag = VectorMath.magnitude(Vector2f.sub(getCenter(), solidCollider.getCenter()));

            Vector2f vel = Vector2f.sub(getVelocity(), Vector2f.mul(Vector2f.sub(getCenter(), solidCollider.getCenter()), partialMass * (dot / (mag * mag))));
            collisionVelocity = Vector2f.add(collisionVelocity, vel);


            //calculate offset to move object out of collision
            FloatRect intersect = getBounds().intersection(collider.getBounds());
            if (intersect != null) {
                if (intersect.width > intersect.height) {
                    if (intersect.contains(intersect.left, parent.getPosition().y)) {
                        //collision on the top side so move down
                        collisionOffset = new Vector2f(0, intersect.height / 2);
                    } else {
                        //collision on bottom side so move up
                        collisionOffset = new Vector2f(0, -intersect.height / 2);
                    }
                } else if (intersect.width < intersect.height) {
                    if (intersect.contains(parent.getPosition().x, intersect.top)) {
                        //left side so move right
                        collisionOffset = new Vector2f(intersect.width / 2, 0);
                    } else {
                        //right side so move left
                        collisionOffset = new Vector2f(-intersect.width / 2, 0);
                    }
                }
            }
        }

        //calculate the amount of kinetic energy the object will have after the collision
        float kE_after = 0.5f * getMass() * (float) Math.pow(VectorMath.magnitude(collisionVelocity), 2);

        //spend the waste energy from the collision on creating heat
        tempChange = parent.getTemperatureChange(kE_after * (1f - efficiency));

        //reduce the velocity proportionally to the energy lost
        //Kinetic energy E = 1/2 mv^2 => v = sqrt(2E/m)

        float velRemaining = (float) Math.sqrt((2.0f * kE_after * efficiency) / getMass());
        float fractionalVel = velRemaining / VectorMath.magnitude(collisionVelocity);

        collisionVelocity = Vector2f.mul(collisionVelocity, fractionalVel);
    }

    public void applyCollision() {
        move(collisionOffset);
        move(collisionVelocity);
        setVelocity(collisionVelocity);

        parent.setTemperature(parent.getTemperature() + tempChange);

        collisionVelocity = Vector2f.ZERO;
        collisionOffset = Vector2f.ZERO;
    }

    @Override
    public Vector2f getCenter() {
        return parent.getCenter();
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(hitbox);
    }

    @Override
    public void update() {
        hitbox.setPosition(parent.getPosition());
    }


    public GameObject getParent() {
        return parent;
    }
}
