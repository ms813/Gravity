package GameObjects.Colliders;

import Core.VectorMath;
import GameObjects.GameObject;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import sun.util.resources.cldr.ve.CalendarData_ve_ZA;

/**
 * Created by Matthew on 04/11/2015.
 */
public class GasCloudCollider implements DiffuseCollider {

    private GameObject parent;
    private Vector2f collisionVelocity = Vector2f.ZERO;
    private Shape hitbox;
    private float viscosity;


    public GasCloudCollider(GameObject parent, Vector2f[] hitboxPoints){
        this.parent = parent;
        VertexArray v = new VertexArray();
        hitbox = new ConvexShape(hitboxPoints);

        viscosity = parent.getDensity() * 1.0f;
    }

    public float getViscosity(){
        return viscosity;
    }

    @Override
    public void calculateCollision(Collider collider) {
        //F = 6 *pi * viscosity * radius of object passing through * relative velocty

        if(collider instanceof SolidCollider) {

            SolidCollider solidCollider = (SolidCollider) collider;

            float relativeVel = VectorMath.magnitude(Vector2f.sub(solidCollider.getVelocity(), this.getVelocity()));

            float force = 6 * (float) Math.PI * parent.getDensity() * solidCollider.getRadius() * relativeVel;

            collisionVelocity = Vector2f.mul(VectorMath.normalize(solidCollider.getVelocity()), force);

        } else if(collider instanceof DiffuseCollider){

        }

    }

    @Override
    public void applyCollision() {
        setVelocity(Vector2f.add(getVelocity(), collisionVelocity));
    }

    @Override
    public void rescale(float size) {

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
    public void draw(RenderWindow window) {

    }

    @Override
    public void update() {
        //move hitbox to match parent shape;
        hitbox.setPosition(parent.getPosition());
    }

    @Override
    public GameObject getParent() {
        return parent;
    }

    @Override
    public FloatRect getBounds(){
        return parent.getBounds();
    }
}
