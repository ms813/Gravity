package GameObjects;

import Core.TextureManager;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Tools.HealthBar;
import GameObjects.Tools.Turret;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 04/12/2015.
 */
public class Creep extends GameObject {
    private float hp_max = 100;
    private float hp_current;
    private float damage = 1;
    private float SIZE = 25f;
    private HealthBar healthBar;
    private float xpValue = 1;

    private VertexArray trail = new VertexArray();
    private int trailCount = 0;

    public Creep(float mass, Vector2f pos) {

        this.density = 1f;
        this.mass = mass;
        this.heatCapacity = 0.84f;
        this.temperature = 200f;

        hp_current = hp_max;

        this.setPosition(pos);

        collider = new CircleCollider(this, 0.90f);

        texture = TextureManager.getTexture("creep.png");
        sprite.setTexture(texture);
        sprite.setScale(SIZE / getSize().x, SIZE / getSize().y);

        healthBar = new HealthBar(this);

        trail.setPrimitiveType(PrimitiveType.LINES);
    }

    public float getCurrentHp() {
        return hp_current;
    }

    public float getMaxHp() {
        return hp_max;
    }

    public void receiveFire(Turret turret) {
        this.hp_current -= turret.getDamage();
        if (hp_current <= 0) {
            setDestroyFlag(true);
            System.out.println("[Creep.receiveFire] " + this + " destroyed by " + turret);
            turret.gainXp(xpValue);
        }
    }

    @Override
    public void update(float dt, boolean VERLET_STATE) {
        super.update(dt, VERLET_STATE);

        if(VERLET_STATE){
            healthBar.update();

            trailCount++;
            if(trailCount % 5 == 0){
                trail.add(new Vertex(getCenter(), Color.GREEN));
                trailCount = 0;
            }
        }
    }

    @Override
    public void draw(RenderWindow window) {
        window.draw(trail);
        super.draw(window);
        healthBar.draw(window);
    }

    public float getDamage(){
        return damage;
    }
}
