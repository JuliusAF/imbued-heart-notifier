package com.imbuedheartnotifier;

import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImbuedHeartNotifierOverlay extends WidgetItemOverlay {
    private final ItemManager itemManager;
    private final ImbuedHeartNotifierPlugin plugin;
    private final ImbuedHeartNotifierConfig config;

    @Inject
    private ImbuedHeartNotifierOverlay(ItemManager itemManager, ImbuedHeartNotifierPlugin plugin, ImbuedHeartNotifierConfig config) {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (itemId == ItemID.IMBUED_HEART && !plugin.isHeartActive()) {
            Color color = config.getHighlightColor();
            if (color != null) {
                Rectangle bounds = itemWidget.getCanvasBounds();
                if (config.outlineHeart()) {
                    final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
                    graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
                }

                if (config.fillHeart()) {
                    final Color fillColor = ColorUtil.colorWithAlpha(color, config.getFillHeartOpacity());
                    Image image = ImageUtil.fillImage(itemManager.getImage(ItemID.IMBUED_HEART, itemWidget.getQuantity(), false), fillColor);
                    graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
                }
            }
        }
    }
}
