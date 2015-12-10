package GameStates;

import Core.*;
import GameObjects.Asteroid;
import GameObjects.Creep;
import GameObjects.GameObject;
import GameObjects.Planet;
import GameObjects.Tools.CreepSpawner;
import GameObjects.Tools.Turret;
import GameObjects.Tools.TurretPlatform;
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

/**
 * Created by Matthew on 04/12/2015.
 */
public class LevelTestState implements GameState {

    private Game game;

    private List<GameObject> sceneObjects = new ArrayList<>();

    private CollisionHandler collisionHandler = new CollisionHandler(10);
    private GravityHandler gravityHandler = new GravityHandler(50);
    private TargetingHandler targetingHandler = new TargetingHandler();

    private CreepSpawner creepSpawner;

    private View view = new View();
    private View guiView = new View();

    private Font font = new Font();
    private Text label = new Text();

    private boolean VERLET_STATE = false;

    private GameObject focusedObject = null;

    public LevelTestState(Game game) {
        this.game = game;
        //collisionHandler.showGrid();

        /*
        for (int i = 0; i < 1000; i++) {

            float x = (float) (Math.random() - 0.5f) * 1500;
            float y = (float) (Math.random() - 0.5f) * 1500;
            Vector2f pos = new Vector2f(x, y);
            float mass = (float) Math.random() * 10000f + 100f;
            Vector2f vel = new Vector2f(((float) Math.random() - 0.5f), ((float) Math.random() - 0.5f));
            addAsteroid(mass, pos, vel);
        }
        */

        Planet p = new Planet(10000000f, Vector2f.ZERO);
        sceneObjects.add(p);

        /*
        *   Stable circular orbit velocity = sqrt(Gm / 2 R)
        *
        */

        float asteroidHeight = 300f;
        float asteroidMass = 10000f;

        float vel = (float) Math.sqrt(GlobalConstants.GRAVITATIONAL_CONSTANT * p.getMass() /(2* asteroidHeight));

        addAsteroid(asteroidMass, new Vector2f(0, asteroidHeight), new Vector2f(vel, 0));

        List<GameObject> overlapping = new ArrayList<>();
        for (GameObject o1 : sceneObjects) {
            for (GameObject o2 : sceneObjects) {
                if (o1 == o2) continue;
                if (o1.getBounds().intersection(o2.getBounds()) != null && !overlapping.contains(o1)) {
                    overlapping.add(o1);
                }
            }
        }

        try {
            font.loadFromFile(Paths.get("resources/fonts/arial.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.setFont(font);
        label.setPosition(50, 50);
        label.setCharacterSize(24);
        label.setColor(Color.CYAN);

        System.out.println("number of overlapping starting objects removed: " + overlapping.size());
        sceneObjects.removeAll(overlapping);


        view.setCenter(getSceneMassCenter());
        view.setSize(new Vector2f(game.getWindow().getSize()));
        game.setView(view);

        creepSpawner = new CreepSpawner(new Vector2f(0, -asteroidHeight), new Vector2f(-vel, 0));

        sceneObjects.add(creepSpawner);
        creepSpawner.startNextLevel();
    }

    @Override
    public void draw(float dt) {
        centerView(focusedObject);

        game.setView(view);
        RenderWindow window = game.getWindow();

        for (GameObject o : sceneObjects) {
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

        //check for dead creeps and remove them
        cleanSceneObjects();

        //velocity Verlet requires all of the positions to be updated first, then the velocities
        if (VERLET_STATE) {
            /*
            *   On the Verlet frame, do anything to do with position including:
            *      --- collision detection
            *      --- assigning targets
            */
            collisionHandler.reset();
            collisionHandler.insertAll(sceneObjects);
            collisionHandler.resolveCollisions();

            targetingHandler.insertAll(sceneObjects);
            targetingHandler.assignTargets();


        } else {
            /*
            *   Off the Verlet frame, do anything to do with velocity including:
            *      --- gravity calculations
            */
            gravityHandler.reset();
            gravityHandler.insertAll(sceneObjects);
            gravityHandler.recalculatePhysicalProperties();   //Update the properties like Center of Mass for each cell

            for (GameObject o : sceneObjects) {
                o.applyForce(gravityHandler.getForce(o));
            }

        }

        creepSpawner.setDominantGravityObject(gravityHandler.getDominantObject(creepSpawner, Planet.class));

        for (GameObject o : sceneObjects) {
            o.update(dt, VERLET_STATE);
        }

        /*
        *   Check if creeps have spawned and add them
        */
        while(!creepSpawner.isEmpty()){
            sceneObjects.add(creepSpawner.poll());
        }

        //toggle the Verlet state
        VERLET_STATE = !VERLET_STATE;

        label.setString("Creeps remaining: " + creepSpawner.getCreepsRemaining() + "\nFPS: " + Math.round(1 / dt));
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

                    for (GameObject o : sceneObjects) {
                        if (o.getBounds().contains(worldPos)) {
                            focusedObject = o;
                            if (o instanceof TurretPlatform) {
                                ((TurretPlatform) o).addTurret(new Turret(o));
                            }
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

    private void addAsteroid(float mass, Vector2f pos, Vector2f vel) {
        Asteroid a = new Asteroid(mass, pos);
        a.setVelocity(vel);
        sceneObjects.add(a);
    }

    public Vector2f getSceneMassCenter() {
        float x = 0, y = 0, mass = 0;
        for (GameObject o : sceneObjects) {
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

    @Override
    public void addGameObject(GameObject object) {
        if (!sceneObjects.contains(object)) {
            sceneObjects.add(object);
        } else {
            System.err.println(this + " already has object " + object + " on its object list");
        }
    }

    private void cleanSceneObjects() {

        List<GameObject> objsForDestruction = new ArrayList<>();

        for (GameObject object : sceneObjects) {
            if (object.getDestroyFlag()) {
                objsForDestruction.add(object);
            }
        }

        sceneObjects.removeAll(objsForDestruction);
    }
}
