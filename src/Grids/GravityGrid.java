package Grids;

import Components.Collider;
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
    public ArrayList<Vector2i> getCellsForCollider(Collider c) {
        int col = (int) Math.floor((c.getPosition().x + c.getSize().x / 2) / cellSize);
        int row = (int) Math.floor((c.getPosition().y + c.getSize().y / 2) / cellSize);

        ArrayList<Vector2i> l = new ArrayList<>();
        l.add(new Vector2i(col, row));

        return l;
    }

    @Override
    public void insert(Collider c) {
        List<Vector2i> cellIds = getCellsForCollider(c);

        for (Vector2i id : cellIds) {

            if (cells.get(id) == null) {
                cells.put(id, new GravityGridCell(this, id));
            }

            if (!cells.get(id).contains(c)) {
                cells.get(id).insert(c);
            }
        }
    }

    public void recalculatePhysicalProperties() {
        for (GridCell cell : cells.values()) {
            ((GravityGridCell) cell).updateProperties();
        }
    }
}
