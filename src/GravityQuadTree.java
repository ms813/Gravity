import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 27/10/2015.
 */
public class GravityQuadTree {

    private GravityQuadTree parent;

    private int MAX_OBJECTS = 5;
    private int MAX_LEVELS = 10;

    private int level;
    private List<GameObject> objects;

    private FloatRect bounds;
    private GravityQuadTree[] nodes;

    Vector2f centerOfGravity;
    float totalMass;

    public GravityQuadTree(int level, FloatRect bounds, GravityQuadTree parent) {
        this.level = level;
        objects = new ArrayList<>();
        this.bounds = bounds;
        nodes = new GravityQuadTree[4];
        this.parent = parent;
    }

    public void printStatus() {
        if(parent != null)
        System.out.println("parent : " + parent + ", parent level: "+parent.level + ", my level: " + level + ", objs: " + objects);
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].printStatus();
            }
        }
    }

    /*
    *   Draws a box around each quad
    */
    public void draw(RenderWindow window) {
        RectangleShape outline = new RectangleShape(new Vector2f(bounds.width, bounds.height));
        outline.setPosition(bounds.left, bounds.top);
        outline.setFillColor(Color.TRANSPARENT);
        outline.setOutlineThickness(1.0f);
        outline.setOutlineColor(Color.GREEN);

        window.draw(outline);

        for (GravityQuadTree t : nodes) {
            if (t != null) {
                t.draw(window);
            }
        }
    }

    /*
    *    Clears the quadtree
    */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /*
    *   Splits the node into 4 subnodes
    */
    public void split() {
        float subWidth = bounds.width / 2;
        float subHeight = bounds.height / 2;
        float x = bounds.left;
        float y = bounds.top;

        //top right
        nodes[0] = new GravityQuadTree(level + 1, new FloatRect(x + subWidth, y, subWidth, subHeight), this);

        //top left
        nodes[1] = new GravityQuadTree(level + 1, new FloatRect(x, y, subWidth, subHeight), this);

        //bottom left
        nodes[2] = new GravityQuadTree(level + 1, new FloatRect(x, y + subHeight, subWidth, subHeight), this);

        //bottom right
        nodes[3] = new GravityQuadTree(level + 1, new FloatRect(x + subWidth, y + subHeight, subWidth, subHeight), this);
    }

    /*
    *   Determine which node the object belongs to.
    *   -1 means object cannot completely fit within a child node
    *   and is part of the parent node
    *    ___________
    *   |  1  |  0  |
    *   |_____|_____|
    *   |  2  |  3  |
    *   |_____|_____|
    *
    */
    private int getIndex(GameObject object) {
        int index = -1;

        //pos should be the center of the object
        Vector2f pos = object.getPosition();
        Vector2f size = new Vector2f(object.getBounds().width, object.getBounds().height);

        float verticalMidpoint = bounds.left + (bounds.width / 2);
        float horizontalMidpoint = bounds.top + (bounds.height / 2);

        //Object can completely fit within top quadrants
        boolean topQuadrant = pos.y < horizontalMidpoint && pos.y + size.y < horizontalMidpoint;

        //object can completely fit within the bottom quadrants
        boolean bottomQuadrant = pos.y > horizontalMidpoint;

        //object can fit completely within the left quadrants
        if (pos.x < verticalMidpoint && pos.x + size.x < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        //object can fit completely in the right quadrants
        else if (pos.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /*
    *   Insert the object into the quadtree.
    *   If the node exceeds the capacity, it will split
    *   and add all objects to their corresponding nodes
    */
    public void insert(GameObject obj) {

        //if this quad has children, put the object straight into the children
        if (nodes[0] != null) {
            int index = getIndex(obj);
            if (index != -1) {
                nodes[index].insert(obj);
                return;
            }
        }

        //if it has no children, add the object to this level
        objects.add(obj);

        float x = 0, y = 0;
        totalMass = 0;
        for(GameObject o : objects){
            x += o.getPosition().x * o.getMass();
            y += o.getPosition().y * o.getMass();

            totalMass += o.getMass();
        }

        centerOfGravity = new Vector2f(x / totalMass, y / totalMass);

        //if this level has too many objects, split the quad into 4 child quads
        if (objects.size() > MAX_OBJECTS) {
            if (level < MAX_LEVELS) {
                if (nodes[0] == null) {
                    split();
                }

                //and distribute its objects into the child quads
                int i = 0;
                while (i < objects.size()) {
                    int index = getIndex(objects.get(i));
                    if (index != -1) {
                        nodes[index].insert(objects.get(i));
                        objects.remove(i);
                    } else {
                        i++;
                    }
                }
            } else {
                throw new IndexOutOfBoundsException("[GravityQuadTree.insert()] Max tree level reached");
            }
        }
    }

    /*
    *   Return all objects in the same quad as the given object
    */
    public List retrieve(List<GameObject> returnObjects, GameObject object) {
        int index = getIndex(object);

        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, object);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void setBounds(FloatRect bounds) {
        this.bounds = bounds;
    }
}




