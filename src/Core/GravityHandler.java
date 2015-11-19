package Core;

import GameObjects.GameObject;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.List;

/**
 * Created by smithma on 17/11/15.
 */
public class GravityHandler {

    private GravityGrid grid = new GravityGrid(50.0f);

    private boolean GRID_VISIBLE = false;

    public void showGrid() {
        GRID_VISIBLE = true;
    }

    public void hideGrid() {
        GRID_VISIBLE = false;
    }

    public GravityHandler(float gridSize) {
        grid = new GravityGrid(gridSize);
    }

    public void draw(RenderWindow window) {
        if (GRID_VISIBLE) {
            grid.draw(window);
        }
    }

    public Vector2f getForce(GameObject o) {
        List<GravityGridCell> gravityCells = grid.getCells();

        Vector2f totalForce = Vector2f.ZERO;    //running total of forces acting on object o

        //find the cell that object o is in
        GravityGridCell oCell = null;
        for (GravityGridCell cell : gravityCells) {
            if (cell.contains(o)) {
                oCell = cell;
            }
        }

        //get the forces from all of the particles in the same cell as o
        if (oCell != null) {
            for (GameObject object : oCell.getObjects()) {
                if (object == o) continue; //don't calculate force contribution from self

                float F, G, m, M, r;
                Vector2f centerToCenter = Vector2f.sub(object.getCenter(), o.getCenter());
                Vector2f direction = VectorMath.normalize(centerToCenter);

                G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                m = o.getMass();
                M = object.getMass();
                r = VectorMath.magnitude(centerToCenter);


                F = (G * m * M) / (r * r);

                totalForce = Vector2f.add(totalForce, Vector2f.mul(direction, F));
            }
        } else {
            System.err.println("Object not found in gravity grid");
        }


        //now add an approximate force from the cell's center of mass
        for (GravityGridCell cell : gravityCells) {
            //ignore contribution from same cell that object is in as we have already considered it
            if (cell == oCell) continue;

            float F, G, m, M, r;
            Vector2f centerToCenter = Vector2f.sub(cell.getCenterOfMass(), o.getCenter());
            Vector2f direction = VectorMath.normalize(centerToCenter);

            G = GlobalConstants.GRAVITATIONAL_CONSTANT;
            m = o.getMass();
            M = cell.getTotalMass();
            r = VectorMath.magnitude(centerToCenter);

            F = (G * m * M) / (r * r);

            totalForce = Vector2f.add(totalForce, Vector2f.mul(direction, F));
        }

        return totalForce;
    }

    public void reset() {
        grid.clear();
    }

    public void insertAll(List<GameObject> objects) {
        for (GameObject object : objects) {
            insert(object);
        }
    }

    public void insert(GameObject object) {
        grid.insert(object);
        if (object.getChildren().size() > 0) {
            insertAll(object.getChildren());
        }
    }

    public void recalculatePhysicalProperties() {
        grid.recalculatePhysicalProperties();
    }
}
