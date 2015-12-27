package Components;

import GameObjects.Entity;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;

/**
 * Created by Matthew on 26/12/2015.
 */
public class ComponentFactory {

    public Component createComponent(JSONObject componentJson, Entity owner){
        String componentName = (String) componentJson.get("ComponentName");
        JSONObject attributes = (JSONObject) componentJson.get("Attributes");

        try {

            Class<?> clazz = Class.forName("Components." + componentName);
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();

            Component c = (Component) instance;
            if(attributes == null || attributes.isEmpty()) {
                c.initialise(owner);
            } else{
                c.initialise(attributes, owner);
            }

            return c;

        } catch (Exception e){
            e.printStackTrace();
        }

        System.err.println("[ComponentFactory.createComponent()] Bad times with reflection");
        return null;
    }
}
