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
public class GravityGrid implements SpatialHashGrid {

    private float cellSize;

    protected HashMap<Vector2i, GravityGridCell> cells = new HashMap<>();

    public GravityGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    public ArrayList<Vector2i> getCellsForObj(GameObject o) {
        int col = (int) Math.floor(o.getPosition().x / cellSize);
        int row = (int) Math.floor(o.getPosition().y / cellSize);

        ArrayList<Vector2i> l = new ArrayList<>();
        l.add(new Vector2i(col, row));

        return l;
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
    public void insert(GameObject o) {
        List<Vector2i> cellIds = getCellsForObj(o);

        for (Vector2i id : cellIds) {

            if (cells.get(id) == null) {
                cells.put(id, new GravityGridCell(this, id));
            }

            if (!cells.get(id).contains(o)) {
                cells.get(id).insert(o);
            }
        }
    }

    public void updateProperties(){
        for(GravityGridCell cell : cells.values()){
            cell.updateProperties();
        }
    }

    @Override
    public List<GravityGridCell> getCells(){
        return new ArrayList<>(cells.values());
    }

    @Override
    public float getCellSize() {
        return cellSize;
    }
}
