package Components;

import GameObjects.Entity;
import org.jsfml.graphics.RenderWindow;
import org.json.simple.JSONObject;

/**
 * Created by Matthew on 26/12/2015.
 */
public abstract class Component {

    protected Entity owner;

    public abstract void update(float dt, boolean VERLET_STATE);
    public abstract void draw(RenderWindow window);
    public void initialise(JSONObject attributes, Entity owner){
        this.owner = owner;
    }
    public void initialise(Entity owner){ this.owner = owner; }

    public Entity getOwner() {
        return owner;
    }
}
