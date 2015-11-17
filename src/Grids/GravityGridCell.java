package Grids;

import GameObjects.GameObject;
import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.List;

/**
 * Created by Matthew on 01/11/2015.
 */
public class GravityGridCell extends GridCell {

    private Vector2f centerOfMass;
    private float totalMass;

    public GravityGridCell(SpatialHashGrid parent, Vector2i id) {
        super(parent, id);
        outline.setOutlineColor(Color.RED);
    }

    private float calculateTotalMass(List<GameObject> objs) {
        float mass = 0;
        for (GameObject o : objs) {
            mass += o.getMass();
        }
        return mass;
    }

    private Vector2f calculateCenterOfMass(List<GameObject> objs) {
        float x = 0, y = 0, tMass = 0;

        for (GameObject o : objs) {
            x += o.getCenter().x * o.getMass();
            y += o.getCenter().y * o.getMass();
            tMass += o.getMass();
        }

        Vector2f com = new Vector2f(x / tMass, y / tMass);
        /*
        if (com.x < parent.getCellSize() * id.x
                || com.x > parent.getCellSize() * (id.x + 1)
                || com.y < parent.getCellSize() * id.y
                || com.y > parent.getCellSize() * (id.y + 1)) {
            System.out.println("Center of mass of cell " + id + " outside of cell!");
            System.out.println("Com = " + com);

            float l = outline.getGlobalBounds().left,
                    r = outline.getGlobalBounds().left + outline.getGlobalBounds().width,
                    t = outline.getGlobalBounds().top,
                    b = outline.getGlobalBounds().top + outline.getGlobalBounds().height;

            System.out.println("Left = " + l + ", right = " + r + ", top = " + t + ", bottom = " + b);
            System.out.println("outline: " + outline + " com = " + com);
        }
        */
        return com;
    }


    public void updateProperties() {
        totalMass = calculateTotalMass(objects);
        centerOfMass = calculateCenterOfMass(objects);

    }

    public float getTotalMass() {
        return totalMass;
    }

    public Vector2f getCenterOfMass() {
        return centerOfMass;
    }
}
