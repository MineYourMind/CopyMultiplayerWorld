package de.promolitor.copymultiplayerworld;

import java.util.LinkedHashSet;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.promolitor.copymultiplayerworld.network.DataMessage;
import de.promolitor.copymultiplayerworld.network.DataMessageHandler;
import de.promolitor.copymultiplayerworld.proxies.CommonProxy;

@Mod(modid = CopyMultiplayerWorld.MODID, version = CopyMultiplayerWorld.VERSION, name = CopyMultiplayerWorld.MODNAME, acceptableRemoteVersions = "*")
public class CopyMultiplayerWorld {
	public static final String MODID = "cmw";
	public static final String MODNAME = "Copy Multiplayer World";
	public static final String VERSION = "1.0";

	@Instance(MODID)
	public static CopyMultiplayerWorld instance;
	private static Uploader uploader;

	protected static final String CLIENT_PROXY = "de.promolitor.copymultiplayerworld.proxies.ClientProxy";
	protected static final String SERVER_PROXY = "de.promolitor.copymultiplayerworld.proxies.CommonProxy";
	@SidedProxy(modId = MODID, clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
	protected static CommonProxy proxy;

	public static final SimpleNetworkWrapper SNW = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SNW.registerMessage(DataMessageHandler.class, DataMessage.class, 1, Side.CLIENT);
		uploader = new Uploader();
		if (proxy.isServer()) { }
		if (proxy.isClient()) {
			
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) { }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) { }
	
	@SideOnly(Side.SERVER)
	public void sendCoords(String uuid, String saveName, LinkedHashSet<int[]> chunkCoords) {
		uploader.sendCoords(uuid, saveName, chunkCoords);
	}
}
