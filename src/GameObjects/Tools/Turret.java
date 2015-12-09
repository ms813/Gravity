package GameObjects.Tools;

import GameObjects.Creep;
import GameObjects.GameObject;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 26/11/15.
 */
public class Turret {

    public static final float TURRET_SIZE = 25;
    private GameObject parent;
    private float cooldown_max = 0.1f;
    private float cooldown_remaining = 0;
    private float damage = 1;
    private float range = 100;
    private Creep target;

    private float xp = 0f;

    public Turret(GameObject parent) {
        this.parent = parent;
    }

    public Bullet fireBullet(Vector2f targetPos) {

        if (cooldown_remaining > 0) {
            //weapon is cooling down, should never enter here
            throw new Error("Trying to fire weapon that hasn't cooled down!");
        }

        cooldown_remaining = cooldown_max;
        Bullet b = new Bullet(this, targetPos);
        b.setDestroyOnHit(true);
        return b;
    }

    public void update(float dt, boolean VERLET_STATE) {
        if (cooldown_remaining > 0) {
            //weapon is on cooldown so decrement the cooldown
            cooldown_remaining -= dt;
        } else {
            //check if turret has a target and fire at it
            if (VERLET_STATE && target != null) {
                target.receiveFire(this);
                //System.out.println("[Turret.update] " + this + " firing at " + target);
            }
        }

        if (target != null && target.getCurrentHp() <= 0) {
            target = null;
        }
    }

    public void draw(RenderWindow window){
        if(target != null) {
            VertexArray line = new VertexArray();
            line.add(new Vertex(parent.getCenter()));
            line.add(new Vertex(target.getCenter()));

            line.setPrimitiveType(PrimitiveType.LINES);
            window.draw(line);
        }
    }

    public void gainXp(float xp){
        this.xp += xp;
    }

    public GameObject getParent() {
        return parent;
    }

    public float getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }

    public Creep getTarget() {
        return target;
    }

    public void setTarget(Creep target) {
        this.target = target;
    }
}
