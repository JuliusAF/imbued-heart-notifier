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
		keyName = "highlightColor",
		name = "Highlight Color",
		description = "Color to highlight your imbued heart when it's ready to invigorate"
	)
	default Color getHighlightColor()
	{
		return new Color(245, 255, 54);
	}

	@ConfigItem(
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
			keyName =  "fillHeartOpacity",
			name = "Fill Opacity",
			description = "The opacity of the highlight color when filling in the heart"
	)
	default int getFillHeartOpacity() {
		return 50;
	}

	@ConfigItem(
			keyName = "outlineHeart",
			name = "Outline Heart",
			description = "Outline the heart with the highlight color"
	)
	default boolean outlineHeart() {
		return false;
	}
}
