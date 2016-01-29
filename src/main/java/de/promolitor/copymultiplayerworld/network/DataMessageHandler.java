package de.promolitor.copymultiplayerworld.network;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.promolitor.copymultiplayerworld.Download;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class DataMessageHandler implements IMessageHandler<DataMessage, IMessage> {

	public byte[] combined = null;

	@Override
	public IMessage onMessage(DataMessage message, MessageContext ctx) {
		System.out.println("MESSAGE FROM SERVER TO CLIENT!!!");
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		byte[] download = message.data;
		if (message.id == 1) {
			System.out.println("WRITE PLAYERFILE");
			try {
				FileUtils.writeByteArrayToFile(
						new File(Download.saveFolderPath + "/playerdata/" + player.getUniqueID().toString() + ".dat"),
						download);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (message.id == 3) {
			System.out.println("WRITE THAUMCRAFT DATA");
			try {
				FileUtils.writeByteArrayToFile(
						new File(Download.saveFolderPath + "/playerdata/" + player.getDisplayName() + ".thaum"),
						download);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (message.id == 4) {
			System.out.println("WRITE BAUBLES DATA");
			try {
				FileUtils.writeByteArrayToFile(
						new File(Download.saveFolderPath + "/playerdata/" + player.getDisplayName() + ".baub"),
						download);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (message.id == 5) {
			System.out.println("WRITE LEVEL DATA");
			try {
				FileUtils.writeByteArrayToFile(new File(Download.saveFolderPath + "/level.dat"), download);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (message.id == 2) {
			if (message.last) {
				System.out.println("LAST PACKAGE DETECTED!!!");
				combined = ArrayUtils.addAll(combined, download);
				try {
					FileUtils.writeByteArrayToFile(
							new File(Download.saveFolderPath + "/region/r." + Download.regionIds.get(Download.count)[0]
									+ "." + Download.regionIds.get(Download.count++)[1] + ".mca"),
							combined);
					combined = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.addChatMessage(new ChatComponentText("Download finished and new world created!"));
			} else {
				combined = ArrayUtils.addAll(combined, download);
			}
		}
		return null;
	}

}
