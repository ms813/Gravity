package GameObjects.Colliders;

import Core.GlobalConstants;
import Core.VectorMath;
import GameObjects.GameObject;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 04/11/15.
 */
public class RockyCollider implements Collider {

    GameObject parent;
    private CircleShape hitbox = new CircleShape();

    Vector2f collisionVelocity;

    public RockyCollider(GameObject parent) {
        this.parent = parent;

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

        float partialMass = 2 * collider.getMass() / (getMass() + collider.getMass());

        float dot = VectorMath.dot(Vector2f.sub(getVelocity(), collider.getVelocity()), Vector2f.sub(getCenter(), collider.getCenter()));
        float mag = VectorMath.magnitude(Vector2f.sub(getCenter(), collider.getCenter()));

        collisionVelocity = Vector2f.sub(getVelocity(), Vector2f.mul(Vector2f.sub(getCenter(), collider.getCenter()), partialMass * (dot / (mag * mag))));
    }

    public void applyCollision(){
        move(collisionVelocity);
        setVelocity(collisionVelocity);
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
