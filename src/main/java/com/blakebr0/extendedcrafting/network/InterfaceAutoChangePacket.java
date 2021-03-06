package com.blakebr0.extendedcrafting.network;

import com.blakebr0.extendedcrafting.tile.TileAutomationInterface;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InterfaceAutoChangePacket implements IMessage {

	private long pos;
	private int mode;
	
	public InterfaceAutoChangePacket() {
		
	}

	public InterfaceAutoChangePacket(BlockPos pos, int mode) {
		this.pos = pos.toLong();
		this.mode = mode;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = buf.readLong();
		this.mode = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos);
		buf.writeInt(this.mode);
	}

	public static class Handler implements IMessageHandler<InterfaceAutoChangePacket, IMessage> {

		@Override
		public IMessage onMessage(InterfaceAutoChangePacket message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(InterfaceAutoChangePacket message, MessageContext ctx) {
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(BlockPos.fromLong(message.pos));
			if (tile instanceof TileAutomationInterface) {
				TileAutomationInterface machine = (TileAutomationInterface) tile;
				
				if (message.mode == 0) {
					machine.switchInserter();
				} else if (message.mode == 1) {
					machine.switchExtractor();
				} else if (message.mode == 2) {
					machine.toggleAutoEject();
				} else if (message.mode == 3) {
					machine.toggleSmartInsert();
				} else if (message.mode == 4) {
					machine.disableInserter();
				} else if (message.mode == 5) {
					machine.disableExtractor();
				}
			}
		}
	}
}
