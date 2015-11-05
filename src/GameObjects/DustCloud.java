package GameObjects;

import Core.VectorMath;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.DiffuseCollider;
import GameObjects.Colliders.GasCloudCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Matthew on 04/11/2015.
 */
public class DustCloud implements GameObject {

    private ConvexShape shape;
    private float density = 1f;
    private float temperature = 200f;
    private float mass;
    private float heatCapacity = 2.0f; //estimate

    private Vector2f velocity = Vector2f.ZERO;
    private Vector2f appliedForce = Vector2f.ZERO;

    DiffuseCollider collider;

    public DustCloud(float mass, Vector2f position) {

        this.mass = mass;
        shape = new ConvexShape(generateConvexPoints(50, 20.0f));
        shape.setFillColor(Color.MAGENTA);
        setPosition(position);
        collider = new GasCloudCollider(this, shape.getPoints());
    }

    @Override
    public void update(float dt) {

        //get the direction and size of the applied force
        Vector2f dir = VectorMath.normalize(appliedForce);
        float F = VectorMath.magnitude(appliedForce);

        //calculate the acceleration this frame and add it to the current velocity of the particle
        //F = ma
        Vector2f a = Vector2f.mul(dir, (F / mass) * dt);
        velocity = Vector2f.add(velocity, a);

        move(Vector2f.mul(velocity, dt));
        appliedForce = Vector2f.ZERO;

        collider.update();
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(shape);
    }

    private Vector2f[] generateConvexPoints(int n, float maxRadius) {
        Vector2f[] points = new Vector2f[n];

        for (int i = 0; i < n; i++) {
            float angle = (float) Math.random() * 2f * (float) Math.PI;
            float r = (float) Math.random() * maxRadius;

            float x = r * (float) Math.cos(angle);
            float y = r * (float) Math.sin(angle);

            points[i] = new Vector2f(x, y);
        }
        return points;
    }

    @Override
    public Vector2f getPosition() {
        return new Vector2f(shape.getGlobalBounds().left, shape.getGlobalBounds().top);
    }

    @Override
    public void setPosition(Vector2f position) {
        shape.setPosition(position);
    }

    @Override
    public Vector2f getCenter() {
        //this returns the center of the bounding box, not the center of mass
        float x = shape.getGlobalBounds().left + shape.getGlobalBounds().width / 2;
        float y = shape.getGlobalBounds().top + shape.getGlobalBounds().height / 2;

        return new Vector2f(x, y);
    }

    @Override
    public FloatRect getBounds() {
        return shape.getGlobalBounds();
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(shape.getGlobalBounds().width, shape.getGlobalBounds().height);
    }

    @Override
    public void move(Vector2f offset) {
        shape.move(offset);
    }

    @Override
    public void setFillColor(Color c) {
        shape.setFillColor(c);
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
    public float getArea() {
        float area = 0;

        int j = shape.getPointCount() - 1;
        for (int i = 0; i < shape.getPointCount(); i++) {
            area += (shape.getPoint(j).x + shape.getPoint(i).x) * (shape.getPoint(j).y - shape.getPoint(i).y);
            j = i;
        }

        return area / 2;
    }

    @Override
    public float getDensity() {
        return density;
    }

    @Override
    public float getTemperatureChange(float energy) {
        return energy / (mass * heatCapacity);
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
    public Collider getCollider() {
        return collider;
    }
}
