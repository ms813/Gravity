package Core;

import Components.Collider;
import GameObjects.Entity;
import Grids.CollisionGrid;
import Grids.GridCell;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.RenderWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 12/11/15.
 */
public class CollisionHandler {

    private CollisionGrid grid = new CollisionGrid(10.0f);
    private boolean COLLISION_POINTS_VISIBLE = true;
    private boolean GRID_VISIBLE = false;
    private List<CircleShape> collisionPoints = new ArrayList<>();
    private List<Entity> objectsToRemove = new ArrayList<>();

    public CollisionHandler(float gridSize) {
        grid = new CollisionGrid(gridSize);
    }

    public void showGrid() {
        GRID_VISIBLE = true;
    }

    public void hideGrid() {
        GRID_VISIBLE = false;
    }

    public void showCollisionPoints() {
        COLLISION_POINTS_VISIBLE = true;
    }

    public void hideCollisionPoints() {
        COLLISION_POINTS_VISIBLE = false;
    }

    public void resolveCollisions() {
        List<GridCell> collisionCells = grid.getCells();

        for (GridCell cell : collisionCells) {
            List<Collider> cellObjects = cell.getColliders();
            for (int i = 0; i < cellObjects.size() - 1; i++) {

                for (int j = i + 1; j < cellObjects.size(); j++) {


                    //see http://alexanderx.net/how-apply-collision/
                    //http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
                    //http://www.hoomanr.com/Demos/Elastic2/
                    //https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
                    //http://gamedev.stackexchange.com/questions/20516/ball-collisions-sticking-together
                    Collider col1 = cellObjects.get(i);
                    Collider col2 = cellObjects.get(j);

                    //check that colliders are colliding
                    if (col1.isColliding(col2))
                    {
                        if (COLLISION_POINTS_VISIBLE) {
                            float collisionPointX = (col1.getCenter().x * col2.getSize().x / 2
                                    + col2.getCenter().x * col1.getSize().x / 2)
                                    / (col1.getSize().x / 2 + col2.getSize().x / 2);
                            float collisionPointY = (col1.getCenter().y * col2.getSize().x / 2
                                    + col2.getCenter().y * col1.getSize().x / 2)
                                    / (col1.getSize().x / 2 + col2.getSize().x / 2);

                            CircleShape c = new CircleShape(4);
                            c.setOrigin(c.getRadius() / 2, c.getRadius() / 2);
                            c.setPosition(collisionPointX, collisionPointY);
                            collisionPoints.add(c);
                        }

                        //we have to calculate the collision first, so that both colliders use the same values during the calculation
                        col1.createCollisionEvent(col2);
                        col2.createCollisionEvent(col1);

                        //we then apply the calculated collisions in the next step
                        col1.applyCollisions();
                        col2.applyCollisions();

                        /*
                        if (col1.isDestroyOnHit()) {
                            objectsToRemove.add(col1);
                        }

                        if (col2.isDestroyOnHit()) {
                            objectsToRemove.add(col2);
                        }
                        */
                    }
                }
            }
        }
    }

    public void reset() {
        grid.clear();
        collisionPoints.clear();
    }

    public void insertAll(List<Collider> colliders) {
        for (Collider collider : colliders) {
            insert(collider);
        }
    }

    public void insert(Collider collider) {
        grid.insert(collider);
    }

    public void draw(RenderWindow window) {
        if (GRID_VISIBLE) grid.draw(window);
        if (COLLISION_POINTS_VISIBLE) {
            int count = 0;
            for (CircleShape c : collisionPoints) {
                window.draw(c);
                if (c.getRadius() < 1) {
                    count++;
                }
                c.setRadius(c.getRadius() - 0.5f);
                c.setPosition(c.getPosition().x + 1, c.getPosition().y + 1);
            }
            collisionPoints.subList(count, collisionPoints.size()).clear();
        }
    }
}



