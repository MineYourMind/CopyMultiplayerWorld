package de.promolitor.copymultiplayerworld.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class DataMessage implements IMessage {

	public boolean last;
	
	// 1 PlayerData / 2 RegionData / 3 Thaumcraft / 4 Baubles / 5 level.dat
	public int id;
	public int size;
	public byte[] data;

	public DataMessage() {

	}

	public DataMessage(boolean last,int id, byte[] data) {
		this.last = last;
		this.id = id;
		this.size = data.length;
		this.data = data;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Writes the int into the buf
		buf.writeBoolean(last);
		buf.writeInt(id);
		buf.writeInt(size);
		buf.writeBytes(data);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Reads the int back from the buf. Note that if you have multiple
		// values, you must read in the same order you wrote.
		last = buf.readBoolean();
		id = buf.readInt();
		size = buf.readInt();
		data = new byte[size];
		buf.readBytes(data);
	}

}
