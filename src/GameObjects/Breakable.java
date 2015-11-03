package GameObjects;

import java.util.List;

/**
 * Created by Matthew on 03/11/2015.
 */
public interface Breakable extends CoreGameObject {
    List<GameObject> breakInto();
    float getBindingEnergy();
}
