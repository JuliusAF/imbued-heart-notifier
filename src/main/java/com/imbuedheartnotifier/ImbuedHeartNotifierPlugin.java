package com.imbuedheartnotifier;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Imbued Heart Notifier"
)
public class ImbuedHeartNotifierPlugin extends Plugin {
	public static final int SATURATED_HEART_ITEM_ID = 27641; // replace with ItemID.SATURATED_HEART with new version of runelite library

	private static final Pattern IMBUED_HEART_READY_MESSAGE = Pattern.compile("<col=.{6}>Your (saturated|imbued) heart has regained its magical power.</col>");
	private static final Pattern IMBUED_HEART_BUSY_MESSAGE = Pattern.compile("The heart is still drained of its power. Judging by how it feels, it will be ready in around (\\d) minutes\\.");

	private static final int invigorateDuration = 700;
	private static final int saturatedDuration = 500;

	@Inject
	private Client client;

	@Inject
	private ImbuedHeartNotifierConfig config;

	@Inject
	private ImbuedHeartNotifierOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	private int invigorateTick;

	private int remainingDuration;


	@Override
	protected void startUp() throws Exception {
		invigorateTick = -1;
		remainingDuration = 0;
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
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

		Matcher heartReady = IMBUED_HEART_READY_MESSAGE.matcher(message);
		if (heartReady.find()) {
			invigorateTick = -1;
			remainingDuration = 0;
			notifyUser();
		}

		Matcher heartBusy = IMBUED_HEART_BUSY_MESSAGE.matcher(message);
		if (heartBusy.find()) {
			remainingDuration = (int) ((Integer.parseInt(heartBusy.group(1)) * 60) / 0.6);
			invigorateTick = client.getTickCount();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuOption().contains("Invigorate")) {
			log.debug("Invigorate option clicked");
			invigorateTick = client.getTickCount();
			ItemContainer playerInventory = client.getItemContainer(InventoryID.INVENTORY);
			if (playerInventory != null && playerInventory.contains(SATURATED_HEART_ITEM_ID)) {
				remainingDuration = saturatedDuration;
			} else {
				remainingDuration = invigorateDuration;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (invigorateTick > 0 && client.getTickCount() > invigorateTick + remainingDuration) {
			invigorateTick = -1;
			remainingDuration = 0;
			notifyUser();
		}
	}

	@Provides
	ImbuedHeartNotifierConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ImbuedHeartNotifierConfig.class);
	}

	private void notifyUser() {
		if (config.enableNotifier()) {
			ItemContainer playerInventory = client.getItemContainer(InventoryID.INVENTORY);

			if (playerInventory != null && (playerInventory.contains(ItemID.IMBUED_HEART) || playerInventory.contains(SATURATED_HEART_ITEM_ID))) {
				notifier.notify("Your imbued heart can be used again");
			}
		}
	}
}
