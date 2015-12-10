package GameObjects;

import Core.TextureManager;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Tools.Turret;
import GameObjects.Tools.TurretPlatform;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Matthew on 26/10/2015.
 */
public class Asteroid extends TurretPlatform {

    private ParticleType type = ParticleType.DUST_SMALL;

    private VertexArray trail = new VertexArray();
    private int trailCount = 0;

    public Asteroid(float mass, Vector2f pos) {

        this.density = 5.0f;
        this.mass = mass;
        this.heatCapacity = 0.84f;
        this.temperature = 200f;

        this.setPosition(pos);
        collider = new CircleCollider(this, 0.90f);

        texture = TextureManager.getTexture("dust.png");
        texture.setSmooth(true);
        sprite.setTexture(texture);
        updateTextureRect();

        float radius = (float) Math.sqrt(this.mass / (Math.PI * density));
        rescale(radius * 2);
        type = checkType();
        updateTextureRect();

        maxTurrets = (int) Math.floor(getSize().y / Turret.TURRET_SIZE) * (int) Math.floor(getSize().x / Turret.TURRET_SIZE);

        trail.setPrimitiveType(PrimitiveType.LINE_STRIP);
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
    public void update(float dt, boolean VERLET_STATE) {
        super.update(dt, VERLET_STATE);

        if(VERLET_STATE){
            trailCount++;
            if(trailCount % 5 == 0){
                trail.add(new Vertex(getCenter()));
                trailCount = 0;
            }
        }
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(trail);
        super.draw(window);
    }
}
