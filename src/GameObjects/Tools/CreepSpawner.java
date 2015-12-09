package GameObjects.Tools;

import Core.VectorMath;
import GameObjects.Creep;
import GameStates.GameState;
import org.jsfml.system.Vector2f;
import org.omg.CORBA.VM_ABSTRACT;

/**
 * Created by Matthew on 04/12/2015.
 */
public class CreepSpawner {

    private boolean spawning = false;
    private int level = 0;
    private GameState scene;

    private float cooldown = 1;
    private float cooldown_remaining = 0;
    private int creep_count, creeps_remaining;

    public CreepSpawner(GameState scene) {
        this.scene = scene;
    }

    public void startLevel(int level) {
        this.level = level;
        spawning = true;

        if (level == 1) {
            creep_count = 20;
            creeps_remaining = creep_count;
        }
    }

    public void update(float dt) {

        if (creeps_remaining > 0) {
            if (cooldown_remaining > 0) {
                //spawner on cooldown so decrement the cooldown
                cooldown_remaining -= dt;
            } else {
                //spawner ready so lets spawn a creep

                Creep creep = new Creep(1f, Vector2f.ZERO);
                creep.setVelocity(Vector2f.mul(VectorMath.RIGHT,75));

                scene.addGameObject(creep);

                cooldown_remaining = cooldown;
                --creeps_remaining;
            }
        }
    }

}
