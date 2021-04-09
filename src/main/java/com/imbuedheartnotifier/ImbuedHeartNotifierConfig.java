package com.imbuedheartnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("example")
public interface ImbuedHeartNotifierConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "highlightColor",
		name = "Highlight Color",
		description = "Color to highlight your imbued heart when it's ready to invigorate"
	)
	default Color getHighlightColor()
	{
		return new Color(245, 255, 54);
	}

	@ConfigItem(
			position = 1,
			keyName = "fillHeart",
			name = "Fill Heart",
			description = "Fill in the heart with the highlight color"
	)
	default boolean fillHeart() {
		return true;
	}

	@Range(
			max = 255
	)
	@ConfigItem(
			position = 2,
			keyName =  "fillHeartOpacity",
			name = "Fill Opacity",
			description = "The opacity of the highlight color when filling in the heart"
	)
	default int getFillHeartOpacity() {
		return 50;
	}

	@ConfigItem(
			position = 3,
			keyName = "outlineHeart",
			name = "Outline Heart",
			description = "Outline the heart with the highlight color"
	)
	default boolean outlineHeart() {
		return false;
	}

	@ConfigItem(
			position = 4,
			keyName = "enableNotifier",
			name = "Enable Notifier",
			description = "Enable a system notifier when your imbued heart is ready to be used"
	)
	default boolean enableNotifier() {
		return false;
	}
}
