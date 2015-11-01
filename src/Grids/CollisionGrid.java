package Grids;

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

    public CollisionGrid(float cellSize){
        this.cellSize = cellSize;
    }

    @Override
    public ArrayList<Vector2i> getCellsForObj(GameObject o) {

        ArrayList<Vector2i> ids = new ArrayList<>();

        Vector2f topLeftPos = o.getPosition();
        Vector2f bottomRightPos = Vector2f.add(o.getPosition(), o.getSize());

        int leftCol = (int) Math.floor(topLeftPos.x / cellSize);
        int rightCol = (int) Math.floor(bottomRightPos.x / cellSize);
        int topRow = (int) Math.floor(topLeftPos.y / cellSize);
        int bottomRow = (int) Math.floor(bottomRightPos.y / cellSize);

        for (int i = leftCol; i <= rightCol; i++) {
            for (int j = topRow; j <= bottomRow; j++) {
                ids.add(new Vector2i(i, j));
            }
        }
        return ids;
    }

    @Override
    public void draw(RenderWindow w) {
        for(GridCell cell : cells.values()){
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

    public  void insert(GameObject o) {
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
