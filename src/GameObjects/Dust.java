package GameObjects;

import Core.MainState;
import Core.TextureManager;
import Core.VectorMath;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Matthew on 26/10/2015.
 */
public class Dust implements GameObject {

    private VertexArray velocityLine = new VertexArray();
    private Color velocityLineColor = Color.MAGENTA;
    private VertexArray trail = new VertexArray();
    private Color trailColor = Color.WHITE;

    private Sprite sprite = new Sprite();
    private Texture texture = new Texture();

    private float density;
    private float mass;
    private Vector2f velocity = Vector2f.ZERO;
    private Vector2f appliedForce = Vector2f.ZERO;

    public Dust(float radius, Vector2f pos) {

        texture = TextureManager.getTexture("dust.png");
        texture.setSmooth(true);
        sprite.setTexture(texture);

        List<IntRect> textureRects = new ArrayList<>();//coords of small particles on texture
        textureRects.add(new IntRect(339, 262, 30, 29));
        textureRects.add(new IntRect(384, 262, 30, 29));
        textureRects.add(new IntRect(428, 262, 30, 29));
        textureRects.add(new IntRect(341, 311, 31, 29));
        textureRects.add(new IntRect(383, 311, 31, 29));
        textureRects.add(new IntRect(427, 311, 31, 29));

        sprite.setTextureRect(textureRects.get((int) Math.floor(Math.random() * 6)));

        sprite.setPosition(pos);

        sprite.scale(radius / sprite.getGlobalBounds().width, radius / sprite.getGlobalBounds().height);

        density = 5.0f;
        mass = getArea() * density;

        velocityLine.setPrimitiveType(PrimitiveType.LINES);
    }

    @Override
    public void update(float dt) {

        //get the direction and size of the applied force
        Vector2f dir = VectorMath.normalize(appliedForce);
        float F = VectorMath.magnitude(appliedForce);

        //calcualte the acceleration this frame and add it to the current velocity of the particle
        //F = ma
        Vector2f a = Vector2f.mul(dir, (F / mass) * dt);
        velocity = Vector2f.add(velocity, a);

        //finally, move the particle according to its current velocity, and reset the applied force to zero
        move(velocity);
        applyForce(Vector2f.ZERO);

        velocityLine.clear();
        Vector2f center = Vector2f.add(getPosition(), Vector2f.div(getSize(), 2));
        velocityLine.add(new Vertex(center, velocityLineColor));
        velocityLine.add(new Vertex(Vector2f.add(center, Vector2f.mul(velocity, 10)), velocityLineColor));

        trail.add(new Vertex(center, trailColor));
        trail.add(new Vertex(Vector2f.add(center, appliedForce), trailColor));

        if (trail.size() > 1000) {
            trail.remove(0);
        }

    }

    public void merge(GameObject d) {

        Vector2f startSize = getSize(); //used to recenter the sprite

        float totalArea = this.getArea() + d.getArea();

        //new density = area% * p1 + area% * p2

        float v1 = this.getArea() / (this.getArea() + d.getArea());
        float v2 = d.getArea() / (this.getArea() + d.getArea());

        this.setDensity(v1 * this.density + v2 * d.getDensity());

        //radius of a sphere = sqrt(A / pi)
        float radius = (float) Math.sqrt(totalArea / (float) Math.PI);
        float ratio = radius / (sprite.getGlobalBounds().width / 2);
        sprite.scale(ratio, ratio);

        //pTot = p1 * p2
        //mTot * vTot = m1 * v1 + m2 + v2
        Vector2f p1 = Vector2f.mul(velocity, mass);
        Vector2f p2 = Vector2f.mul(d.getVelocity(), d.getMass());
        Vector2f pTot = Vector2f.add(p1, p2);
        float mTot = mass + d.getMass();

        this.velocity = Vector2f.div(pTot, mTot);

        mass = getArea() * density;
               /*
        if (mass > 20000) {
            setFillColor(Color.BLUE);
        } else if (mass > 12000) {
            setFillColor(Color.YELLOW);
        } else if (mass > 8000) {
            setFillColor(new Color(255, 165, 0));
        } else if (mass > 3000) {
            setFillColor(Color.RED);
        }
            */
        Vector2f endSize = getSize();
        Vector2f dif = Vector2f.sub(endSize, startSize);
        //since the sprite's origin is (0,0, we have to move it
        //so that it's center stays in the same position
        //Hence we move it by back towards the top left by half of the size increase
        sprite.move(Vector2f.mul(dif, -0.5f));

    }

    @Override
    public void draw(RenderWindow w) {
        w.draw(sprite);

    }

    public void drawVelocity(RenderWindow w){
        w.draw(velocityLine);
    }

    public void drawTrail(RenderWindow w){
        w.draw(trail);
    }

    public float getMass() {
        return mass;
    }

    public float getArea() {
        //area of a circle =  pi * r^2
        float r = sprite.getGlobalBounds().width / 2;
        return (float) (Math.PI * Math.pow(r, 2));
    }

    private void setDensity(float p) {
        this.density = p;
    }

    public float getDensity() {
        return this.density;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }


    public void move(Vector2f offset) {
        sprite.move(offset);
    }

    public void applyForce(Vector2f force) {
        appliedForce = force;
    }


    @Override
    public Vector2f getPosition() {
        return sprite.getPosition();
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
    public void setFillColor(Color c) {
        sprite.setColor(c);
    }
}
