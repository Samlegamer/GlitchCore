/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package glitchcore.neoforge.handlers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.function.Predicate;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ToolModificationEventHandler
{
    public static final Multimap<Block, Pair<Predicate<UseOnContext>, BlockState>> tillables = HashMultimap.create();

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event)
    {
        BlockState originalState = event.getState();

        if (event.getItemAbility() == ItemAbilities.HOE_TILL && tillables.containsKey(originalState.getBlock()))
        {
            for (var tillable : tillables.get(originalState.getBlock()))
            {
                if (tillable.getFirst().test(event.getContext()))
                {
                    event.setFinalState(tillable.getSecond());
                    return;
                }
            }
        }
    }
}
