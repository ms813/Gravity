package Factories;

import Components.Component;
import Components.ComponentFactory;
import GameObjects.Entity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 26/12/2015.
 */
public class EntityFactory {

    private HashMap<String, JSONObject> entityData = new HashMap<>();

    private ComponentFactory componentFactory = new ComponentFactory();

    public EntityFactory() {
        JSONParser parser = new JSONParser();

        Path path = Paths.get("resources/ObjectTypeDefinitions/DefaultEntities.json");
        try {
            String s = new String(Files.readAllBytes(path));
            JSONArray jsonIn = (JSONArray) parser.parse(s);

            for(Object o : jsonIn){
                JSONObject jsonObject = (JSONObject) o;
                String entityName = (String) jsonObject.get("EntityName");

                entityData.put(entityName, jsonObject);
            }

        } catch (IOException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public Entity createEntity(String entityName){

        JSONArray objComponentData = getComponentData(entityName);
        Path assetPath = getAssetPath(entityName);

        Entity entityToReturn = new Entity(assetPath);

        for(Object json : objComponentData) {
            JSONObject attributeJSON = (JSONObject) json;
            Component component = componentFactory.createComponent(attributeJSON, entityToReturn);

            entityToReturn.addComponent(entityName, component);
        }

        return entityToReturn;
    }

    private Path getAssetPath(String entityName){
        for(Map.Entry<String, JSONObject> entry : entityData.entrySet()){
            if(entry.getKey().equals(entityName)){
                return Paths.get((String) entry.getValue().get("AssetPath"));
            }
        }

        System.err.println("[EntityFactory.getAssetPath()] Cannot find entity: " + entityName);
        return null;
    }

    private JSONArray getComponentData(String entityName){
        for(Map.Entry<String, JSONObject> entry : entityData.entrySet()){
            if(entry.getKey().equals(entityName)){
                return (JSONArray) entry.getValue().get("Components");
            }
        }

        System.err.println("[EntityFactory.getComponentData()] Cannot find entity: " + entityName);
        return null;
    }
}
