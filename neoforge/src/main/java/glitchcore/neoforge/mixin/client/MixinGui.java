/*******************************************************************************
 * Copyright 2023, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package glitchcore.neoforge.mixin.client;

import glitchcore.event.EventManager;
import glitchcore.event.client.RenderGuiEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui
{
    @Unique
    private float partialTicks;

    @Shadow
    public int rightHeight;

    @Inject(method="render", at=@At(value="HEAD"))
    public void onRender(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci)
    {
        this.partialTicks = partialTicks;
    }

    @Inject(method="renderCameraOverlays", at=@At(value="INVOKE", target="net/minecraft/client/player/LocalPlayer.getTicksFrozen()I"))
    private void onBeginRenderFrozenOverlay(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci)
    {
        EventManager.fire(new RenderGuiEvent.Pre(RenderGuiEvent.Type.FROSTBITE, (Gui)(Object)this, guiGraphics, this.partialTicks, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
    }

    @Inject(method="renderFoodLevel", at=@At(value="INVOKE", target="net/minecraft/client/gui/Gui.getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private void onRenderFoodLevel(GuiGraphics guiGraphics, CallbackInfo ci)
    {
        EventManager.fire(new RenderGuiEvent.Pre(RenderGuiEvent.Type.FOOD, (Gui)(Object)this, guiGraphics, this.partialTicks, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
    }

    @Inject(method="renderAirLevel", at=@At(value="INVOKE", target="net/minecraft/world/entity/player/Player.getMaxAirSupply()I"))
    private void onBeginRenderAir(GuiGraphics guiGraphics, CallbackInfo ci)
    {
        int rightTop = guiGraphics.guiHeight() - this.rightHeight;
        var event = new RenderGuiEvent.Pre(RenderGuiEvent.Type.AIR, (Gui)(Object)this, guiGraphics, this.partialTicks, guiGraphics.guiWidth(), guiGraphics.guiHeight(), rightTop);
        EventManager.fire(event);
        this.rightHeight = guiGraphics.guiHeight() - event.getRowTop();
    }
}
