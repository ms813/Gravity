package Grids;

import Components.CircleCollider;
import Components.Collider;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.*;

/**
 * Created by Matthew on 31/10/2015.
 */
public class CollisionGrid extends SpatialHashGrid {

    public CollisionGrid(float cellSize) {
        super(cellSize);
    }

    @Override
    public ArrayList<Vector2i> getCellsForCollider(Collider collider) {

        ArrayList<Vector2i> ids = new ArrayList<>();

        Vector2f topLeftPos = collider.getPosition();
        Vector2f bottomRightPos = Vector2f.add(topLeftPos, collider.getSize());

        int leftCol = (int) Math.floor(topLeftPos.x / cellSize);
        int rightCol = (int) Math.floor(bottomRightPos.x / cellSize);
        int topRow = (int) Math.floor(topLeftPos.y / cellSize);
        int bottomRow = (int) Math.floor(bottomRightPos.y / cellSize);

        if (collider instanceof CircleCollider) {
            float objRadius = ((CircleCollider) collider).getRadius();
            Vector2f objCenter = collider.getCenter();


            for (int i = leftCol; i <= rightCol; i++) {
                for (int j = topRow; j <= bottomRow; j++) {

                    if (objRadius < cellSize) {
                        //object is small enough to inside a 2*2 square of cells
                        //therefore, at most 4 cells will be needed, with no hollow in the middle
                        ids.add(new Vector2i(i, j));
                    } else {

                        //object is larger than a 2*2 square of cells, so it requires a hollow in the middle

                        Vector2f cellCenter = new Vector2f(i * cellSize + cellSize / 2, j * cellSize + cellSize / 2);

                        float dx = Math.abs(cellCenter.x - objCenter.x);
                        float dy = Math.abs(cellCenter.y - objCenter.y);

                        float r_min = (dx - cellSize / 2) * (dx - cellSize / 2) + (dy - cellSize / 2) * (dy - cellSize / 2);
                        float r_max = (dx + cellSize / 2) * (dx + cellSize / 2) + (dy + cellSize / 2) * (dy + cellSize / 2);

                        if (r_min < objRadius * objRadius &&
                                r_max > objRadius * objRadius) {
                            ids.add(new Vector2i(i, j));
                        }
                    }
                }
            }

        } else {
            //assume rectangular collider for now

            /*
            for (int i = leftCol; i <= rightCol; i++) {
                for (int j = topRow; j <= bottomRow; j++) {
                    ids.add(new Vector2i(i, j));
                }
            }
            */
            //add grid squares only to the outsides of the object
            //first, the top and bottom row
            for (int i = leftCol; i <= rightCol; i++) {
                ids.add(new Vector2i(i, topRow));
                ids.add(new Vector2i(i, bottomRow));
            }

            //next, the left and right columns (not including the top and bottom row)
            for (int j = topRow + 1; j <= bottomRow - 1; j++) {
                ids.add(new Vector2i(leftCol, j));
                ids.add(new Vector2i(rightCol, j));
            }
        }

        return ids;
    }

    @Override
    public void insert(Collider collider) {
        List<Vector2i> cellIds = getCellsForCollider(collider);

        for (Vector2i id : cellIds) {

            if (cells.get(id) == null) {
                cells.put(id, new GridCell(this, id));
            }

            if (!cells.get(id).contains(collider)) {
                cells.get(id).insert(collider);
            }
        }
    }
}
