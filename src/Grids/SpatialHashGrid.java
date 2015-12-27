package Grids;

import Components.Collider;
import GameObjects.Entity;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2i;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matthew on 30/10/2015.
 */
public abstract class SpatialHashGrid {

    protected float cellSize;

    protected HashMap<Vector2i, GridCell> cells = new HashMap<>();

    public SpatialHashGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    abstract void insert(Components.Collider collider);

    abstract ArrayList<Vector2i> getCellsForCollider(Components.Collider collider);

    public void draw(RenderWindow w) {
        for (GridCell cell : cells.values()) {
            cell.draw(w);
        }
    }

    public void clear() {
        cells.clear();
    }

    public List<GridCell> getCells() {
        return new ArrayList<>(cells.values());
    }

    public float getCellSize() {
        return cellSize;
    }

    public List<Collider> getAllColliders() {
        List<Collider> colliders = new ArrayList<>();
        for (GridCell cell : cells.values()) {
            colliders.addAll(cell.getColliders());
        }
        return colliders;
    }
}
