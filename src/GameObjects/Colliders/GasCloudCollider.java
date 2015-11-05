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
    private Vector2f collisionForce = Vector2f.ZERO;
    private Shape hitbox;
    private float viscosity;


    public GasCloudCollider(GameObject parent, Vector2f[] hitboxPoints) {
        this.parent = parent;
        VertexArray v = new VertexArray();
        hitbox = new ConvexShape(hitboxPoints);

        viscosity = parent.getDensity() * 1.0f;
    }

    @Override
    public float getViscosity() {
        return viscosity;
    }

    @Override
    public void calculateCollision(Collider collider) {

        //F proportional to - velocity depending on shape and fluid properties

        if (collider instanceof SolidCollider) {

            SolidCollider solidCollider = (SolidCollider) collider;

            Vector2f relativeVel = Vector2f.sub(this.getVelocity(), solidCollider.getVelocity());

            float xForce = getViscosity() * solidCollider.getRadius() * relativeVel.x;
            float yForce = getViscosity() * solidCollider.getRadius() * relativeVel.y;

            collisionForce = Vector2f.mul(new Vector2f(xForce, yForce), -1.0f);
            System.out.println("force on dust: " + collisionForce);
        } else if (collider instanceof DiffuseCollider) {
            System.out.println("diffuse on diffuse collision");
        }
    }

    @Override
    public void applyCollision(Collider collider) {
        //setVelocity(Vector2f.add(getVelocity(), collisionForce));
        parent.applyForce(collisionForce);
    }

    @Override
    public float getDrag(Collider collider) {
        //drag = 1/2 * fluid density * relative vel ^2 * cross section * shape coefficient

        float shapeCoefficient = 0.47f; //drag coefficient of a circle

        float relativeVel = VectorMath.magnitude(Vector2f.sub(getVelocity(), collider.getVelocity()));

        float crossSection = collider.getParent().getBounds().width; //cross section of a circle is 2r

        return 0.5f * parent.getDensity() * relativeVel * relativeVel * crossSection * shapeCoefficient;
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
    public FloatRect getBounds() {
        return parent.getBounds();
    }

    @Override
    public float getDensity(){
        return parent.getDensity();
    }
}
