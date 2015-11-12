package Core;

import GameObjects.GameObject;
import Grids.CollisionGrid;
import Grids.GridCell;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 12/11/15.
 */
public class CollisionHandler {

    private CollisionGrid grid = new CollisionGrid(10.0f);
    private boolean DRAW_COLLISION_POINTS = false;
    private List<CircleShape> collisionPoints = new ArrayList<>();

    public void insert(GameObject object){
        grid.insert(object);
    }

    public void setCollisionPointsVisible(boolean vis){
        DRAW_COLLISION_POINTS = vis;
    }

    public void resolveCollisions(List<GameObject> objects){
        List<GridCell> collisionCells = grid.getCells();

        for (GridCell cell : collisionCells) {
            List<GameObject> cellObjects = cell.getObjects();
            for (int i = 0; i < cellObjects.size() - 1; i++) {

                for (int j = i + 1; j < cellObjects.size(); j++) {


                    //see http://alexanderx.net/how-apply-collision/
                    //http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
                    //http://www.hoomanr.com/Demos/Elastic2/
                    //https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
                    //http://gamedev.stackexchange.com/questions/20516/ball-collisions-sticking-together
                    GameObject o1 = cellObjects.get(i);
                    GameObject o2 = cellObjects.get(j);

                    //don't bother wasting cycles on inactive objects
                    if(!o1.isActive() || !o2.isActive()){
                        continue;
                    }

                    boolean collision = false;

                    if (o1.isSolid() && o2.isSolid()) {

                        Vector2f pos1 = o1.getPosition();
                        Vector2f pos2 = o2.getPosition();
                        float dist = VectorMath.magnitude(Vector2f.sub(pos1, pos2));

                        if (o1.isColliding(o2)) {
                            collision = true;

                            if (DRAW_COLLISION_POINTS) {
                                float collisionPointX = (o1.getCenter().x * o2.getSize().x/2
                                        + o2.getCenter().x * o1.getSize().x/2)
                                        / (o1.getSize().x/2 + o2.getSize().x/2);
                                float collisionPointY = (o1.getCenter().y * o2.getSize().x/2
                                        + o2.getCenter().y * o1.getSize().x/2)
                                        / (o1.getSize().x/2 + o2.getSize().x/2);

                                CircleShape c = new CircleShape(4);
                                c.setOrigin(c.getRadius() / 2, c.getRadius() / 2);
                                c.setPosition(collisionPointX, collisionPointY);
                                collisionPoints.add(c);
                            }
                        }
                    }

                    if (collision) {
                        //we have to calculate the collision first, so that both objects use the same values during the calculation
                        o1.calculateCollision(o2);
                        o2.calculateCollision(o1);

                        //we then apply the calculated collisions in the next step
                        o1.applyCollision();
                        o2.applyCollision();
                    }
                }
            }
        }
    }

    public void reset(){
        grid.clear();
    }


    public void draw(RenderWindow window){
        if (DRAW_COLLISION_POINTS) {
            int count = 0;
            for (CircleShape c : collisionPoints) {
                window.draw(c);
                if (c.getRadius() < 1) {
                    count++;
                }
                c.setRadius(c.getRadius() - 1);
                c.setPosition(c.getPosition().x + 1, c.getPosition().y + 1);
            }

            collisionPoints.subList(count, collisionPoints.size()).clear();
        }
    }
}
