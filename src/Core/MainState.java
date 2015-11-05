package Core;

import GameObjects.Asteroid;
import GameObjects.Colliders.Collider;
import GameObjects.Colliders.DiffuseCollider;
import GameObjects.Colliders.SolidCollider;
import GameObjects.DustCloud;
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
import org.w3c.dom.css.Rect;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
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

    private Boolean DRAW_GRID_COLLISION = false;
    private Boolean DRAW_GRID_GRAVITY = false;
    private Boolean DRAW_COLLISION_POINTS = false;

    private List<CircleShape> collisionPoints = new ArrayList<>();
    private List<RectangleShape> collisionIntersects = new ArrayList<>();

    public MainState(Game game) {
        super(game);
/*
        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(300, 300));
            float mass = (float) Math.random() * 10000f + 100f;
            Vector2f vel = Vector2f.ZERO;
            addAsteroid(mass, pos, vel);
        }

        for (int i = 0; i < 50; i++) {
            Vector2f pos = Vector2f.add(Vector2f.mul(VectorMath.randomUnit(), new Random().nextFloat() * 300), new Vector2f(500, 500));
            float mass = (float) Math.random() * 10000f + 100f;
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
        */


        addAsteroid(10000f, new Vector2f(200, 200), VectorMath.RIGHT);
        //addAsteroid(10000f, new Vector2f(400, 210), VectorMath.LEFT);

        DustCloud cloud = new DustCloud(1000f, new Vector2f(350, 200));
        colliders.add(cloud);

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
            o.draw(window);
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
            System.out.println(collisionPoints.size());


            Iterator<RectangleShape> iter = collisionIntersects.iterator();
            while (iter.hasNext()) {
                RectangleShape shape = iter.next();
                window.draw(shape);
                Vector2f size = new Vector2f(shape.getGlobalBounds().width, shape.getGlobalBounds().height);
                if (size.x < 2 || size.y < 2) {
                    iter.remove();
                }
                shape.setSize(new Vector2f(0.75f * size.x, 0.75f * size.y));
                shape.setPosition(shape.getPosition().x + 0.25f * size.x, shape.getPosition().y + 0.25f * size.y);
            }
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
                    if (cellObjects.get(i) == cellObjects.get(j)) {
                        System.out.println("fail");
                        continue; //ignore collisions with self
                    }

                    //see http://alexanderx.net/how-apply-collision/
                    //http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
                    //http://www.hoomanr.com/Demos/Elastic2/
                    //https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
                    //http://gamedev.stackexchange.com/questions/20516/ball-collisions-sticking-together

                    Collider col1 = cellObjects.get(i).getCollider();
                    Collider col2 = cellObjects.get(j).getCollider();
                    boolean collision = false;

                    if (col1 instanceof SolidCollider && col2 instanceof SolidCollider) {
                        //2 solid objects are colliding
                        SolidCollider solidCol1 = (SolidCollider) col1;
                        SolidCollider solidCol2 = (SolidCollider) col2;

                        float dist = VectorMath.magnitude(Vector2f.sub(solidCol1.getCenter(), solidCol2.getCenter()));
                        if (dist < solidCol1.getRadius() + solidCol2.getRadius()) {
                            collision = true;

                            if (DRAW_COLLISION_POINTS) {
                                float collisionPointX = (solidCol1.getCenter().x * solidCol2.getRadius()
                                        + solidCol2.getCenter().x * solidCol1.getRadius())
                                        / (solidCol1.getRadius() + solidCol2.getRadius());
                                float collisionPointY = (solidCol1.getCenter().y * solidCol2.getRadius()
                                        + solidCol2.getCenter().y * solidCol1.getRadius())
                                        / (solidCol1.getRadius() + solidCol2.getRadius());

                                CircleShape c = new CircleShape(4);
                                c.setOrigin(c.getRadius() / 2, c.getRadius() / 2);
                                c.setPosition(collisionPointX, collisionPointY);
                                collisionPoints.add(c);
                            }
                        }


                    } else {
                        FloatRect intersect = col1.getBounds().intersection(col2.getBounds());

                        if (intersect != null) {
                            collision = true;

                            if (DRAW_COLLISION_POINTS) {
                                RectangleShape r = new RectangleShape(new Vector2f(intersect.width, intersect.top));
                                r.setPosition(intersect.left, intersect.top);
                                collisionIntersects.add(r);
                            }
                        }
                    }


                    if (collision) {
                        //we have to calculate the collision first, so that both objects use the same values during the calculation
                        col1.calculateCollision(col2);
                        col2.calculateCollision(col1);

                        //we then apply the calculated collisions in the next step
                        col1.applyCollision(col2);
                        col2.applyCollision(col1);
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
                    addAsteroid(15000f, worldPos);
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
