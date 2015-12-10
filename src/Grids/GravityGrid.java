package Grids;

import GameObjects.GameObject;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.*;

/**
 * Created by Matthew on 31/10/2015.
 */
public class GravityGrid extends SpatialHashGrid {

    public GravityGrid(float cellSize) {
        super(cellSize);
    }

    @Override
    public ArrayList<Vector2i> getCellsForObj(GameObject o) {
        int col = (int) Math.floor((o.getPosition().x + o.getSize().x / 2) / cellSize);
        int row = (int) Math.floor((o.getPosition().y + o.getSize().y / 2) / cellSize);

        ArrayList<Vector2i> l = new ArrayList<>();
        l.add(new Vector2i(col, row));

        return l;
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

    public void recalculatePhysicalProperties() {
        for (GridCell cell : cells.values()) {
            ((GravityGridCell) cell).updateProperties();
        }
    }
}
