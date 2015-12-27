package Components;

import GameObjects.Entity;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderWindow;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 27/12/2015.
 */
public class DrawableComponent extends Component {

    private List<Drawable> drawables = new ArrayList<>();

    @Override
    public void initialise(JSONObject attributes, Entity owner) {
        super.initialise(attributes, owner);
    }

    @Override
    public void draw(RenderWindow window) {

    }

    @Override
    public void update(float dt, boolean VERLET_STATE) {

    }
}
