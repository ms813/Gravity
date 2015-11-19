package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import GameObjects.Planet;
import GameObjects.Spaceship;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 17/11/15.
 */
public class PlanetTestState implements GameState {

    private Game game;

    private List<GameObject> planets = new ArrayList<>();

    private GravityHandler gravityHandler;
    private CollisionHandler collisionHandler;

    private Spaceship ship;

    private View view = new View();
    private GameObject focusedObject = null;

    private boolean VERLET_STATE = false; //used to update either the position or the velocity

    public PlanetTestState(Game game) {
        this.game = game;

        Vector2f windowCenter = Vector2f.div(new Vector2f(game.getWindow().getSize()), 2);

        Planet planet = new Planet(100000, Vector2f.ZERO);
        planet.setPosition(Vector2f.sub(windowCenter, Vector2f.div(planet.getSize(), 2)));

        planets.add(planet);
        ship = new Spaceship(new Vector2f(windowCenter.x - 10, 20));
        planets.add(ship);

        gravityHandler = new GravityHandler(50);
        collisionHandler = new CollisionHandler(10);
        collisionHandler.showGrid();
    }

    @Override
    public void draw(float dt) {

        centerView(focusedObject);
        game.setView(view);
        RenderWindow window = game.getWindow();

        for(GameObject o : planets){
            if(o.isVisible() && o.isActive()){
                o.draw(window);
            }
        }

        collisionHandler.draw(window);
        gravityHandler.draw(window);
    }

    @Override
    public void update(float dt) {

        collisionHandler.reset();
        collisionHandler.insertAll(planets);
        collisionHandler.resolveCollisions();

        gravityHandler.reset();
        gravityHandler.insertAll(planets);
        gravityHandler.recalculatePhysicalProperties();

        if (VERLET_STATE) {
            for (GameObject p : planets) {
                p.updatePosition(dt);
            }
        } else {
            for (GameObject p : planets) {
                p.applyForce(gravityHandler.getForce(p));
                p.updateVelocity(dt);
            }
        }
        VERLET_STATE = !VERLET_STATE;
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

                    for (GameObject o : planets) {
                        if (o.getBounds().contains(worldPos)) {
                            focusedObject = o;
                        }
                    }
                }
            } else if (e.type == Event.Type.KEY_PRESSED) {
                KeyEvent keyEvt = e.asKeyEvent();
                switch(keyEvt.key){
                    case ESCAPE :
                        focusedObject = null;
                        break;
                    case RIGHT:
                        focusedObject.applyForce(Vector2f.mul(VectorMath.RIGHT, 1000));
                        break;
                    case LEFT:
                        focusedObject.applyForce(Vector2f.mul(VectorMath.LEFT, 1000));
                        break;
                    case UP:
                        focusedObject.applyForce(Vector2f.mul(VectorMath.UP, 1000));
                        break;
                    case DOWN:
                        focusedObject.applyForce(Vector2f.mul(VectorMath.DOWN, 1000));
                        break;
                }
            }
        }
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

    public Vector2f getSceneMassCenter() {
        float x = 0, y = 0, mass = 0;
        for (GameObject o : planets) {
            x += o.getCenter().x * o.getMass();
            y += o.getCenter().y * o.getMass();
            mass += o.getMass();
        }

        return new Vector2f(x / mass, y / mass);
    }
}
