package GameObjects.Tools;

import GameObjects.GameObject;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 04/12/2015.
 */
public class TurretPlatform extends GameObject {

    protected List<Turret> turrets = new ArrayList<>();

    protected int maxTurrets;

    @Override
    public void update(float dt, boolean VERLET_STATE) {
        super.update(dt, VERLET_STATE);
        for (Turret t : turrets) {
            t.update(dt, VERLET_STATE);
        }
    }

    @Override
    public void draw(RenderWindow window){
        super.draw(window);
        for(Turret t : turrets){
            t.draw(window);
        }
    }

    public void addTurret(Turret turret) {
        if (!turrets.contains(turret)) {
            if (turrets.size() < maxTurrets) {
                turrets.add(turret);
                setColor(Color.MAGENTA);
            } else {
                System.err.println(this + " turret cap reached");
            }

        } else {
            System.err.println(this + " already contains turret " + turret);
        }
    }

    public void setColor(Color color){
        sprite.setColor(color);
    }

    public List<Turret> getTurrets(){
        return turrets;
    }

}
