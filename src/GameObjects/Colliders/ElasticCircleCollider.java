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
public class ElasticCircleCollider implements SolidCollider {

    GameObject parent;
    private CircleShape hitbox = new CircleShape();

    Vector2f collisionVelocity;
    float tempChange;

    public ElasticCircleCollider(GameObject parent) {
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

        if(collider instanceof SolidCollider) {

            SolidCollider solidCollider = (SolidCollider) collider;

            float partialMass = 2 * solidCollider.getMass() / (getMass() + solidCollider.getMass());

            float dot = VectorMath.dot(Vector2f.sub(getVelocity(), solidCollider.getVelocity()), Vector2f.sub(getCenter(), solidCollider.getCenter()));
            float mag = VectorMath.magnitude(Vector2f.sub(getCenter(), solidCollider.getCenter()));

            collisionVelocity = Vector2f.sub(getVelocity(), Vector2f.mul(Vector2f.sub(getCenter(), solidCollider.getCenter()), partialMass * (dot / (mag * mag))));
        } else if(collider instanceof DiffuseCollider){

        }
    }

    public void applyCollision(){
        move(Vector2f.mul(collisionVelocity, 1.05f));
        setVelocity(collisionVelocity);
        parent.setTemperature(parent.getTemperature() + tempChange);
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
