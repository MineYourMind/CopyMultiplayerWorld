package de.promolitor.copymultiplayerworld.network;

import java.util.Arrays;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.promolitor.copymultiplayerworld.CopyMultiplayerWorld;
import de.promolitor.copymultiplayerworld.Download;
import net.minecraft.entity.player.EntityPlayerMP;

public class RIdsMessageHandler implements IMessageHandler<RIdsMessage, IMessage> {

	@Override
	public IMessage onMessage(RIdsMessage message, MessageContext ctx) {
		System.out.println("MESSAGE FROM CLIENT TO SERVER!!!");
		EntityPlayerMP serverPlayer = ctx.getServerHandler().playerEntity;
		String[] rIdsAsString = message.rid.split(",");
		int[] rids = { Integer.parseInt(rIdsAsString[0]), Integer.parseInt(rIdsAsString[1]) };
		Download.runDownload(rids);
		int lastIndex = Download.toSend.size() - 1;
		int i = 0;
		for (byte[] split : Download.toSend) {
			boolean last = false;
			if (i == lastIndex) {
				last = true;
			}
			CopyMultiplayerWorld.SNW.sendTo(new DataMessage(last, split), serverPlayer);
			i++;
		}
		return null;
	}

}
