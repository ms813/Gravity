package GameObjects;

import Core.GlobalConstants;
import Core.TextureManager;
import Core.VectorMath;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

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

    private float radius;
    private CircleShape collisionRadius = new CircleShape();

    private float density;
    private float mass;
    private Vector2f velocity = Vector2f.ZERO;
    private Vector2f appliedForce = Vector2f.ZERO;

    private ParticleType type = ParticleType.DUST_SMALL;

    public Dust(float radius, Vector2f pos) {

        this.radius = radius;

        texture = TextureManager.getTexture("dust.png");
        texture.setSmooth(true);
        sprite.setTexture(texture);
        updateTextureRect();
        sprite.setPosition(pos);

        rescale();
        type = checkType();
        updateTextureRect();

        density = 5.0f;
        mass = getArea() * density;

        velocityLine.setPrimitiveType(PrimitiveType.LINES);

        collisionRadius.setFillColor(Color.TRANSPARENT);
        collisionRadius.setOutlineColor(Color.GREEN);
        collisionRadius.setOutlineThickness(-1.0f);
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
        appliedForce = Vector2f.ZERO;
        collisionRadius.setPosition(getPosition());

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
        radius = (float) Math.sqrt(totalArea / (float) Math.PI);
        rescale();
        if (type != checkType()) {
            type = checkType();
            updateTextureRect();
        }

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

    //this scales the different sized textures according to the internal "radius" of the particle
    private void rescale() {
        sprite.setScale(1f, 1f);
        float width = sprite.getGlobalBounds().width;
        float height = sprite.getGlobalBounds().height;
        sprite.scale((radius * 2) / width, (radius * 2) / height);
        collisionRadius.setRadius(radius);
    }

    private ParticleType checkType() {
        float width = sprite.getGlobalBounds().width;
        if (width < 32f) {
            return ParticleType.DUST_SMALL;
        } else if (width < 64f) {
            return ParticleType.DUST_MED;
        } else {
            return ParticleType.DUST_LARGE;
        }
    }

    private void updateTextureRect() {
        if (type == ParticleType.DUST_SMALL) {
            sprite.setTextureRect(TextureManager.smallTextureRects[(int) Math.floor(Math.random() * TextureManager.smallTextureRects.length)]);
        } else if (type == ParticleType.DUST_MED) {
            sprite.setTextureRect(TextureManager.medTextureRects[(int) Math.floor(Math.random() * TextureManager.medTextureRects.length)]);
        } else {
            sprite.setTextureRect(TextureManager.largeTextureRects[(int) Math.floor(Math.random() * TextureManager.largeTextureRects.length)]);
        }
        rescale();
    }

    @Override
    public void draw(RenderWindow w) {
        w.draw(sprite);
        //w.draw(collisionRadius);
    }

    public void drawVelocity(RenderWindow w) {
        w.draw(velocityLine);
    }

    public void drawTrail(RenderWindow w) {
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
        appliedForce = Vector2f.add(appliedForce, force);
    }

    @Override
    public Vector2f getKineticEnergy() {
        float x = 0.5f * mass * velocity.x * velocity.x;
        float y = 0.5f * mass * velocity.y * velocity.y;
        return new Vector2f(x, y);
    }

    @Override
    public float getCollisionRadius() {
        return radius;
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
        return new Vector2f(
                sprite.getGlobalBounds().left + sprite.getGlobalBounds().width / 2,
                sprite.getGlobalBounds().top + sprite.getGlobalBounds().height / 2
        );
    }

    @Override
    public FloatRect getBounds() {
        float fringe = 0;
        return new FloatRect(
                sprite.getGlobalBounds().left + fringe,
                sprite.getGlobalBounds().top + fringe,
                sprite.getGlobalBounds().width - 2 * fringe,
                sprite.getGlobalBounds().height - 2 * fringe);
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
    }

    @Override
    public void setFillColor(Color c) {
        sprite.setColor(c);
    }

    @Override
    public List<GameObject> breakInto() {
        return null;
    }

    @Override
    public float getBindingEnergy() {
        //2D gravitational binding energy = (2/3) * Gm^2/r
        return (2f / 3f) * GlobalConstants.GRAVITATIONAL_CONSTANT * mass * mass / radius;
    }
}
