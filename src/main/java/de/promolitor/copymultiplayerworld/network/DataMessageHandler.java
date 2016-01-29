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

public class DataMessageHandler implements IMessageHandler<DataMessage, IMessage> {

	public static byte[] comparer;

	public static void fillComparer() {
		comparer = new byte[100];
		Arrays.fill(comparer, (byte) 0);
	}

	public byte[] combined = new byte[0];

	@Override
	public IMessage onMessage(DataMessage message, MessageContext ctx) {
		System.out.println("MESSAGE FROM SERVER TO CLIENT!!!");
		byte[] download = message.data;
		System.out.println("BYTE ARRAY NACH DEM DEM SENDEN!!!!");
		System.out.println(download);
		if (message.last) {
			System.out.println("LAST PACKAGE DETECTED!!!");
			combined = ArrayUtils.addAll(combined, download);
			try {
				FileUtils.writeByteArrayToFile(
						new File(Download.saveFolderPath + "/r." + Download.regionIds.get(Download.count)[0] + "."
								+ Download.regionIds.get(Download.count++)[1] + ".mca"),
						combined);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			combined = ArrayUtils.addAll(combined, download);
		}

		return null;
	}

}
