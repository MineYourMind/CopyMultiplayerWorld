package de.promolitor.copymultiplayerworld.proxies;

public class ClientProxy extends CommonProxy {
	

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public boolean isServer() {
        return false;
    }


}