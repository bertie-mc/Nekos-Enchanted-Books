package com.berlord.nebs.test;

import com.berlord.nebs.NebsBakedModel;
import com.mojang.logging.LogUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.slf4j.Logger;

@Mod(value = NebsClientTestMod.MOD_ID, dist = Dist.CLIENT)
public final class NebsClientTestMod {
    static final String MOD_ID = "nebstest";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NebsClientTestMod(IEventBus modBus) {
        modBus.addListener(this::onModelsBaked);
    }

    private void onModelsBaked(ModelEvent.ModifyBakingResult event) {
        BakedModel book = event.getModels().get(ModelResourceLocation.inventory(
                ResourceLocation.withDefaultNamespace("enchanted_book")));
        if (!(book instanceof NebsBakedModel)) {
            throw new IllegalStateException("enchanted-book model was not replaced: " + book);
        }

        BakedModel sharpness = event.getModels().get(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("nebs", "item/minecraft/sharpness")));
        if (sharpness == null) {
            throw new IllegalStateException("sharpness book model was not baked");
        }
        LOGGER.info("NEBS_ENCHANTED_BOOK_MODEL_OK");
    }
}
