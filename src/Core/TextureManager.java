package Core;

import org.jsfml.graphics.IntRect;
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

    //coords of small particles on texture
    public final static IntRect[] smallTextureRects = {
            new IntRect(339, 262, 30, 29),
            new IntRect(384, 262, 30, 29),
            new IntRect(428, 262, 30, 29),
            new IntRect(341, 311, 31, 29),
            new IntRect(383, 311, 31, 29),
            new IntRect(427, 311, 31, 29)
    };

    public static final IntRect[] medTextureRects = {
            new IntRect(352, 12, 50, 57),
            new IntRect(419, 12, 50, 57),
            new IntRect(485, 12, 50, 57),

            new IntRect(338, 102, 64, 52),
            new IntRect(412, 102, 64, 52),
            new IntRect(484, 102, 64, 52),

            new IntRect(334, 182, 63, 56),
            new IntRect(417, 182, 63, 56),
            new IntRect(487, 182, 63, 56)
    };

    public static final IntRect[] largeTextureRects = {
            new IntRect(5, 5, 150, 140),
            new IntRect(171, 5, 150, 140),
            new IntRect(7, 182, 144, 155),
            new IntRect(168, 182, 144, 155),
            new IntRect(9, 375, 136, 149),
            new IntRect(175, 375, 136, 149),
            new IntRect(344, 375, 136, 149)
    };


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

    public static Texture loadTexture(String name) {
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
