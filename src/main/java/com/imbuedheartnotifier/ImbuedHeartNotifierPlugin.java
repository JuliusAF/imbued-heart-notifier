package com.imbuedheartnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Imbued Heart Notifier"
)
public class ImbuedHeartNotifierPlugin extends Plugin
{
	private static final String IMBUED_HEART_READY_MESSAGE = "<col=ef1020>Your imbued heart has regained its magical power.</col>";
	private static final Pattern IMBUED_HEART_BUSY_MESSAGE = Pattern.compile("The heart is still drained of its power. Judging by how it feels, it will be ready in around (\\d) \\.");

	private static final int invigorateDuration = 700;

	@Inject
	private Client client;

	@Inject
	private ImbuedHeartNotifierConfig config;

	@Inject
	private ImbuedHeartNotifierOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	private int invigorateTick;

	private int remainingDuration;


	@Override
	protected void startUp() throws Exception
	{
		invigorateTick = -1;
		remainingDuration = 0;
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		invigorateTick = -1;
		remainingDuration = 0;
	}

	public boolean isHeartActive() {
		return invigorateTick > 0;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		String message = event.getMessage();

		if (message.equals(IMBUED_HEART_READY_MESSAGE))
		{
			invigorateTick = -1;
			remainingDuration = 0;
		}

		Matcher heartBusy = IMBUED_HEART_BUSY_MESSAGE.matcher(message);
		if (heartBusy.find()) {
			remainingDuration = (int) ((Integer.parseInt(heartBusy.group(1)) * 60) / 0.6);
			invigorateTick = client.getTickCount();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuOption().contains("Invigorate") && event.getId() == ItemID.IMBUED_HEART) {
			invigorateTick = client.getTickCount();
			remainingDuration = invigorateDuration;
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (invigorateTick > 0 && client.getTickCount() > invigorateTick + remainingDuration) {
			invigorateTick = -1;
			remainingDuration = 0;
		}
	}

	@Provides
	ImbuedHeartNotifierConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImbuedHeartNotifierConfig.class);
	}
}
