package tinytowns.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Building implements Json.Serializable {
    private enum Type {
        BLUE,
        RED,
        GRAY,
        ORANGE,
        GREEN,
        YELLOW,
        PURPLE,
        PINK
    }

    private enum Material {
        EMPTY,
        WOOD,
        WHEAT,
        BRICK,
        GLASS,
        STONE
    }

    private String name;
    private String description;
    private Type type;
    private Material[][] blueprint;

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("description", description);
        json.writeValue("type", type.name());
        json.writeArrayStart("blueprint");
        for (Material[] row : blueprint) {
            json.writeArrayStart();
            for (Material m : row) {
                json.writeValue(m.name());
            }
            json.writeArrayEnd();
        }
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        JsonValue entry = jsonData.child();
        name = entry.asString();
        
        entry = entry.next();
        description = entry.asString();

        entry = entry.next();
        type = Type.valueOf(entry.asString());

        entry = entry.next();
        int x = entry.size;
        int y = entry.child().size;
        Material[][] b = new Material[x][y];
        int i = 0;
        for (JsonValue r = entry.child(); r != null; r = r.next()) {
            int j = 0;
            for (JsonValue m = r.child(); m != null; m = m.next()) {
                b[i][j] = Material.valueOf(m.asString());
                j++;
            }
            i++;
        }
        blueprint = b;
    }
}
