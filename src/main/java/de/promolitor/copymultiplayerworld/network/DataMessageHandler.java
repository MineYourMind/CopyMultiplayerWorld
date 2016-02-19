package de.promolitor.copymultiplayerworld.network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.promolitor.copymultiplayerworld.Uploader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;

public class DataMessageHandler implements IMessageHandler<DataMessage, IMessage> {

	private static String saveFolderPath = "";
	public LinkedHashSet<NBTTagCompound> chunks = new LinkedHashSet<NBTTagCompound>();

	@Override
	public IMessage onMessage(DataMessage message, MessageContext ctx) {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		byte[] download = message.getData();
		// message.getID()  1 PlayerData / 2 Chunks / 3 Thaumcraft / 4 Baubles / 5 level.dat / 6 finished / 7 init
		if (message.getID() == 1) {
			try {
				File pFile = new File(saveFolderPath + "/playerdata/" + player.getUniqueID().toString() + ".dat");
				FileUtils.writeByteArrayToFile(pFile, CompressedStreamTools.compress(message.getNBT()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getID() == 3) {
			try {
				FileUtils.writeByteArrayToFile(
						new File(saveFolderPath + "/playerdata/" + player.getDisplayName() + ".thaum"),
						download);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getID() == 4) {
			try {
				FileUtils.writeByteArrayToFile(
						new File(saveFolderPath + "/playerdata/" + player.getDisplayName() + ".baub"),
						download);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getID() == 5) {
			try {
				FileUtils.writeByteArrayToFile(new File(saveFolderPath + "/level.dat"), download);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getID() == 2) {
			NBTTagCompound chunkNBT = message.getNBT();
			NBTTagCompound level = chunkNBT.getCompoundTag("Level");			
			File rf = new File(saveFolderPath);	
			RegionFileCache.createOrLoadRegionFile(rf, level.getInteger("xPos"), level.getInteger("zPos"));
			DataOutputStream dataOut = RegionFileCache.getChunkOutputStream(rf, level.getInteger("xPos"), level.getInteger("zPos"));	
			System.out.println(level.getInteger("xPos")+","+level.getInteger("zPos"));
			try {
				CompressedStreamTools.write(chunkNBT, dataOut);
				dataOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getID() == 6) {
			player.addChatMessage(new ChatComponentText("Download finished and new world created!"));
		} else if (message.getID() == 7) {
			Date date = new java.util.Date();
			String seconds = ""+(date.getTime()/1000);
			saveFolderPath = "saves/" + new String(download) + "-" + seconds;
			File directory = new File(saveFolderPath);
			File playerDir = new File(saveFolderPath+"/playerdata");
			File regionDir = new File(saveFolderPath+"/region");
			if (!directory.exists()) { System.out.println("Save Folder Creation Success? " + directory.mkdirs()); }
			if (!playerDir.exists()) { System.out.println("Player Folder Creation Success? " + playerDir.mkdirs()); }
			if (!regionDir.exists()) { System.out.println("Region Folder Creation Success? " + regionDir.mkdirs()); }
		}
		return null;
	}
}
