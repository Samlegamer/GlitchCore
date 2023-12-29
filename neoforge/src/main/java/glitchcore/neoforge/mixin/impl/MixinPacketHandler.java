package glitchcore.neoforge.mixin.impl;

import glitchcore.network.CustomPacket;
import glitchcore.network.PacketHandler;
import net.jodah.typetools.TypeResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.*;

@Mixin(value = PacketHandler.class, remap = false)
public abstract class MixinPacketHandler
{
    @Shadow
    @Final
    private ResourceLocation channelName;

    @Unique
    private SimpleChannel channel;

    @Unique
    private int id = 0;

    @Overwrite
    public <T extends CustomPacket<T>> void register(CustomPacket<T> packet)
    {
        final Class<T> dataType = (Class<T>) TypeResolver.resolveRawArgument(CustomPacket.class, packet.getClass());

        if ((Class<?>)dataType == TypeResolver.Unknown.class)
        {
            throw new IllegalStateException("Failed to resolve packet data type: " + packet);
        }

        this.channel.messageBuilder(dataType, id++).encoder(CustomPacket::encode).decoder(packet::decode).consumerMainThread((data, forgeContext) ->
        {
            forgeContext.enqueueWork(() ->
            {
                packet.handle(data, new CustomPacket.Context() {
                    @Override
                    public boolean isClientSide() {
                        return forgeContext.getDirection().getReceptionSide() == LogicalSide.CLIENT;
                    }

                    @Override
                    public ServerPlayer getSender()
                    {
                        return forgeContext.getSender();
                    }
                });
            });
            forgeContext.setPacketHandled(true);
        }).add();
    }

    @Overwrite
    public <T> void sendToPlayer(T data, ServerPlayer player)
    {
        channel.send(PacketDistributor.PLAYER.with(() -> player), data);
    }

    @Overwrite
    public <T> void sendToServer(T data)
    {
        channel.send(PacketDistributor.SERVER.noArg(), data);
    }

    @Overwrite
    private void init()
    {
        this.channel = NetworkRegistry.ChannelBuilder.named(this.channelName).simpleChannel();
    }
}
