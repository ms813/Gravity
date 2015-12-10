package Core;

import GameObjects.Creep;
import GameObjects.GameObject;
import GameObjects.Tools.Turret;
import GameObjects.Tools.TurretPlatform;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 05/12/2015.
 */
public class TargetingHandler {

    private List<Creep> targets = new ArrayList<>();
    private List<TurretPlatform> platforms = new ArrayList<>();

    public void assignTargets(){
        for(TurretPlatform platform : platforms){
            for(Turret t : platform.getTurrets()){

                float range = t.getRange();

                //set closest creep to previous target if there was one, if not to the first creep in the list
                Creep closestCreep = (t.getTarget() != null) ? t.getTarget() : targets.get(0);

                float closestDist = VectorMath.magnitude(Vector2f.sub(closestCreep.getCenter(), platform.getCenter()));
                for(Creep creep : targets){
                    float dist = VectorMath.magnitude(Vector2f.sub(creep.getCenter(), platform.getCenter()));
                    if(dist < closestDist){
                        closestCreep = creep;
                        closestDist = dist;
                    }
                }

                if(closestDist < range){
                    t.setTarget(closestCreep);
                } else{
                    t.setTarget(null);
                }
            }
        }
    }

    public void insertAll(List<GameObject> objects) {
        targets.clear();
        platforms.clear();

        for (GameObject o : objects) {
            if (o instanceof Creep) {
                targets.add((Creep) o);
            } else if (o instanceof TurretPlatform) {
                platforms.add((TurretPlatform) o);
            }
        }
    }
}
