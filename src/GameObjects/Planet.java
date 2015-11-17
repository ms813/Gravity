package GameObjects;

import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.SolidCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 17/11/15.
 */
public class Planet implements GameObject {

    private boolean visible = true;
    private boolean active = true;

    //private Sprite sprite;
    private CircleShape sprite;

    private Vector2f velocity = Vector2f.ZERO;
    private float mass;
    private float density = 5.0f;
    private float temperature = 200;
    private Vector2f appliedForce = Vector2f.ZERO;

    private CircleCollider collider;

    private List<GameObject> satellites = new ArrayList<>();

    public Planet(float mass, Vector2f position) {
        this.mass = mass;
        float radius = (float) Math.sqrt(mass / (Math.PI * density));

        sprite = new CircleShape(radius);
        sprite.setPosition(position);
        sprite.setOutlineThickness(-1.0f);
        sprite.setOutlineColor(Color.GREEN);

        collider = new CircleCollider(this, 1.0f);
    }

    @Override
    public void update(float dt) {
        if (active) {
            velocity = getUpdatedVelocity(dt);

            //move the particle according to its current velocity, and reset the applied force to zero
            move(velocity);
            collider.update();
            appliedForce = Vector2f.ZERO;

            for(GameObject satellite : satellites){
                satellite.update(dt);
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
        if(visible){
            window.draw(sprite);

            for(GameObject satellite : satellites){
                satellite.draw(window);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;

        for(GameObject o : satellites){
            o.setVisible(visible);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;

        for(GameObject o : satellites){
            o.setActive(active);
        }
    }

    @Override
    public List<GameObject> getChildren() {
        return satellites;
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
        //TODO implement planet temperature change
        return temperature;
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

    public void addSatellite(GameObject satellite){
        if(!satellites.contains(satellite)){
            satellites.add(satellite);
        } else{
            System.out.println("[Planet.addSatellite()] This satellite already belongs to this body");
        }
    }
}
