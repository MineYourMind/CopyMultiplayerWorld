package de.promolitor.copymultiplayerworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.promolitor.copymultiplayerworld.network.DataMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;

public class Uploader {
	private static String worldSaveFolder = "";
	
	@SideOnly(Side.SERVER)
	public void sendCoords(String uuid, String saveName, LinkedHashSet<int[]> chunkCoords)
	{
		EntityPlayerMP serverPlayer = null;
		for (Object p : MinecraftServer.getServer().getEntityWorld().playerEntities)
		{
			// Debug
			//System.out.println(((EntityPlayerMP)p).getUniqueID().toString());
			// Debug
			//System.out.println(uuid);
			if (((EntityPlayerMP)p).getUniqueID().toString().equalsIgnoreCase(uuid))
			{
				serverPlayer = (EntityPlayerMP)p;
			}
		}
		// Must check if there's a player and chunkCoords
		if (serverPlayer == null || chunkCoords == null) {
			/* DEBUG START */
			if (serverPlayer == null) { System.out.println("PLAYER IS NULL"); }
			if (chunkCoords == null) { System.out.println("CHUNKCOORDS IS NULL"); }
			/* DEBUG END */
			return;
		}
		// Static field for convenience 
		worldSaveFolder = serverPlayer.getServerForPlayer().getSaveHandler().getWorldDirectoryName();		
		if (!Files.exists(Paths.get(worldSaveFolder+"/level.dat"))) {
			System.out.println("[CMW] LEVEL.DAT DOES NOT EXIST YET");
			serverPlayer.addChatMessage(new ChatComponentText("The server has not saved yet."));
			return; // Should only reach this if the server was just started without saving yet
		}
		// Save player data to update save with current position
		serverPlayer.mcServer.getConfigurationManager().saveAllPlayerData();
		
		// Initial message to let the client know we are sending stuff
		CopyMultiplayerWorld.SNW.sendTo(new DataMessage(7, saveName.getBytes()), serverPlayer);

		// Send Player file message as NBT
		CopyMultiplayerWorld.SNW.sendTo(new DataMessage(1, getPlayerFile(serverPlayer)), serverPlayer);
		// Send level.dat file
		CopyMultiplayerWorld.SNW.sendTo(new DataMessage(5, getLevelFile(serverPlayer)), serverPlayer);
		// Send extra thaumcraft and baubles data
		byte[] thaumFileData = getThaumcraftFile(serverPlayer);
		if (thaumFileData.length > 0) { 
			CopyMultiplayerWorld.SNW.sendTo(new DataMessage(3, thaumFileData), serverPlayer);
		}
		byte[] baublesFileData = getBaublesFile(serverPlayer);
		if (baublesFileData.length > 0) {
			CopyMultiplayerWorld.SNW.sendTo(new DataMessage(4, baublesFileData), serverPlayer);
		}
		
		// Chunk nbt messages
		for (int[] chunkCoord : chunkCoords) {
			AnvilChunkLoader acl = (AnvilChunkLoader) MinecraftServer.getServer().getEntityWorld().getSaveHandler().getChunkLoader(MinecraftServer.getServer().getEntityWorld().provider);
			try {
				//System.out.println(chunkCoord[0]+","+chunkCoord[1]);
				NBTTagCompound nbt = (NBTTagCompound)acl.loadChunk__Async(MinecraftServer.getServer().getEntityWorld(), chunkCoord[0], chunkCoord[1])[1];
				//System.out.println(nbt.getCompoundTag("Level").getInteger("xPos")+","+nbt.getCompoundTag("Level").getInteger("zPos"));
				CopyMultiplayerWorld.SNW.sendTo(new DataMessage(2, nbt), serverPlayer);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}	
		// Tell client we are finished
		CopyMultiplayerWorld.SNW.sendTo(new DataMessage(6, new byte[]{}), serverPlayer);
		return;
	}
	
	private NBTTagCompound getPlayerFile(EntityPlayerMP player) {
		NBTTagCompound nbt = new NBTTagCompound();
		try {
			nbt = CompressedStreamTools.readCompressed(new FileInputStream(worldSaveFolder + "/playerdata/" + player.getUniqueID() + ".dat"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (nbt.hasNoTags())
		{
			// Debug
			System.out.println("PLAYER NBT DATA MISSING");
			nbt = player.getEntityData();
		}
		nbt.setInteger("playerGameType", 1);
		return nbt;
	}

	private byte[] getLevelFile(EntityPlayerMP player) {

		InputStream in = null;
		byte[] bytes = null;
		try {
			in = new FileInputStream(worldSaveFolder+"/level.dat");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("No Playerdata?");
		}

		return bytes;

	}

	private byte[] getThaumcraftFile(EntityPlayerMP player) {

		InputStream in = null;
		byte[] bytes = null;
		String path = worldSaveFolder+"/playerdata/" + player.getDisplayName() + ".thaum";
		try {
			if (Files.exists(Paths.get(path))) {
				in = new FileInputStream(path);
				bytes = IOUtils.toByteArray(in);
				in.close();
				return bytes;
			} else {
				return new byte[]{};
			}
		} catch (Exception e) {
			System.out.println("No Thaumcraft Data?");
			return new byte[]{};
		}
	}

	private byte[] getBaublesFile(EntityPlayerMP player) {

		InputStream in = null;
		byte[] bytes = null;
		String path = worldSaveFolder+"/playerdata/" + player.getDisplayName() + ".baub";
		try {
			if (Files.exists(Paths.get(path))) {
				in = new FileInputStream(path);
				bytes = IOUtils.toByteArray(in);
				in.close();		
				return bytes;
			} else {
				return new byte[]{};
			}
				
		} catch (Exception e) {
			System.out.println("No Baubles Data?");
			return new byte[]{};
		}
	}
}
