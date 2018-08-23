package net.runelite.client.plugins.nab.nabwithdraw;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NabWithdrawOverlay extends Overlay {

    private final Client client;
    private final NabWithdrawPlugin plugin;
    private final SpriteManager spriteManager;

    private Rectangle option1;
    private Rectangle option5;
    private Rectangle option10;
    private Rectangle optionX;
    private Rectangle optionAll;

    @Inject
    private NabWithdrawOverlay ( Client client, NabWithdrawPlugin plugin, SpriteManager spriteManager ) {
        setLayer( OverlayLayer.ABOVE_WIDGETS );
        this.client = client;
        this.plugin = plugin;
        this.spriteManager = spriteManager;
    }

    @Override
    public Dimension render ( Graphics2D graphics ) {
        Widget widgetBankItems = client.getWidget( WidgetInfo.BANK_ITEM_CONTAINER );

        if ( widgetBankItems == null || widgetBankItems.isHidden() ) {
            return null;
        }

        Rectangle bounds = widgetBankItems.getBounds();

        Point mouse = client.getMouseCanvasPosition();

        option1 = new Rectangle( bounds.x - 55, bounds.y - 20, 36, 32 );
        option5 = new Rectangle( bounds.x - 55, bounds.y - 20 + 36, 36, 32 );
        option10 = new Rectangle( bounds.x - 55, bounds.y - 20 + 72, 36, 32 );
        optionX = new Rectangle( bounds.x - 55, bounds.y - 20 + 108, 36, 32 );
        optionAll = new Rectangle( bounds.x - 55, bounds.y - 20 + 144, 36, 32 );

        drawButton( graphics, !plugin.isControlModifier() && ( plugin.isShiftModifier() || plugin.selectedEntry == 1 ), option1 );
        drawButton( graphics, !plugin.isShiftModifier() && !plugin.isControlModifier() && plugin.selectedEntry == 2, option5 );
        drawButton( graphics, !plugin.isShiftModifier() && !plugin.isControlModifier() && plugin.selectedEntry == 3, option10 );
        drawButton( graphics, !plugin.isShiftModifier() && ( plugin.isControlModifier() || plugin.selectedEntry == 4 ), optionX );
        drawButton( graphics, !plugin.isShiftModifier() && !plugin.isControlModifier() && plugin.selectedEntry == 6, optionAll );

        graphics.drawString( "1", option1.x + 13, option1.y + 22 );
        graphics.drawString( "5", option5.x + 13, option5.y + 22 );
        graphics.drawString( "10", option10.x + 10, option10.y + 22 );
        graphics.drawString( "X", optionX.x + 13, optionX.y + 22 );
        graphics.drawString( "All", optionAll.x + 10, optionAll.y + 22 );

        BufferedImage active = spriteManager.getSprite( 0 , 0);

        return null;
    }

    public int getClickedOption( int x, int y ){
        if ( option1 == null ){
            return -1;
        }

        if ( option1.contains( x, y ) ){
            return 1;
        } else if ( option5.contains( x, y ) ){
            return 2;
        } else if ( option10.contains( x, y ) ){
            return 3;
        } else if ( optionX.contains( x, y ) ){
            return 4;
        } else if ( optionAll.contains( x, y ) ){
            return 6;
        }

        return -1;
    }

    public void drawButton( Graphics2D graphics, boolean selected, Rectangle rect ){
        if ( selected ){
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_TOP_LEFT_SELECTED, 0 ), rect.x, rect.y, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_TOP_SELECTED, 0 ), rect.x + 6, rect.y, rect.width - 12, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_TOP_RIGHT_SELECTED, 0 ), rect.x + rect.width - 6, rect.y, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_LEFT_SELECTED, 0 ), rect.x, rect.y + 6, 6, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_MIDDLE_SELECTED, 0 ), rect.x + 6, rect.y + 6, rect.width - 12, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_RIGHT_SELECTED, 0 ), rect.x + rect.width - 6, rect.y + 6, 6, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_BOTTOM_LEFT_SELECTED, 0 ), rect.x, rect.y + rect.height - 6, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_BOTTOM_SELECTED, 0 ), rect.x + 6, rect.y + rect.height - 6, rect.width - 12, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_BOTTOM_RIGHT_SELECTED, 0 ), rect.x + rect.width - 6, rect.y + rect.height - 6, 6, 6, null );
        } else {
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_TOP_LEFT, 0 ), rect.x, rect.y, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_TOP, 0 ), rect.x + 6, rect.y, rect.width - 12, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_TOP_RIGHT, 0 ), rect.x + rect.width - 6, rect.y, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_LEFT, 0 ), rect.x, rect.y + 6, 6, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_MIDDLE, 0 ), rect.x + 6, rect.y + 6, rect.width - 12, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_RIGHT, 0 ), rect.x + rect.width - 6, rect.y + 6, 6, rect.height - 12, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_BOTTOM_LEFT, 0 ), rect.x, rect.y + rect.height - 6, 6, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_EDGE_BOTTOM, 0 ), rect.x + 6, rect.y + rect.height - 6, rect.width - 12, 6, null );
            graphics.drawImage( spriteManager.getSprite( SpriteID.BUTTON_CORNER_BOTTOM_RIGHT, 0 ), rect.x + rect.width - 6, rect.y + rect.height - 6, 6, 6, null );
        }
    }
}
