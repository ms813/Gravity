package Core;

import GameObjects.GameObject;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.List;

/**
 * Created by smithma on 17/11/15.
 */
public class GravityHandler {

    private GravityGrid grid = new GravityGrid(50.0f);

    private boolean GRID_VISIBLE = false;

    public void showGrid(){
        GRID_VISIBLE = true;
    }

    public void hideGrid(){
        GRID_VISIBLE = false;
    }

    public void draw(RenderWindow window){
        if(GRID_VISIBLE){
            grid.draw(window);
        }
    }

    public void applyGravityForces(){

        List<GravityGridCell> gravityCells = grid.getCells(); //get a list of references to the cells in the collisionGrid
        for (GravityGridCell c1 : gravityCells) {

            Vector2f totalForce = Vector2f.ZERO; //running total of forces acting on c1

            List<GameObject> c1Objs = c1.getObjects();
            for (GameObject o1 : c1Objs) {
                for (GameObject o2 : c1Objs) {
                    if (o1 == o2) continue;
                    float F, G, m, M, r;

                    Vector2f diff = Vector2f.sub(o2.getCenter(), o1.getCenter());
                    Vector2f dir = VectorMath.normalize(diff);
                    assert !Float.isNaN(dir.x) && !Float.isNaN(dir.y);

                    r = VectorMath.magnitude(dir);
                    if (r == 0) continue;

                    G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                    m = o1.getMass();
                    M = o2.getMass();

                    F = (G * m * M) / (r*r);
                    //add it to the running total of forces acting on c1
                    totalForce = Vector2f.add(totalForce, Vector2f.mul(dir, F));
                }

                for (GravityGridCell c2 : gravityCells) {
                    if (c1 == c2) continue; //ignore own cell

                    //Calculate the force from c2's center of mass acting on c1
                    //F = GmM / r in 2D (not over r squared!)

                    float F, G, m, M, r;
                    Vector2f diff = Vector2f.sub(c2.getCenterOfMass(), c1.getCenterOfMass());
                    Vector2f dir = VectorMath.normalize(diff);
                    assert !Float.isNaN(dir.x) && !Float.isNaN(dir.y);

                    r = VectorMath.magnitude(dir);
                    if (r == 0) continue;

                    G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                    m = c1.getTotalMass();
                    M = c2.getTotalMass();

                    F = (G * m * M) / (r*r);
                    //add it to the running total of forces acting on c1
                    totalForce = Vector2f.add(totalForce, Vector2f.mul(dir, F));
                    // System.out.println(totalForce);
                }
                o1.applyForce(totalForce);
            }
        }
    }

    public void reset(){
        grid.clear();
    }

    public void insertAll(List<GameObject> objects){
        for(GameObject object : objects){
            insert(object);
        }
    }

    public void insert(GameObject object){
        grid.insert(object);
        if(object.getChildren().size() > 0){
            insertAll(object.getChildren());
        }
    }

    public void recalculatePhysicalProperties(){
        grid.recalculatePhysicalProperties();
    }
}
