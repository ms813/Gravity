package GameObjects;

import Core.VectorMath;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.SolidCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 28/10/15.
 */
public abstract class GameObject {

    protected boolean visible = true;
    protected boolean active = true;

    protected Sprite sprite = new Sprite();
    protected Texture texture = new Texture();

    protected Collider collider;

    protected List<GameObject> children = new ArrayList<>();

    protected Vector2f appliedForce = Vector2f.ZERO;
    protected Vector2f velocity = Vector2f.ZERO;

    protected float mass;
    protected float temperature;
    protected float heatCapacity;
    protected float density;

    private boolean HITBOX_VISIBLE = true;

    /*
        Core
    */
    public abstract void update(float dt);

    public void draw(RenderWindow window) {
        if (visible && active) {
            window.draw(sprite);
            if(HITBOX_VISIBLE) collider.draw(window);
            for (GameObject child : getChildren()) {
                child.draw(window);
            }
        }
    }

    protected Vector2f getUpdatedVelocity(float dt) {
        //get the direction and size of the applied force
        Vector2f dir = VectorMath.normalize(appliedForce);
        float F = VectorMath.magnitude(appliedForce);

        //calculate the acceleration this frame and add it to the current velocity of the particle
        //F = ma
        Vector2f a = Vector2f.mul(dir, (F / mass) * dt);
        return Vector2f.add(velocity, a);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        for (GameObject o : children) {
            o.setVisible(visible);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        for (GameObject o : children) {
            o.setActive(active);
        }
    }

    public List<GameObject> getChildren() {
        return children;
    }

    /*
        Basic shape manipulation
    */
    public Vector2f getPosition() {
        return sprite.getPosition();
    }

    public void setPosition(Vector2f position) {
        sprite.setPosition(position);
    }

    public Vector2f getCenter() {
        float x = sprite.getGlobalBounds().left + sprite.getGlobalBounds().width / 2;
        float y = sprite.getGlobalBounds().top + sprite.getGlobalBounds().height / 2;
        return new Vector2f(x, y);
    }

    public FloatRect getBounds() {
        return sprite.getGlobalBounds();
    }

    public Vector2f getSize() {
        return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
    }

    public void move(Vector2f offset) {
        sprite.move(offset);
        collider.update();
    }

    /*
       Physics
    */
    public boolean isSolid() {
        return collider instanceof SolidCollider;
    }

    public void applyForce(Vector2f force) {
        appliedForce = Vector2f.add(appliedForce, force);
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public float getMass() {
        return mass;
    }

    public float getTemperatureChange(float energy) {
        return energy / (mass * heatCapacity);
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isColliding(GameObject object){
        return collider.isColliding(object);
    }

    public void calculateCollision(GameObject object){
        collider.calculateCollision(object);
    }


    public void applyCollision(){
        collider.applyCollision();
    }

    public Collider getCollider(){
        return collider;
    }
}
