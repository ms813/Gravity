package Grids;

import GameObjects.Colliders.CircleCollider;
import GameObjects.GameObject;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matthew on 31/10/2015.
 */
public class CollisionGrid implements SpatialHashGrid {

    private float cellSize;

    private HashMap<Vector2i, GridCell> cells = new HashMap<>();

    public CollisionGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    public ArrayList<Vector2i> getCellsForObj(GameObject o) {

        ArrayList<Vector2i> ids = new ArrayList<>();

        Vector2f topLeftPos = o.getCollider().getPosition();
        Vector2f bottomRightPos = Vector2f.add(topLeftPos, o.getCollider().getSize());

        int leftCol = (int) Math.floor(topLeftPos.x / cellSize);
        int rightCol = (int) Math.floor(bottomRightPos.x / cellSize);
        int topRow = (int) Math.floor(topLeftPos.y / cellSize);
        int bottomRow = (int) Math.floor(bottomRightPos.y / cellSize);

        if (o.getCollider() instanceof CircleCollider) {
            float objRadius = ((CircleCollider) o.getCollider()).getRadius();
            Vector2f objCenter = o.getCollider().getCenter();


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
    public void draw(RenderWindow w) {
        for (GridCell cell : cells.values()) {
            cell.draw(w);
        }
    }

    @Override
    public void clear() {
        cells.clear();
    }

    @Override
    public List<GridCell> getCells() {
        return new ArrayList<>(cells.values());
    }

    @Override
    public float getCellSize() {
        return cellSize;
    }

    public void insert(GameObject o) {
        List<Vector2i> cellIds = getCellsForObj(o);

        for (Vector2i id : cellIds) {

            if (cells.get(id) == null) {
                cells.put(id, new GridCell(this, id));
            }

            if (!cells.get(id).contains(o)) {
                cells.get(id).insert(o);
            }
        }
    }
}
