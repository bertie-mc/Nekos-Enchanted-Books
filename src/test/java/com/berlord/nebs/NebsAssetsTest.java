package com.berlord.nebs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NebsAssetsTest {

    @Test
    void everyIndexEntryHasAModelAndTexture() throws IOException {
        JsonObject index = readJson("assets/nebs/nebs_books.json");
        assertTrue(index.size() >= 150, "the shipped mapping index is unexpectedly small");

        for (Map.Entry<String, JsonElement> entry : index.entrySet()) {
            String modelId = entry.getValue().getAsString();
            assertTrue(modelId.startsWith("nebs:item/"), entry.getKey() + " has an external model: " + modelId);

            String modelPath = "assets/nebs/models/" + modelId.substring("nebs:".length()) + ".json";
            JsonObject model = readJson(modelPath);
            assertEquals("minecraft:item/enchanted_book", model.get("parent").getAsString(), modelPath);

            String textureId = model.getAsJsonObject("textures").get("layer0").getAsString();
            assertTrue(textureId.startsWith("nebs:item/"), modelPath + " has an external texture: " + textureId);
            String texturePath = "assets/nebs/textures/" + textureId.substring("nebs:".length()) + ".png";
            try (InputStream texture = resource(texturePath)) {
                assertNotNull(texture, texturePath);
            }
        }
    }

    @Test
    void keepsRepresentativeVanillaAndAliasMappings() throws IOException {
        JsonObject index = readJson("assets/nebs/nebs_books.json");
        assertEquals("nebs:item/minecraft/sharpness", index.get("minecraft:sharpness").getAsString());
        assertEquals("nebs:item/minecraft/sweeping", index.get("minecraft:sweeping_edge").getAsString());
        assertEquals("nebs:item/ensorcellation/reach", index.get("alexscaves:field_extension").getAsString());
    }

    private static JsonObject readJson(String path) throws IOException {
        try (InputStream input = resource(path)) {
            assertNotNull(input, path);
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    private static InputStream resource(String path) {
        return NebsAssetsTest.class.getClassLoader().getResourceAsStream(path);
    }
}
