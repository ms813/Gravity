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
public class Spaceship implements GameObject {

    private boolean visible = true;
    private boolean active = true;

    private float mass = 10000;
    private float temperature = 273;

    private Vector2f appliedForce = Vector2f.ZERO;
    private Vector2f velocity = Vector2f.ZERO;

    private RectangleShape sprite = new RectangleShape();

    private Collider collider;

    private Thruster thruster;

    public Spaceship(Vector2f position) {
        setPosition(position);
        sprite.setSize(new Vector2f(1, 1));
        sprite.setFillColor(Color.RED);

        collider = new CircleCollider(this, 1);
        thruster = new Thruster();
        thruster.setRotation(Math.toRadians(sprite.getRotation()));
    }

    @Override
    public void update(float dt) {
        if (active) {
            if (Game.leapfrogStep) {
                appliedForce = Vector2f.add(appliedForce, thruster.getThrustVector());
                velocity = getUpdatedVelocity(dt);
                appliedForce = Vector2f.ZERO;
            } else {
                move(velocity);
            }
        }
    }

    private Vector2f getUpdatedVelocity(float dt) {
        //get the direction and size of the applied force

        Vector2f dir = VectorMath.normalize(appliedForce);
        float F = VectorMath.magnitude(appliedForce);

        //calculate the acceleration this frame and add it to the current velocity of the particle
        //F = ma
        Vector2f a = Vector2f.mul(dir, (F / mass) * dt);
        return Vector2f.add(velocity, a);
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(sprite);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public List<GameObject> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public Vector2f getPosition() {
        return sprite.getPosition();
    }

    @Override
    public void setPosition(Vector2f position) {
        sprite.setPosition(position);
    }

    @Override
    public Vector2f getCenter() {
        float x = sprite.getGlobalBounds().left + sprite.getGlobalBounds().width / 2;
        float y = sprite.getGlobalBounds().top + sprite.getGlobalBounds().height / 2;
        return new Vector2f(x, y);
    }

    @Override
    public FloatRect getBounds() {
        return sprite.getGlobalBounds();
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
    }

    @Override
    public void move(Vector2f offset) {
        sprite.move(offset);
        collider.update();
    }

    @Override
    public boolean isSolid() {
        return collider instanceof SolidCollider;
    }

    @Override
    public void applyForce(Vector2f force) {
        appliedForce = force;
    }

    @Override
    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public float getTemperatureChange(float energy) {
        //TODO implement spaceship temperature change
        return 0;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public boolean isColliding(GameObject object) {
        return collider.isColliding(object);
    }

    @Override
    public void calculateCollision(GameObject object) {
        collider.calculateCollision(object);
    }

    @Override
    public void applyCollision() {
        collider.applyCollision();
    }

    @Override
    public Collider getCollider() {
        return collider;
    }
}
