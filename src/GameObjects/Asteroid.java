package GameObjects;

import Core.TextureManager;
import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.SolidCollider;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Matthew on 26/10/2015.
 */
public class Asteroid implements GameObject {

    private CircleCollider collider;

    private boolean visible = true;
    private boolean active = true;

    private Sprite sprite = new Sprite();
    private Texture texture = new Texture();

    private float density;
    private float mass;
    private float temperature = 200; //estimate 200 K
    private float heatCapacity = 0.84f; //basalt

    private Vector2f velocity = Vector2f.ZERO;
    private Vector2f appliedForce = Vector2f.ZERO;

    private ParticleType type = ParticleType.DUST_SMALL;

    public Asteroid(float mass, Vector2f pos) {

        density = 5.0f;
        this.mass = mass;

        collider = new CircleCollider(this, 0.90f);
        texture = TextureManager.getTexture("dust.png");
        texture.setSmooth(true);
        sprite.setTexture(texture);
        updateTextureRect();
        sprite.setPosition(pos);

        float radius = (float) Math.sqrt(mass / (Math.PI * density));
        rescale(radius * 2);
        type = checkType();
        updateTextureRect();

    }

    @Override
    public void update(float dt) {
        if (active) {
            velocity = getUpdatedVelocity(dt);

            //move the particle according to its current velocity, and reset the applied force to zero
            move(velocity);
            appliedForce = Vector2f.ZERO;
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
    /*
    public void merge(GameObject d) {

        Vector2f startSize = getSize(); //used to recenter the sprite

        float totalArea = this.getArea() + d.getArea();

        //new density = area% * p1 + area% * p2

        float v1 = this.getArea() / (this.getArea() + d.getArea());
        float v2 = d.getArea() / (this.getArea() + d.getArea());

        this.setDensity(v1 * this.density + v2 * d.getDensity());

        //radius of a sphere = sqrt(A / pi)
        float size = 2f * (float) Math.sqrt(totalArea / (float) Math.PI);

        rescale(size);
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

//        if (mass > 20000) {
//            setFillColor(Color.BLUE);
//        } else if (mass > 12000) {
//            setFillColor(Color.YELLOW);
//        } else if (mass > 8000) {
//            setFillColor(new Color(255, 165, 0));
//        } else if (mass > 3000) {
//            setFillColor(Color.RED);
//        }

        Vector2f endSize = getSize();
        Vector2f dif = Vector2f.sub(endSize, startSize);
        //since the sprite's origin is (0,0, we have to move it
        //so that it's center stays in the same position
        //Hence we move it by back towards the top left by half of the size increase
        sprite.move(Vector2f.mul(dif, -0.5f));

    }
*/

    //this scales the different sized textures according to the internal "radius" of the particle
    private void rescale(float size) {
        sprite.setScale(1f, 1f);
        sprite.scale(size / sprite.getGlobalBounds().width, size / sprite.getGlobalBounds().height);
        collider.update();
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
        ParticleType prevType = type;
        if (type == ParticleType.DUST_SMALL) {
            sprite.setTextureRect(TextureManager.smallTextureRects[(int) Math.floor(Math.random() * TextureManager.smallTextureRects.length)]);
        } else if (type == ParticleType.DUST_MED) {
            sprite.setTextureRect(TextureManager.medTextureRects[(int) Math.floor(Math.random() * TextureManager.medTextureRects.length)]);
        } else {
            sprite.setTextureRect(TextureManager.largeTextureRects[(int) Math.floor(Math.random() * TextureManager.largeTextureRects.length)]);
        }
        rescale((getSize().x + getSize().y) / 4);
    }

    @Override
    public void draw(RenderWindow w) {
        if (visible) {
            w.draw(sprite);
            collider.draw(w);
        }
    }

    @Override
    public boolean isColliding(GameObject obj){
        return collider.isColliding(obj);
    }

    @Override
    public Collider getCollider(){
        return collider;
    }

    @Override
    public float getMass() {
        return mass;
    }
/*
    public float getArea() {
        //area of a circle =  pi * r^2
        float r = (sprite.getGlobalBounds().width + sprite.getGlobalBounds().height) / 4;
        return (float) (Math.PI * Math.pow(r, 2));
    }
*/

    @Override
    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    @Override
    public void move(Vector2f offset) {
        sprite.move(offset);
        collider.update();
    }

    @Override
    public void applyForce(Vector2f force) {
        appliedForce = Vector2f.add(appliedForce, force);
    }

    public Vector2f getKineticEnergy() {
        float x = 0.5f * mass * velocity.x * velocity.x;
        float y = 0.5f * mass * velocity.y * velocity.y;
        return new Vector2f(x, y);
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
        return sprite.getGlobalBounds();
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
    }

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
    public boolean isSolid() {
        return collider instanceof SolidCollider;
    }

    @Override
    public void calculateCollision(GameObject o) {
        collider.calculateCollision(o);
    }

    @Override
    public void applyCollision() {
        collider.applyCollision();
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public List<GameObject> getChildren(){
        return new ArrayList<>();
    }
}
