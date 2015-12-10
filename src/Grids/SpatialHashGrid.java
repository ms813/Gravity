package Grids;

import Core.Game;
import GameObjects.GameObject;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 30/10/2015.
 */
public abstract class SpatialHashGrid {

    protected float cellSize;

    protected HashMap<Vector2i, GridCell> cells = new HashMap<>();

    public SpatialHashGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    abstract void insert(GameObject o);

    abstract ArrayList<Vector2i> getCellsForObj(GameObject o);

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

    public List<GameObject> getAllObjects() {
        List<GameObject> objects = new ArrayList<>();
        for (GridCell cell : cells.values()) {
            objects.addAll(cell.getObjects());
        }
        return objects;
    }

    public <T extends GameObject> List<T> getAll(Class<T> cls) {

        List<T> objects = new ArrayList<>();

        for (GridCell cell : cells.values()) {
            for (GameObject obj : cell.getObjects()) {
                if (cls.isInstance(obj)){
                    objects.add((T) obj);
                }
            }
        }

        return objects;
    }
}
