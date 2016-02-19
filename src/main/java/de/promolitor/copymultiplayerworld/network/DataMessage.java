package de.promolitor.copymultiplayerworld.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class DataMessage implements IMessage {
	
	private int id = 0;
	private NBTTagCompound nbt = new NBTTagCompound();
	private byte[] playerData = new byte[]{};
	private int size = 0;
	private byte[] data = new byte[]{};

	public DataMessage() { }

	public DataMessage(int id, NBTTagCompound nbt, byte[] data) {
		this.id = id;
		this.nbt = nbt;
		this.size = data.length;
		this.data = data;
	}

	public DataMessage(int id, NBTTagCompound nbt) {
		this.id = id;
		this.nbt = nbt;
		this.size = 0;
		this.data = new byte[]{};
	}
	
	public DataMessage(int id, byte[] data) {
		this.id = id;
		this.nbt = new NBTTagCompound();
		this.size = data.length;
		this.data = data;
	}

	// Getters but no need for setters
	public int getID() { return id; }
	public NBTTagCompound getNBT() { return this.nbt; }
	public int getDataSize() { return this.size; }
	public byte[] getData() { return this.data; }
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeInt(size);
		buf.writeBytes(data);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// If you have multiple values, you must read in the same order you wrote.
		id = buf.readInt();
		nbt = ByteBufUtils.readTag(buf);
		size = buf.readInt();
		data = new byte[size];
		buf.readBytes(data);
	}

}
