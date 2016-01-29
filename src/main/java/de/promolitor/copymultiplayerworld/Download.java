package de.promolitor.copymultiplayerworld;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.promolitor.copymultiplayerworld.network.RIdsMessage;
import de.promolitor.copymultiplayerworld.proxies.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockNote;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import scala.actors.threadpool.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.chunk.Chunk;

public class Download {

	public static String saveName;
	public static String saveFolderPath;
	public static ArrayList<int[]> regionIds;
	public static int count = 0;
	public static ArrayList<byte[]> toSend;
	public static byte[] playerFile;
	public static byte[] level;
	public static byte[] thaumcraftFile;
	public static byte[] baublesFile;

	@SideOnly(Side.SERVER)
	public static void runDownload(int[] rId, EntityPlayerMP player) {
		InputStream in = null;
		byte[] bytes = null;

		// Catch PlayerFile
		playerFile = getPlayerFile(player.getUniqueID());
		level = getLevelFile();
		// Optional
		thaumcraftFile = getThaumcraftFile(player.getDisplayName());
		baublesFile = getBaublesFile(player.getDisplayName());

		try {
			in = new FileInputStream("world/region/r." + rId[0] + "." + rId[1] + ".mca");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("Problems catching Region Files");
			e.printStackTrace();
		}
		toSend = new ArrayList<byte[]>();
		for (int i = 0; i < bytes.length; i += 64000) {
			if (i == bytes.length) {
				toSend.add(Arrays.copyOfRange(bytes, i, i));
			} else if ((i + 64000) >= (bytes.length)) {
				toSend.add(Arrays.copyOfRange(bytes, i, bytes.length));
			} else {
				toSend.add(Arrays.copyOfRange(bytes, i, i + 64000));
			}
		}

	}

	public static byte[] getPlayerFile(UUID uuid) {

		InputStream in = null;
		byte[] bytes = null;
		try {
			in = new FileInputStream("world/playerdata/" + uuid + ".dat");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("No Playerdata?");
		}

		return bytes;

	}

	public static byte[] getLevelFile() {

		InputStream in = null;
		byte[] bytes = null;
		try {
			in = new FileInputStream("world/level.dat");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("No Playerdata?");
		}

		return bytes;

	}

	public static byte[] getThaumcraftFile(String displayName) {

		InputStream in = null;
		byte[] bytes = null;
		try {
			in = new FileInputStream("world/playerdata/" + displayName + ".thaum");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("No Thaumcraft Data?");
		}

		return bytes;

	}

	public static byte[] getBaublesFile(String displayName) {

		InputStream in = null;
		byte[] bytes = null;
		try {
			in = new FileInputStream("world/playerdata/" + displayName + ".baub");
			bytes = IOUtils.toByteArray(in);
			in.close();
		} catch (Exception e) {
			System.out.println("No Baubles Data?");
		}

		return bytes;

	}

	@SideOnly(Side.CLIENT)
	public static void getMCAFiles(int diameter, String saveName) {
		Download.saveName = saveName;
		Minecraft mc = Minecraft.getMinecraft();
		int currentChunkX = mc.thePlayer.chunkCoordX;
		int currentChunkZ = mc.thePlayer.chunkCoordZ;
		System.out.println("Standing in Chunk: x:" + currentChunkX + " / y:" + currentChunkZ);
		regionIds = new ArrayList<int[]>();

		for (int x = -2; x < 5; x += 4) {
			for (int z = -2; z < 5; z += 4) {
				int regionX = (currentChunkX + x) >> 5;
				int regionZ = (currentChunkZ + z) >> 5;
				int[] region = { regionX, regionZ };
				boolean isNotAddedYet = true;
				for (int[] rIds : regionIds) {
					if (areEqual(rIds, region)) {
						isNotAddedYet = false;
					}

				}
				if (isNotAddedYet) {
					regionIds.add(region);
				}

			}

		}
		for (int[] rIds : regionIds) {
			System.out.println(Arrays.toString(rIds));
		}

		saveFolderPath = "saves/" + saveName;
		File directory = new File(saveFolderPath);
		if (!directory.exists()) {
			System.out.println("Folder Creation Success? " + directory.mkdirs());
		}
		for (int[] rIds : regionIds) {
			CopyMultiplayerWorld.SNW.sendToServer(new RIdsMessage(rIds));
		}

	}

	public static boolean areEqual(int[] array1, int[] array2) {
		boolean equal = false;
		if (array1.length == array2.length) {
			for (int i = 0; i < array1.length; i++) {
				if (array1[i] == array2[i]) {
					equal = true;
				} else {
					equal = false;
				}
			}
		}
		return equal;
	}

}
