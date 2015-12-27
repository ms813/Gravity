package GameObjects;

import Components.Component;
import Core.TextureManager;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Matthew on 27/12/2015.
 */
public class Entity {

    private HashMap<String, Component> components = new HashMap<>();

    private Texture texture = new Texture();
    private Sprite sprite = new Sprite();

    public Entity(Path assetPath){


        try{
            Path texturePath = Paths.get(assetPath.toString() + "/texture.png");
            texture.loadFromFile(texturePath);
        } catch (IOException e){
            e.printStackTrace();
        }

        sprite.setTexture(texture);
    }

    public void draw(RenderWindow window){

        window.draw(sprite);

        for(Component c : components.values()){
            c.draw(window);
        }
    }

    public void update(float dt, boolean VERLET_STATE){
        for(Component c : components.values()){
            c.update(dt, VERLET_STATE);
        }
    }

    public void addComponent(String componentName, Component component){
        if(!components.values().contains(component)){
            components.put(componentName, component);
        } else{
            System.err.println("[Entity.addComponent()] Component " + componentName + " already exists on entity " + this);
        }
    }

    public void addComponent(JSONObject componentData){}

    public void removeComponent(String componentName){}

    public Vector2f getPosition(){
        return sprite.getPosition();
    }

    public Vector2f getCenter(){
        float x = sprite.getPosition().x + sprite.getGlobalBounds().width /2;
        float y = sprite.getPosition().y + sprite.getGlobalBounds().height/2;
        return new Vector2f(x, y);
    }

    public Vector2f getSize(){
        return new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height);
    }

    public FloatRect getBounds(){
        return sprite.getGlobalBounds();
    }
}
