package GameObjects;

import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.CollisionEvent;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by smithma on 28/10/15.
 */
public abstract class GameObject {

    protected boolean visible = true;
    protected boolean active = true;

    protected Sprite sprite = new Sprite();
    protected Texture texture;

    protected Collider collider = new CircleCollider(this, 1);
    protected Queue<CollisionEvent> collisionEvents = new PriorityQueue<>();

    protected List<GameObject> children = new ArrayList<>();

    protected Vector2f appliedForce = Vector2f.ZERO;
    protected Vector2f velocity = Vector2f.ZERO;

    protected float mass = 1f;
    protected float temperature = 1f;
    protected float heatCapacity = 1f;
    protected float density = 1f;

    private boolean HITBOX_VISIBLE = true;

    private boolean destroyOnHit = false;

    private boolean destroyFlag = false;

    /*
        Core
    */

    //Verlet method requires updating position and velocity in separate steps
    //http://gamedev.stackexchange.com/questions/15708/how-can-i-implement-gravity
    private Vector2f acceleration = Vector2f.ZERO;

    public void update(float dt, boolean VERLET_STATE) {

        //velocity Verlet requires all of the positions to be updated first, then the velocities
        if (VERLET_STATE) {
            updatePosition(dt);
        } else {
            updateVelocity(dt);
        }
    }

    private void updatePosition(float dt) {
        acceleration = getAcceleration();
        //position  += timestep * (velocity + timestep * acceleration / 2)
        move(Vector2f.mul(Vector2f.add(velocity, Vector2f.mul(acceleration, dt / 2)), dt));

        for (GameObject child : children) {
            child.updatePosition(dt);
        }
    }

    private void updateVelocity(float dt) {
        Vector2f newAcceleration = getAcceleration();
        velocity = Vector2f.add(velocity, Vector2f.mul(Vector2f.add(acceleration, newAcceleration), dt / 2));
        appliedForce = Vector2f.ZERO;

        for (GameObject child : children) {
            child.updateVelocity(dt);
        }
        acceleration = newAcceleration;
    }

    public void draw(RenderWindow window) {
        if (visible && active && sprite != null) {
            window.draw(sprite);
            if (HITBOX_VISIBLE) collider.draw(window);
            for (GameObject child : getChildren()) {
                child.draw(window);
            }
        }
    }

    protected Vector2f getAcceleration() {
        //get the direction and size of the applied force
        Vector2f dir = VectorMath.normalize(appliedForce);
        float F = VectorMath.magnitude(appliedForce);

        //calculate the acceleration this frame and add it to the current velocity of the particle
        //F = ma => a = F/m
        return Vector2f.mul(dir, F / mass);
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
        return collider instanceof CircleCollider;
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

    public boolean isColliding(GameObject object) {
        return collider.isColliding(object);
    }

    public void addCollisionEvent(GameObject object) {
        collisionEvents.add(collider.createCollisionEvent(object));
    }

    public void applyCollisions() {
        while(!collisionEvents.isEmpty()){
            CollisionEvent e = collisionEvents.poll();

            move(e.getCollisionOffset());
            setVelocity(e.getCollisionVelocity());
            temperature += e.getTemperatureChange();
        }
    }

    public Collider getCollider() {
        return collider;
    }

    public boolean hasWeapons() {
        return false;
    }

    public boolean isDestroyOnHit() {
        return destroyOnHit;
    }

    public void setDestroyOnHit(boolean bool) {
        destroyOnHit = bool;
    }

    public void setDestroyFlag(boolean flag) {
        destroyFlag = flag;
    }

    public boolean getDestroyFlag(){
        return destroyFlag;
    }

}
