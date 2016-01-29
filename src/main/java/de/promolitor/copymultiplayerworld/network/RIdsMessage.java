package de.promolitor.copymultiplayerworld.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RIdsMessage implements IMessage {

	public String rid;

	public RIdsMessage() {

	}

	public RIdsMessage(int[] rid) {
		this.rid = rid[0] + "," + rid[1];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		ByteBufUtils.writeUTF8String(buf, rid);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		rid = ByteBufUtils.readUTF8String(buf);

	}

}
