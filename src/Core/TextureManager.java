package Core;

import org.jsfml.graphics.Texture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 05/04/2015.
 */
public class TextureManager {

    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

    public static Texture getTexture(String name) {
        Texture tex = new Texture();
        boolean found = false;

        for (Map.Entry<String, Texture> entry : textures.entrySet()) {
            if (entry.getKey().equals(name)) {
                tex = entry.getValue();
                found = true;
            }
        }

        if (!found) {
            tex = loadTexture(name);
        }

        return tex;
    }

    public static Texture loadTexture(String name){
        String path = "resources" + File.separatorChar + "textures" + File.separatorChar + name;
        Texture tex = new Texture();

        try {
            tex.loadFromFile(Paths.get(path));
            textures.put(name, tex);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot load texture '" + name + "in resources/textures/");
        }

        return tex;
    }

}
