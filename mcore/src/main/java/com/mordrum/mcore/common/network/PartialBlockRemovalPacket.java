package com.mordrum.mcore.common.network;


import com.mordrum.mcore.client.MultiMineClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;

public class PartialBlockRemovalPacket implements NetworkHelper.IPacket
{

    private BlockPos pos;

    public PartialBlockRemovalPacket()
    {
    }

    public PartialBlockRemovalPacket(BlockPos p)
    {
        pos = p;
    }

    @Override
    public void writeBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        bytes.writeInt(pos.getX());
        bytes.writeInt(pos.getY());
        bytes.writeInt(pos.getZ());
    }

    @Override
    public void readBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        pos = new BlockPos(bytes.readInt(), bytes.readInt(), bytes.readInt());
        FMLClientHandler.instance().getClient().addScheduledTask(new ScheduledCode());
    }

    class ScheduledCode implements Runnable
    {

        @Override
        public void run()
        {
            MultiMineClient.instance().onServerSentPartialBlockDeleteCommand(pos);
        }
    }

}