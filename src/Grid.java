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
    private int rows, cols;
    private Vector2f origin = Vector2f.ZERO;
    private Vector2f size;
    private float cellSize;

    public Grid(Vector2f origin, Vector2f sceneSize, float cellSize) {
        this.size = sceneSize;
        this.cellSize = cellSize;
        this.origin = origin;
        setBounds(new FloatRect(origin, size));
    }

    public void setBounds(FloatRect bounds) {
        origin = new Vector2f(bounds.left, bounds.top);
        size = new Vector2f(bounds.width, bounds.height);
        cols = (int) Math.ceil(size.x / cellSize);
        rows = (int) Math.ceil(size.y / cellSize);
    }

    public void insert(GameObject o) {
        List<Vector2i> cellIds = getCellsForObj(o);

        for(Vector2i id : cellIds){
            if(cells.get(id) == null){
                cells.put(id, new ArrayList<>());
            }

            if(!cells.get(id).contains(o)){
                cells.get(id).add(o);
            }
        }
    }

    private ArrayList<Vector2i> getCellsForObj(GameObject o) {
        ArrayList<Vector2i> ids = new ArrayList<>();

        Vector2f topLeft = o.getPosition();
        Vector2f bottomRight = Vector2f.add(o.getPosition(), o.getSize());

        int objCols = (int) Math.ceil((bottomRight.x - topLeft.x) / cellSize);
        int objRows = (int) Math.ceil((bottomRight.y - topLeft.y)/ cellSize);

        for(int i = 0; i < objCols; i++){
            for(int j = 0; j < objRows; j++){
                Vector2i v = new Vector2i((int) Math.floor(topLeft.x / cellSize + i), (int) Math.floor(topLeft.y / cellSize + j));
                ids.add(v);
            }
        }

        return ids;
    }

    public void draw(RenderWindow w){
        for(Vector2i entry : cells.keySet()){

            RectangleShape r = new RectangleShape();
            r.setPosition(Vector2f.mul(new Vector2f(entry), cellSize));
            r.setFillColor(Color.TRANSPARENT);
            r.setOutlineColor(Color.GREEN);
            r.setOutlineThickness(1.0f);
            r.setSize(new Vector2f(cellSize, cellSize));

            w.draw(r);
        }
    }

    public void clear(){
        cells.clear();
    }

    public HashMap<Vector2i, ArrayList<GameObject>> getCells(){
        return cells;
    }

    public void log(){
        System.out.println(cells);
    }
}
