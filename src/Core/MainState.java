package Core;

import GameObjects.Dust;
import GameObjects.GameObject;
import Grids.CollisionGrid;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import Grids.GridCell;
import javafx.scene.shape.Circle;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class MainState extends GameState {

    private List<GameObject> dustList = new ArrayList<>();
    private CollisionGrid collisionGrid = new CollisionGrid(10.0f);
    private GravityGrid gravityGrid = new GravityGrid(50.0f);
    private View view = new View();
    private View guiView = new View();

    private Font font = new Font();
    private Text label = new Text();

    private Boolean DRAW_VELOCITY = false;
    private Boolean DRAW_TRAILS = false;
    private Boolean DRAW_GRID_COLLISION = false;
    private Boolean DRAW_GRID_GRAVITY = false;
    private Boolean DRAW_COLLISION_POINTS = true;

    private List<CircleShape> collisionPoints = new ArrayList<>();

    public MainState(Game game) {
        super(game);

        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(300, 300));
            float radius = (float) Math.random() * 25f + 1;
            Dust d = new Dust(radius, pos);
            d.setVelocity(new Vector2f(0.2f, 0.2f));
            dustList.add(d);
        }

        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(500, 500));
            Dust d = new Dust(new Random().nextFloat() * 25f + 1, pos);
            d.setVelocity(new Vector2f(-0.2f, -0.2f));
            dustList.add(d);
        }

        List overlapping = new ArrayList<>();
        for (GameObject o1 : dustList) {
            for (GameObject o2 : dustList) {
                if (o1 == o2) continue;
                if (o1.getBounds().intersection(o2.getBounds()) != null && !overlapping.contains(o1)) {
                    overlapping.add(o1);
                }
            }
        }
        System.out.println("number of overlapping starting objects removed: " + overlapping.size());
        dustList.removeAll(overlapping);


        Dust d1 = new Dust(25.0f, new Vector2f(200, 250));
        d1.setVelocity(new Vector2f(1, 0));

        Dust d2 = new Dust(25.0f, new Vector2f(400, 250));
        d2.setVelocity(new Vector2f(-1, 0));
        dustList.add(d1);
        dustList.add(d2);

        try {
            font.loadFromFile(Paths.get("resources/fonts/arial.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.setFont(font);
        label.setPosition(50, 50);
        label.setCharacterSize(24);
        label.setColor(Color.CYAN);
        label.setString("Particles remaining: " + dustList.size());

        view.setCenter(getSceneMassCenter());
        view.setSize(new Vector2f(game.getWindow().getSize()));
        game.setView(view);
    }

    @Override
    public void draw(float dt) {

        //view.setCenter(getSceneMassCenter());
        game.setView(view);
        RenderWindow window = game.getWindow();
        if (DRAW_GRID_COLLISION) collisionGrid.draw(window);
        if (DRAW_GRID_GRAVITY) gravityGrid.draw(window);
        for (GameObject o : dustList) {
            o.draw(window);
            if (DRAW_TRAILS) o.draw(window);
            if (DRAW_VELOCITY) o.draw(window);
        }

        if (DRAW_COLLISION_POINTS) {
            List<CircleShape> forRemoval = new ArrayList<>();
            for (CircleShape c : collisionPoints) {
                window.draw(c);
                if (c.getRadius() < 1) {
                    forRemoval.add(c);
                }
                c.setRadius(c.getRadius() - 1);
                c.setPosition(c.getPosition().x + 1, c.getPosition().y + 1);
            }

            collisionPoints.removeAll(forRemoval);
        }

        game.setView(guiView);
        game.getWindow().draw(label);
    }

    @Override
    public void update(float dt) {
        //System.out.println("Frame start");

        //sort the dust so that the smaller particles are at the front
        //dustList.sort(new MassComparator());

        /*
        *   Populate the Collision Grid
        */
        long startTime = System.nanoTime();
        collisionGrid.clear();

        dustList.forEach(collisionGrid::insert);

        long endTime = System.nanoTime();
        //System.out.println("Building Collision Grid took: " + (endTime - startTime) / 1000000 + " millis");

        /*
        *   Collision detection
        */

        startTime = System.nanoTime();
        List<GameObject> mergingObjects = new ArrayList<>(); //create an empty list to keep track of merging objects to be removed

        List<GridCell> collisionCells = collisionGrid.getCells();

        for (GridCell cell : collisionCells) {
            List<GameObject> cellObjects = cell.getObjects();
            for (GameObject o1 : cellObjects) {

                if (mergingObjects.contains(o1)) continue;   //ignore objects queued to be merged

                for (GameObject o2 : cellObjects) {
                    if (o1 == o2) continue; //ignore collisions with self
                    if (mergingObjects.contains(o2)) continue; //ignore objects queued to be merged

                    //see http://alexanderx.net/how-apply-collision/
                    //http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
                    //http://www.hoomanr.com/Demos/Elastic2/
                    //https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
                    //http://gamedev.stackexchange.com/questions/20516/ball-collisions-sticking-together

                    float dist = VectorMath.magnitude(Vector2f.sub(o1.getCenter(), o2.getCenter()));

                    //check squared distance between particle centers (cheaper than sqrt)
                    if (dist < o1.getCollisionRadius() + o2.getCollisionRadius()) {

                        float collisionPointX = (o1.getCenter().x * o2.getCollisionRadius()
                                + o2.getCenter().x * o1.getCollisionRadius())
                                / (o1.getCollisionRadius() + o2.getCollisionRadius());
                        float collisionPointY = (o1.getCenter().y * o2.getCollisionRadius()
                                + o2.getCenter().y * o1.getCollisionRadius())
                                / (o1.getCollisionRadius() + o2.getCollisionRadius());

                        CircleShape c = new CircleShape(4);
                        c.setOrigin(c.getRadius() / 2, c.getRadius() / 2);
                        c.setPosition(collisionPointX, collisionPointY);
                        collisionPoints.add(c);

                        float partialMass1 = 2 * o2.getMass() / (o1.getMass() + o2.getMass());
                        float partialMass2 = 2 * o1.getMass() / (o1.getMass() + o2.getMass());

                        float dot = VectorMath.dot(Vector2f.sub(o1.getVelocity(), o2.getVelocity()), Vector2f.sub(o1.getCenter(), o2.getCenter()));
                        float mag = VectorMath.magnitude(Vector2f.sub(o1.getCenter(), o2.getCenter()));

                        Vector2f final1 = Vector2f.sub(o1.getVelocity(), Vector2f.mul(Vector2f.sub(o1.getCenter(), o2.getCenter()), partialMass1 * (dot / (mag * mag))));
                        Vector2f final2 = Vector2f.sub(o2.getVelocity(), Vector2f.mul(Vector2f.sub(o2.getCenter(), o1.getCenter()), partialMass2 * (dot / (mag * mag))));

                        float eK1before = VectorMath.magnitude(o1.getKineticEnergy());
                        float eK2before = VectorMath.magnitude(o2.getKineticEnergy());

                        o1.move(final1);
                        o2.move(final2);
                        o1.setVelocity(final1);
                        o2.setVelocity(final2);

                        float eK1after =  VectorMath.magnitude(o1.getKineticEnergy());
                        float eK2after =  VectorMath.magnitude(o2.getKineticEnergy());
                        System.out.println("1b = " + eK1before + ", 1a = " + eK1after);
                        System.out.println("2b = " + eK2before + ", 2a = " + eK2after);
                        System.out.println("1 bind = " + o1.getBindingEnergy() + ", 2 bind = " + o2.getBindingEnergy());
                    }

                    //only need to check one dimension as all particles are currently symmetrical
                    /*
                    if (dist < larger.getSize().x / 2) {
                        larger.merge(smaller);           //always keep the larger
                        mergingObjects.add(smaller);    //add the smaller to the list to be cleaned up
                    }
                    */
                }
            }
        }
        dustList.removeAll(mergingObjects);
        endTime = System.nanoTime();
        //System.out.println("Collision checks took " + (endTime - startTime) / 1000000 + " millis");


        /*
        *   Populate the gravity grid
        */
        startTime = System.nanoTime();
        gravityGrid.clear();

        dustList.forEach(gravityGrid::insert);

        gravityGrid.updateProperties();   //Update the properties like Center of Mass for each cell
        endTime = System.nanoTime();

        //System.out.println("Building Gravity Grid took: " + (endTime - startTime) / 1000000 + " millis");


        /*
        *   Gravity calculations
        */
        startTime = System.nanoTime();
        List<GravityGridCell> gravityCells = gravityGrid.getCells(); //get a list of references to the cells in the collisionGrid
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

                    F = (G * m * M) / (float) Math.pow(r, 2);
                    //add it to the running total of forces acting on c1
                    totalForce = Vector2f.add(totalForce, Vector2f.mul(dir, F));
                }
            }

            for (GravityGridCell c2 : gravityCells) {
                if (c1 == c2) continue; //ignore own cell

                //Calculate the force from c2's center of mass acting on c1
                //F = GmM / r^2

                float F, G, m, M, r;
                Vector2f diff = Vector2f.sub(c2.getCenterOfMass(), c1.getCenterOfMass());
                Vector2f dir = VectorMath.normalize(diff);
                assert !Float.isNaN(dir.x) && !Float.isNaN(dir.y);

                r = VectorMath.magnitude(dir);
                if (r == 0) continue;

                G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                m = c1.getTotalMass();
                M = c2.getTotalMass();

                F = (G * m * M) / (float) Math.pow(r, 2);
                //add it to the running total of forces acting on c1
                totalForce = Vector2f.add(totalForce, Vector2f.mul(dir, F));
                // System.out.println(totalForce);
            }
            //System.out.println(totalForce);
            //apply the force to each particle in the cell
            for (GameObject o : c1.getObjects()) {
                o.applyForce(totalForce);
            }
        }
        endTime = System.nanoTime();
        //System.out.println("Gravity calculations took: " + (endTime - startTime) / 1000000 + " millis");

        //run the update loop on all of the particles
        for (GameObject a : dustList) {
            a.update(dt);
        }
        label.setString("Particles remaining: " + dustList.size() + "\nFPS: " + Math.round(1 / dt));
        //System.out.println("Frame end");
    }

    @Override
    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            }
        }
    }

    public Vector2f getSceneMassCenter() {
        float x = 0, y = 0, mass = 0;
        for (GameObject o : dustList) {
            x += o.getCenter().x * o.getMass();
            y += o.getCenter().y * o.getMass();
            mass += o.getMass();
        }

        return new Vector2f(x / mass, y / mass);
    }
}
