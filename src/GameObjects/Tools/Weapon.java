package GameObjects.Tools;

import GameObjects.GameObject;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 26/11/15.
 */
public class Weapon {

    private GameObject parent;

    private float cooldown_max = 0.1f;
    private float cooldown_remaining = 0;
    private float power = 200;

    public Weapon(GameObject parent){
        this.parent = parent;
    }

    public Bullet fire(Vector2f targetPos) {

        assert(cooldown_remaining <= 0);

        if(cooldown_remaining > 0){
            //weapon is cooling down, should never enter here
            throw new Error("Trying to fire weapon that hasn't cooled down!");
        }
        cooldown_remaining = cooldown_max;
        Bullet b = new Bullet(this, targetPos);
        b.setDestroyOnHit(true);
        return b;
    }

    public boolean isReady(){
        return cooldown_remaining <= 0;
    }

    public void decrementCooldown(float dt){
        if(cooldown_remaining > 0){
            cooldown_remaining -= dt;
        }
    }

    public GameObject getParent(){
        return parent;
    }

    public float getPower(){
        return power;
    }
}
