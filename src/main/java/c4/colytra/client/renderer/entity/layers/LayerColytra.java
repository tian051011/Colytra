/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.client.renderer.entity.layers;

import c4.colytra.util.ClientUtil;
import c4.colytra.util.ColytraUtil;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.vanity.feature.DyableElytra;

import javax.annotation.Nonnull;
import java.awt.*;

@SideOnly(Side.CLIENT)
public class LayerColytra implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    private static final ResourceLocation QUARK_DYED_TEXTURE = new ResourceLocation("quark",
            "textures/misc/elytra_dyed.png");
    protected final RenderLivingBase<?> renderPlayer;
    private final ModelElytra modelElytra = new ModelElytra();

    public LayerColytra(RenderLivingBase<?> renderPlayer)
    {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack colytra = ColytraUtil.wornElytra(entitylivingbaseIn);

        if (colytra != ItemStack.EMPTY && colytra.getItem() != Items.ELYTRA) {

            if (!ClientUtil.shouldRenderColytra(colytra)) {
                return;
            }
            boolean quarkDyed = false;

            if (CommonProxy.quarkLoaded) {
                quarkDyed = setElytraColors(colytra);
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }

            if (entitylivingbaseIn instanceof AbstractClientPlayer) {
                AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)entitylivingbaseIn;

                if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
                } else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null
                        && abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE)) {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationCape());
                } else if (quarkDyed) {
                    this.renderPlayer.bindTexture(QUARK_DYED_TEXTURE);
                } else {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
            } else if (quarkDyed) {
                this.renderPlayer.bindTexture(QUARK_DYED_TEXTURE);
            } else {
                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale,
                    entitylivingbaseIn);
            this.modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                    scale);

            if (colytra.isItemEnchanted()) {

                if (CommonProxy.quarkLoaded) {
                    setColorRunes(colytra);
                }
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entitylivingbaseIn, this.modelElytra, limbSwing,
                        limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }

            GlStateManager.color(1F, 1F, 1F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    @Optional.Method(modid = "quark")
    private void setColorRunes(ItemStack stack) {
        ColorRunes.setTargetStack(stack);
    }

    @Optional.Method(modid = "quark")
    private boolean setElytraColors(ItemStack stack) {

        int colorIndex = -1;

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DyableElytra.TAG_ELYTRA_DYE)) {
             colorIndex = stack.getTagCompound().getInteger(DyableElytra.TAG_ELYTRA_DYE);
        }

        if(colorIndex == -1 || colorIndex == 15) {
            GlStateManager.color(1F, 1F, 1F);
            return false;
        } else {
            Color color = new Color(ItemDye.DYE_COLORS[colorIndex]);
            float r = color.getRed() / 255F;
            float g = color.getGreen() / 255F;
            float b = color.getBlue() / 255F;
            GlStateManager.color(r, g, b);
            return true;
        }
    }
}
