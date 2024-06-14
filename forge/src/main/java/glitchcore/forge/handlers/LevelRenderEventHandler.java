/*******************************************************************************
 * Copyright 2023, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package glitchcore.forge.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import glitchcore.event.EventManager;
import glitchcore.event.client.LevelRenderEvent;
import glitchcore.forge.renderer.IExtendedDebugRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_PARTICLES;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LevelRenderEventHandler
{
    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event)
    {
        if (event.getStage().equals(AFTER_PARTICLES))
        {
            fireStage(LevelRenderEvent.Stage.AFTER_PARTICLES, event);
        }
    }

    private static void fireStage(LevelRenderEvent.Stage stage, RenderLevelStageEvent event)
    {
        PoseStack poseStack = ((IExtendedDebugRenderer)Minecraft.getInstance().debugRenderer).getLastPoseStack();

        var deltaTracker = new DeltaTracker() {
            public float getGameTimeDeltaTicks() {
                return event.getPartialTick();
            }

            public float getGameTimeDeltaPartialTick(boolean p_344036_) {
                return event.getPartialTick();
            }

            public float getRealtimeDeltaTicks() {
                return event.getPartialTick();
            }
        };

        EventManager.fire(new LevelRenderEvent(stage, event.getLevelRenderer(), poseStack, event.getProjectionMatrix(), event.getRenderTick(), deltaTracker, event.getCamera(), event.getFrustum()));
    }
}
