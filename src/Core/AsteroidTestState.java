package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class AsteroidTestState implements GameState {

    private Game game;

    private List<GameObject> asteroids = new ArrayList<>();
    private CollisionHandler collisionHandler = new CollisionHandler(10);

    private GravityHandler gravityHandler = new GravityHandler(50);

    private View view = new View();
    private View guiView = new View();

    private Font font = new Font();
    private Text label = new Text();

    private boolean VERLET_STATE = false;

    private GameObject focusedObject = null;

    public AsteroidTestState(Game game) {
        this.game = game;
        collisionHandler.showGrid();

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


        asteroids.add(new Asteroid(10f, new Vector2f(100, 400)));
        asteroids.add(new Asteroid(20000f, new Vector2f(150, 400)));

        List overlapping = new ArrayList<>();
        for (GameObject o1 : asteroids) {
            for (GameObject o2 : asteroids) {
                if (o1 == o2) continue;
                if (o1.getBounds().intersection(o2.getBounds()) != null && !overlapping.contains(o1)) {
                    overlapping.add(o1);
                }
            }
        }

        System.out.println("number of overlapping starting objects removed: " + overlapping.size());
        asteroids.removeAll(overlapping);

        try {
            font.loadFromFile(Paths.get("resources/fonts/arial.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.setFont(font);
        label.setPosition(50, 50);
        label.setCharacterSize(24);
        label.setColor(Color.CYAN);
        label.setString("Particles remaining: " + asteroids.size());

        view.setCenter(getSceneMassCenter());
        view.setSize(new Vector2f(game.getWindow().getSize()));
        game.setView(view);
    }

    @Override
    public void draw(float dt) {

        //view.setCenter(getSceneMassCenter());

        centerView(focusedObject);

        game.setView(view);
        RenderWindow window = game.getWindow();

        for (GameObject o : asteroids) {
            if (o.isVisible() && o.isActive()) {
                o.draw(window);
            }
        }

        collisionHandler.draw(window);
        gravityHandler.draw(window);

        game.setView(guiView);
        game.getWindow().draw(label);

        game.setView(view);
    }

    @Override
    public void update(float dt) {
        // System.out.println("Update start");

        //sort the dust so that the smaller particles are at the front
        //asteroids.sort(new MassComparator());

        /*
        *   Populate the Collision Grid
        */

        collisionHandler.reset();
        collisionHandler.insertAll(asteroids);

        /*
        *   Collision detection
        */
        collisionHandler.resolveCollisions();

        /*
        *   Populate the gravity grid
        */
        gravityHandler.reset();
        gravityHandler.insertAll(asteroids);

        gravityHandler.recalculatePhysicalProperties();   //Update the properties like Center of Mass for each cell

        //velocity Verlet requires all of the positions to be updated first, then the velocities
        if (VERLET_STATE) {
            for (GameObject o : asteroids) {
                o.updatePosition(dt);
            }
        } else {
            for (GameObject o : asteroids) {
                o.applyForce(gravityHandler.getForce(o));
                o.updateVelocity(dt);
            }
        }
        VERLET_STATE = !VERLET_STATE;

        label.setString("Particles remaining: " + asteroids.size() + "\nFPS: " + Math.round(1 / dt));
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

                    for (GameObject o : asteroids) {
                        if (o.getBounds().contains(worldPos)) {
                            focusedObject = o;
                        }
                    }
                }
            } else if (e.type == Event.Type.KEY_PRESSED) {
                KeyEvent keyEvt = e.asKeyEvent();
                if (keyEvt.key == Keyboard.Key.ESCAPE) {
                    focusedObject = null;
                }
            }
        }
    }

    private void addAsteroid(float mass, Vector2f pos) {
        addAsteroid(mass, pos, Vector2f.ZERO);
    }

    private void addAsteroid(float mass, Vector2f pos, Vector2f vel) {
        Asteroid a = new Asteroid(mass, pos);
        a.setVelocity(vel);
        asteroids.add(a);
    }

    public Vector2f getSceneMassCenter() {
        float x = 0, y = 0, mass = 0;
        for (GameObject o : asteroids) {
            x += o.getCenter().x * o.getMass();
            y += o.getCenter().y * o.getMass();
            mass += o.getMass();
        }

        return new Vector2f(x / mass, y / mass);
    }

    private void centerView(GameObject object) {
        if (object == null) {
            view.setCenter(getSceneMassCenter());
            view.setSize(new Vector2f(game.getWindow().getSize()));
        } else {
            Vector2f size = new Vector2f(game.getWindow().getSize());
            view.setCenter(object.getCenter());

            float ZOOM = 0.1f;

            if (object.getSize().x >= object.getSize().y) {
                view.setSize(1 / ZOOM * object.getSize().x * size.x / size.y, 1 / ZOOM * object.getSize().x);
            } else {
                view.setSize(1 / ZOOM * object.getSize().y * size.x / size.y, 1 / ZOOM * object.getSize().y);
            }
        }
    }

}
