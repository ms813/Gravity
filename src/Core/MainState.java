package Core;

import GameObjects.Dust;
import GameObjects.GameObject;
import Grids.CollisionGrid;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import Grids.GridCell;
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

    public MainState(Game game) {
        super(game);

        for (int i = 0; i < 1000; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(300, 300));
            Dust d = new Dust(new Random().nextFloat() * 3f + 1, pos);
            d.setVelocity(new Vector2f(0.2f, 0));
            dustList.add(d);
        }

        for (int i = 0; i < 1000; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(500, 500));
            Dust d = new Dust(new Random().nextFloat() * 3f + 1, pos);
            d.setVelocity(new Vector2f(-0.2f, 0));
            dustList.add(d);
        }

        dustList.add(new Dust(10, new Vector2f(400, 400)));

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

        game.setView(guiView);
        game.getWindow().draw(label);

    }

    @Override
    public void update(float dt) {
        System.out.println("Frame start");

        //sort the dust so that the smaller particles are at the front
        //dustList.sort(new MassComparator());

        /*
        *   Populate the Collision Grid
        */
        long startTime = System.nanoTime();
        collisionGrid.clear();
        for (GameObject o : dustList) {
            collisionGrid.insert(o);
        }
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
            for (GameObject o : cellObjects) {

                if (mergingObjects.contains(o)) continue;   //ignore objects queued to be merged

                for (GameObject x : cellObjects) {
                    if (o == x) continue; //ignore collisions with self

                    if (mergingObjects.contains(x)) continue; //ignore objects queued to be merged

                    Vector2f xCenter = Vector2f.add(x.getPosition(), Vector2f.div(x.getSize(), 2));
                    Vector2f oCenter = Vector2f.add(o.getPosition(), Vector2f.div(o.getSize(), 2));

                    float dist = VectorMath.magnitude(Vector2f.sub(xCenter, oCenter));

                    GameObject larger, smaller;

                    if (o.getMass() > x.getMass()) {
                        larger = o;
                        smaller = x;
                    } else {
                        larger = x;
                        smaller = o;
                    }

                    //only need to check one dimension as all particles are currently symmetrical
                    if (dist < larger.getSize().x / 2) {
                        larger.merge(smaller);           //always keep the larger
                        mergingObjects.add(smaller);    //add the smaller to the list to be cleaned up
                    }
                }
            }
        }
        dustList.removeAll(mergingObjects);
        endTime = System.nanoTime();
        System.out.println("Collision checks took " + (endTime - startTime) / 1000000 + " millis");


        /*
        *   Populate the gravity grid
        */
        startTime = System.nanoTime();
        gravityGrid.clear();
        for (GameObject o : dustList) {
            gravityGrid.insert(o);
        }
        gravityGrid.updateProperties();   //Update the properties like Center of Mass for each cell
        endTime = System.nanoTime();

        // System.out.println("Building Gravity Grid took: " + (endTime - startTime) / 1000000 + " millis");


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
                    Vector2f o2Center = Vector2f.add(o2.getPosition(), Vector2f.div(o2.getSize(), 2));
                    Vector2f o1Center = Vector2f.add(o1.getPosition(), Vector2f.div(o1.getSize(), 2));

                    Vector2f diff = Vector2f.sub(o2Center, o1Center);
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
        System.out.println("Gravity calculations took: " + (endTime - startTime) / 1000000 + " millis");

        //run the update loop on all of the particles
        for (GameObject a : dustList) {
            a.update(dt);
        }
        label.setString("Particles remaining: " + dustList.size() + "\nFPS: " + Math.round(1 / dt));
        System.out.println("Frame end");
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
            x += (o.getPosition().x + o.getSize().x / 2) * o.getMass();
            y += (o.getPosition().y + o.getSize().y / 2) * o.getMass();
            mass += o.getMass();
        }

        return new Vector2f(x / mass, y / mass);
    }
}
