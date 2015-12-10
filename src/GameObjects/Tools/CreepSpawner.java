package GameObjects.Tools;

import Core.Game;
import Core.GlobalConstants;
import Core.VectorMath;
import GameObjects.Colliders.NonCollider;
import GameObjects.Creep;
import GameObjects.GameObject;
import org.jsfml.system.Vector2f;

import java.util.*;

/**
 * Created by Matthew on 04/12/2015.
 */
public class CreepSpawner extends GameObject {

    private int level = 0;

    private float cooldown = 1;
    private float cooldown_remaining = 0;
    private int creeps_remaining;

    private GameObject dominantGravityObject;

    private Queue<Creep> creepQueue = new PriorityQueue<>();

    public CreepSpawner(Vector2f position, Vector2f velocity) {
        setPosition(position);
        setVelocity(velocity);

        //visible = false;
        collider = new NonCollider(this);
    }

    public void startNextLevel() {
        level++;
        creeps_remaining = 10 + (level - 1) * 2;
    }

    @Override
    public void update(float dt, boolean VERLET_STATE) {
        super.update(dt, VERLET_STATE);
        if (creeps_remaining > 0) {
            if (cooldown_remaining > 0) {
                //spawner on cooldown so decrement the cooldown
                cooldown_remaining -= dt;
            } else {
                //spawner ready so lets spawn a creep

                //attempt to set up a stable orbit around the dominant body
                // v = sqrt(Gm/2r)
                float v = 0;
                Vector2f difference = Vector2f.ZERO;
                if (dominantGravityObject != null) {
                    difference = Vector2f.sub(collider.getCenter(), dominantGravityObject.getCenter());

                    float r = VectorMath.magnitude(difference);
                    v = (float) Math.sqrt(GlobalConstants.GRAVITATIONAL_CONSTANT * dominantGravityObject.getMass() / (4f * r));
                }
                Creep creep = new Creep(100f, getCenter());
                creep.setVelocity(Vector2f.mul(VectorMath.unitTangent(difference), v));

                creepQueue.add(creep);

                cooldown_remaining = cooldown;
                --creeps_remaining;
            }
        }
    }

    public Creep poll() {
        return creepQueue.poll();
    }

    public boolean isEmpty() {
        return creepQueue.isEmpty();
    }

    public int getCreepsRemaining() {
        return creeps_remaining;
    }

    public void setDominantGravityObject(GameObject obj) {
        this.dominantGravityObject = obj;
    }
}
