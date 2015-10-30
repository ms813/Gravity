import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 30/10/2015.
 */
public class Grid {

    private HashMap<Vector2i, ArrayList<GameObject>> cells = new HashMap<>();
    private float cellSize;

    public Grid(float cellSize) {
        this.cellSize = cellSize;
    }

    public void insert(GameObject o) {
        List<Vector2i> cellIds = getCellsForObj(o);

        for (Vector2i id : cellIds) {
            if (cells.get(id) == null) {
                cells.put(id, new ArrayList<>());
            }

            if (!cells.get(id).contains(o)) {
                cells.get(id).add(o);
            }
        }
    }

    private ArrayList<Vector2i> getCellsForObj(GameObject o) {

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

    public void draw(RenderWindow w) {
        for (Vector2i entry : cells.keySet()) {

            RectangleShape r = new RectangleShape();
            r.setPosition(Vector2f.mul(new Vector2f(entry), cellSize));
            r.setFillColor(Color.TRANSPARENT);
            r.setOutlineColor(Color.GREEN);
            r.setOutlineThickness(1.0f);
            r.setSize(new Vector2f(cellSize, cellSize));

            w.draw(r);
        }
    }

    public void clear() {
        cells.clear();
    }

    public HashMap<Vector2i, ArrayList<GameObject>> getCells() {
        return cells;
    }
}
