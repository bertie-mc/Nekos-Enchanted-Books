package com.berlord.nebs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Installs data-driven per-enchantment models for enchanted books. */
@Mod(value = NekosEnchantedBooks.MOD_ID, dist = Dist.CLIENT)
public final class NekosEnchantedBooks {
    public static final String MOD_ID = "nebs";

    private static final ResourceLocation BOOKS_FILE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "nebs_books.json");
    private static final ModelResourceLocation ENCHANTED_BOOK =
            ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("enchanted_book"));

    private static final Map<ResourceLocation, ResourceLocation> ENCHANT_TO_MODEL = new HashMap<>();

    public NekosEnchantedBooks(IEventBus modBus) {
        modBus.addListener(this::onRegisterAdditional);
        modBus.addListener(this::onModifyBakingResult);
    }

    private void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        ENCHANT_TO_MODEL.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        ENCHANT_TO_MODEL.putAll(load(mc.getResourceManager()));
        ENCHANT_TO_MODEL.values().stream().distinct()
                .forEach(model -> event.register(ModelResourceLocation.standalone(model)));
    }

    private void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (ENCHANT_TO_MODEL.isEmpty()) {
            return;
        }
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        BakedModel book = models.get(ENCHANTED_BOOK);
        if (book == null) {
            return;
        }
        Map<ResourceLocation, BakedModel> byEnchant = new HashMap<>();
        ENCHANT_TO_MODEL.forEach((enchant, model) -> {
            BakedModel baked = models.get(ModelResourceLocation.standalone(model));
            if (baked != null) {
                byEnchant.put(enchant, baked);
            }
        });
        if (!byEnchant.isEmpty()) {
            models.put(ENCHANTED_BOOK, new NebsBakedModel(book, byEnchant));
        }
    }

    private static Map<ResourceLocation, ResourceLocation> load(ResourceManager rm) {
        Map<ResourceLocation, ResourceLocation> map = new LinkedHashMap<>();
        Optional<Resource> resource = rm.getResource(BOOKS_FILE);
        if (resource.isEmpty()) {
            return map;
        }
        try (Reader reader = resource.get().openAsReader()) {
            JsonObject obj = GsonHelper.parse(reader);
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                try {
                    ResourceLocation enchant = ResourceLocation.parse(entry.getKey());
                    ResourceLocation model = ResourceLocation.parse(entry.getValue().getAsString());
                    map.put(enchant, model);
                } catch (RuntimeException ignored) {
                    // One bad optional-mod entry must not disable the rest of the index.
                }
            }
        } catch (Exception ignored) {
            // Leave books vanilla if a resource pack supplies an unreadable index.
        }
        return map;
    }
}
