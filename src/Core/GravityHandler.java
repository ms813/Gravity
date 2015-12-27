package Core;

import Components.Collider;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import Grids.GridCell;
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

    public Vector2f getForce(Collider c) {
        List<GridCell> gravityCells = grid.getCells();

        Vector2f totalForce = Vector2f.ZERO;    //running total of forces acting on object o

        //find the cell that collider c is in
        GridCell cCell = null;
        for (GridCell cell : gravityCells) {
            if (cell.contains(c)) {
                cCell = cell;
            }
        }

        //get the forces from all of the particles in the same cell as o
        if (cCell != null) {
            for (Collider collider : cCell.getColliders()) {
                if (collider == c) continue; //don't calculate force contribution from self

                float F, G, m, M, r;
                Vector2f centerToCenter = Vector2f.sub(collider.getCenter(), c.getCenter());
                Vector2f direction = VectorMath.normalize(centerToCenter);

                G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                m = c.getMass();
                M = collider.getMass();
                r = VectorMath.magnitude(centerToCenter);


                F = (G * m * M) / (r * r);

                totalForce = Vector2f.add(totalForce, Vector2f.mul(direction, F));
            }
        } else {
            System.err.println("Object not found in gravity grid");
        }


        //now add an approximate force from the cell's center of mass
        for (GridCell gridCell : gravityCells) {
            GravityGridCell cell = (GravityGridCell) gridCell;
            //ignore contribution from same cell that object is in as we have already considered it
            if (cell == cCell) continue;

            float F, G, m, M, r;
            Vector2f centerToCenter = Vector2f.sub(cell.getCenterOfMass(), c.getCenter());
            Vector2f direction = VectorMath.normalize(centerToCenter);

            G = GlobalConstants.GRAVITATIONAL_CONSTANT;
            m = c.getMass();
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

    public void insertAll(List<Collider> colliders) {
        for (Collider c : colliders) {
            insert(c);
        }
    }

    public void insert(Collider c) {
        grid.insert(c);
    }

    public void recalculatePhysicalProperties() {
        grid.recalculatePhysicalProperties();
    }
}
