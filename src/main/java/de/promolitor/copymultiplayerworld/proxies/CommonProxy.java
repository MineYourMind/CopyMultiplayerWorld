package de.promolitor.copymultiplayerworld.proxies;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public boolean isServer() {
		return true;
	}

}
