import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 26/10/2015.
 */
public class Dust extends CoreGameObject implements GameObject {

    private float density;

    public Dust(float radius, Vector2f pos){
        shape = new CircleShape();
        ((CircleShape)shape).setRadius(radius);
        shape.setOutlineColor(Color.CYAN);
        shape.setOutlineThickness(1.0f);

        shape.setPosition(pos);
        shape.setFillColor(new Color(139,69,19));

        density = 5.0f;
        mass = getArea() * density;
    }

    @Override
    public void update(float dt) {
        Vector2f dir = VectorMath.normalize(appliedForce);
        float mag = VectorMath.magnitude(appliedForce);
        Vector2f a = Vector2f.mul(dir, (mag / mass) * dt);
        velocity = Vector2f.add(velocity, a);
        move(velocity);
        applyForce(Vector2f.ZERO);
    }

    public float getMass(){
        return mass;
    }

    public float getArea(){
        //area of a circle =  pi * r^2
        return ((float) Math.PI * (float) Math.pow(((CircleShape)shape).getRadius(), 2));
    }

    public float getRadius(){
        return ((CircleShape) shape).getRadius();
    }

    public void merge(GameObject d){

        float totalArea = this.getArea() + d.getArea();

        //new density = area% * p1 + area% * p2

        float v1 = this.getArea() / (this.getArea() + d.getArea());
        float v2 = d.getArea() / (this.getArea() + d.getArea());

        this.setDensity(v1 * this.density + v2 * d.getDensity());

        //radius of a sphere = sqrt(A / pi)
        this.setRadius((float) Math.sqrt(totalArea / (float) Math.PI));

        //pTot = p1 * p2
        //mTot * vTot = m1 * v1 + m2 + v2
        Vector2f p1 = Vector2f.mul(velocity, mass);
        Vector2f p2 = Vector2f.mul(d.getVelocity(), d.getMass());
        Vector2f pTot = Vector2f.add(p1, p2);
        float mTot = mass + d.getMass();

        this.velocity = Vector2f.div(pTot, mTot);

        mass = getArea() * density;

        if(mass > 20000){
            shape.setFillColor(Color.BLUE);
        } else if(mass > 12000){
            shape.setFillColor(Color.YELLOW);
        } else if(mass > 8000){
            shape.setFillColor(new Color(255,165,0));
        } else if(mass > 3000){
            shape.setFillColor(Color.RED);
        }
    }

    private void setDensity(float p){
        this.density = p;
    }

    public float getDensity(){
        return this.density;
    }

    private void setRadius(float r){
        ((CircleShape) shape).setRadius(r);
        //shape.setOrigin(r, r);
    }

    public Vector2f getVelocity(){
        return velocity;
    }
    public void setVelocity(Vector2f velocity){
        this.velocity = velocity;
    }


    public void move(Vector2f offset) {
        if (sprite != null) {
            sprite.move(offset);
        } else{
            shape.move(offset);
        }
    }

    public void applyForce(Vector2f force){
        appliedForce = force;
    }
}
