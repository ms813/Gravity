package Grids;

import GameObjects.GameObject;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 01/11/2015.
 */
public class GridCell {
    protected SpatialHashGrid parent;
    protected List<GameObject> objects = new ArrayList<>();
    protected Vector2i id;

    protected RectangleShape outline = new RectangleShape();
    protected Color color;

    public GridCell(SpatialHashGrid parent, Vector2i id) {
        this.parent = parent;
        this.id = id;

        outline.setPosition(Vector2f.mul(new Vector2f(id), parent.getCellSize()));
        outline.setFillColor(Color.TRANSPARENT);
        outline.setOutlineColor(Color.GREEN);
        outline.setOutlineThickness(-1.0f);
        outline.setSize(new Vector2f(parent.getCellSize(), parent.getCellSize()));

        /*
        int s = Math.round(parent.getCellSize());
        color = new Color((id.x * s)% 255, (s*id .y) % 255, ((id.x+id.y)*s) % 255);
        outline.setFillColor(color);
        */
    }

    public Vector2i getId() {
        return id;
    }

    public boolean insert(GameObject o) {
        //o.setFillColor(color);
        return objects.add(o);
    }

    public boolean contains(GameObject o) {
        return objects.contains(o);
    }

    public void draw(RenderWindow w) {
        w.draw(outline);
    }

    public List<GameObject> getObjects() {
        return objects;
    }
}
