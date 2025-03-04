/*******************************************************************************
 * Copyright 2023, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package glitchcore.neoforge.mixin;

import com.mojang.authlib.GameProfile;
import glitchcore.event.EventManager;
import glitchcore.event.player.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player
{
    public MixinServerPlayer(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_)
    {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(method="changeDimension", at=@At(value="TAIL"), remap = false)
    public void onChangeDimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir)
    {
        EventManager.fire(new PlayerEvent.ChangeDimension((ServerPlayer)(Player)this));
    }
}
