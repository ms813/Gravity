package GameObjects;

import Core.TextureManager;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Tools.HealthBar;
import GameObjects.Tools.Turret;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 04/12/2015.
 */
public class Creep extends GameObject {
    protected float hp_max = 100;
    protected float hp_current;
    protected float damage = 1;
    protected float SIZE = 25f;
    protected HealthBar healthBar;
    protected float xpValue = 1;


    protected CreepType type = CreepType.NORMAL;

    public Creep(float mass, Vector2f pos) {

        this.density = 1f;
        this.mass = mass;
        this.heatCapacity = 0.84f;
        this.temperature = 200f;

        hp_current = hp_max;

        this.setPosition(pos);

        collider = new CircleCollider(this, 0.99f);

        texture = TextureManager.getTexture("creep.png");
        sprite.setTexture(texture);
        sprite.setScale(SIZE / getSize().x, SIZE / getSize().y);

        healthBar = new HealthBar(this);
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
        }
    }


    @Override
    public void draw(RenderWindow window) {
        super.draw(window);
        healthBar.draw(window);
    }
}
