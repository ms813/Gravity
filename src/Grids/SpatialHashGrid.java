package Grids;

import GameObjects.GameObject;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 30/10/2015.
 */
public interface SpatialHashGrid {

    void insert(GameObject o);

    ArrayList<Vector2i> getCellsForObj(GameObject o);

    void draw(RenderWindow w);

    void clear();

    List getCells();

    float getCellSize();
}
