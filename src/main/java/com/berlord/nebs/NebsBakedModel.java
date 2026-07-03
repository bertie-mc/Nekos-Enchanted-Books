package com.berlord.nebs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Wraps the vanilla baked enchanted-book model. Everything is delegated to the wrapped model except
 * {@link #getOverrides()}, which returns overrides that swap in a per-enchantment model based on the
 * book's stored enchantment.
 */
public final class NebsBakedModel implements BakedModel {
    private final BakedModel base;
    private final ItemOverrides overrides;

    public NebsBakedModel(BakedModel base, Map<ResourceLocation, BakedModel> byEnchant) {
        this.base = base;
        this.overrides = new EnchantOverrides(byEnchant);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return base.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return base.isCustomRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return base.getParticleIcon();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
        return base.applyTransform(context, poseStack, leftHand);
    }

    private static final class EnchantOverrides extends ItemOverrides {
        private final Map<ResourceLocation, BakedModel> byEnchant;

        EnchantOverrides(Map<ResourceLocation, BakedModel> byEnchant) {
            super();
            this.byEnchant = byEnchant;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level,
                                  @Nullable LivingEntity entity, int seed) {
            ItemEnchantments enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (enchantments != null && !enchantments.isEmpty()) {
                for (Holder<Enchantment> holder : enchantments.keySet()) {
                    ResourceLocation id = holder.unwrapKey().map(ResourceKey::location).orElse(null);
                    if (id != null) {
                        BakedModel resolved = byEnchant.get(id);
                        if (resolved != null) {
                            return resolved;
                        }
                    }
                }
            }
            return model;
        }
    }
}
