package com.imbuedheartnotifier;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ImbuedHeartNotifierPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ImbuedHeartNotifierPlugin.class);
		RuneLite.main(args);
	}
}