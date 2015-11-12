package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import Grids.CollisionGrid;
import Grids.GravityGrid;
import Grids.GravityGridCell;
import Grids.GridCell;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class MainState extends GameState {

    private List<GameObject> colliders = new ArrayList<>();

    private CollisionGrid collisionGrid = new CollisionGrid(10.0f);
    private GravityGrid gravityGrid = new GravityGrid(50.0f);
    private View view = new View();
    private View guiView = new View();

    private Font font = new Font();
    private Text label = new Text();

    private boolean DRAW_GRID_COLLISION = false;
    private boolean DRAW_GRID_GRAVITY = false;
    private boolean DRAW_COLLISION_POINTS = true;

    private List<CircleShape> collisionPoints = new ArrayList<>();

    public MainState(Game game) {
        super(game);

        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(300, 300));
            float mass = (float) Math.random() * 10000f + 100f;
            //Vector2f vel = new Vector2f(2.0f*((float)Math.random()-0.5f), ((float)Math.random()-0.5f));
            Vector2f vel = Vector2f.ZERO;
            addAsteroid(mass, pos, vel);
        }

        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(500, 500));
            float mass = (float) Math.random() * 10000f + 100f;
            //Vector2f vel = new Vector2f(((float)Math.random()-0.5f), ((float)Math.random()-0.5f));
            Vector2f vel = Vector2f.ZERO;
            addAsteroid(mass, pos, vel);
        }


        colliders.add(new Asteroid(10f, new Vector2f(100, 400)));
        colliders.add(new Asteroid(20000f, new Vector2f(150, 400)));

        List overlapping = new ArrayList<>();
        for (GameObject o1 : colliders) {
            for (GameObject o2 : colliders) {
                if (o1 == o2) continue;
                if (o1.getBounds().intersection(o2.getBounds()) != null && !overlapping.contains(o1)) {
                    overlapping.add(o1);
                }
            }
        }

        System.out.println("number of overlapping starting objects removed: " + overlapping.size());
        colliders.removeAll(overlapping);

        try {
            font.loadFromFile(Paths.get("resources/fonts/arial.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.setFont(font);
        label.setPosition(50, 50);
        label.setCharacterSize(24);
        label.setColor(Color.CYAN);
        label.setString("Particles remaining: " + colliders.size());

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

        for (GameObject o : colliders) {
            if(o.isVisible() && o.isActive()){
                o.draw(window);
            }
        }

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

        game.setView(guiView);
        game.getWindow().draw(label);

        game.setView(view);
    }

    @Override
    public void update(float dt) {
        // System.out.println("Update start");

        //sort the dust so that the smaller particles are at the front
        //colliders.sort(new MassComparator());

        /*
        *   Populate the Collision Grid
        */
        long startTime = System.nanoTime();
        collisionGrid.clear();

        colliders.forEach(collisionGrid::insert);

        long endTime = System.nanoTime();
        // System.out.println("Building Collision Grid took: " + (endTime - startTime) / 1000000 + " millis");

        /*
        *   Collision detection
        */

        startTime = System.nanoTime();

        List<GridCell> collisionCells = collisionGrid.getCells();

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

        endTime = System.nanoTime();
        //System.out.println("Collision checks took " + (endTime - startTime) / 1000000 + " millis");

        /*
        *   Populate the gravity grid
        */
        startTime = System.nanoTime();
        gravityGrid.clear();

        colliders.forEach(gravityGrid::insert);

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

                    Vector2f diff = Vector2f.sub(o2.getCenter(), o1.getCenter());
                    Vector2f dir = VectorMath.normalize(diff);
                    assert !Float.isNaN(dir.x) && !Float.isNaN(dir.y);

                    r = VectorMath.magnitude(dir);
                    if (r == 0) continue;

                    G = GlobalConstants.GRAVITATIONAL_CONSTANT;
                    m = o1.getMass();
                    M = o2.getMass();

                    F = (G * m * M) / r;
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

                    F = (G * m * M) / r;
                    //add it to the running total of forces acting on c1
                    totalForce = Vector2f.add(totalForce, Vector2f.mul(dir, F));
                    // System.out.println(totalForce);
                }
                o1.applyForce(totalForce);
            }
        }

        endTime = System.nanoTime();
        //System.out.println("Gravity calculations took: " + (endTime - startTime) / 1000000 + " millis");

        //run the update loop on all of the particles
        for (GameObject a : colliders) {
            a.update(dt);
        }

        label.setString("Particles remaining: " + colliders.size() + "\nFPS: " + Math.round(1 / dt));
        // System.out.println("Frame end");
    }

    @Override
    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            } else if (e.type == Event.Type.MOUSE_BUTTON_PRESSED) {
                MouseButtonEvent mouseEvt = e.asMouseButtonEvent();
                if (mouseEvt.button == Mouse.Button.LEFT) {
                    Vector2f worldPos = game.mapPixelToCoords(mouseEvt.position);

                }
            }
        }
    }

    private void addAsteroid(float mass, Vector2f pos) {
        colliders.add(new Asteroid(mass, pos));
    }

    private void addAsteroid(float mass, Vector2f pos, Vector2f vel) {
        Asteroid a = new Asteroid(mass, pos);
        a.setVelocity(vel);
        colliders.add(a);
    }

    public Vector2f getSceneMassCenter() {
        float x = 0, y = 0, mass = 0;
        for (GameObject o : colliders) {
            x += o.getCenter().x * o.getMass();
            y += o.getCenter().y * o.getMass();
            mass += o.getMass();
        }

        return new Vector2f(x / mass, y / mass);
    }
}
