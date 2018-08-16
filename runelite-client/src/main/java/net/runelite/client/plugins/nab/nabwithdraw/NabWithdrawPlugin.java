package net.runelite.client.plugins.nab.nabwithdraw;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor( name = "NabWithdraw", description = "Change the default count of items that is withdrawn from bank", tags = { "bank", "inventory", "items", "objects", "withdraw" } )
@Slf4j
public class NabWithdrawPlugin extends Plugin {
    @Inject
    private Client client;
    
    @Inject
    private MenuManager menuManager;
    
    @Inject
    private ConfigManager configManager;
    
    @Inject
    private NabWithdrawOverlay overlay;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private MouseManager mouseManager;
    
    @Inject
    private NabWithdrawMouseListener mouseListener;
    
    @Inject
    private KeyManager keyManager;
    
    @Inject
    private NabWithdrawKeyboardListener keyboardListener;
    
    private static final String CONFIG_GROUP = "nabwithdraw";
    
    int selectedEntry;
    
    @Getter
    private boolean shiftModifier;
    
    public void setShiftModifier( boolean mod ){
        shiftModifier = !controlModifier && mod;
    }
    
    @Getter
    private boolean controlModifier;
    
    public void setControlModifier( boolean mod ){
        controlModifier = !shiftModifier && mod;
    }
    
    @Override
    public void startUp () {
        overlayManager.add( overlay );
        mouseManager.registerMouseListener( mouseListener );
        keyManager.registerKeyListener( keyboardListener );
        selectedEntry = Integer.parseInt( configManager.getConfiguration( CONFIG_GROUP, "selected" ) );
    }
    
    @Override
    public void shutDown () {
        overlayManager.remove( overlay );
        mouseManager.unregisterMouseListener( mouseListener );
        keyManager.unregisterKeyListener( keyboardListener );
    }
    
    @Subscribe
    public void onMenuEntryAdded ( MenuEntryAdded event ) {
        if ( client.getGameState() != GameState.LOGGED_IN ) {
            return;
        }
        
        MenuEntry[] entries = client.getMenuEntries();
        
        if ( event.getActionParam1() != WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId() && event.getActionParam1() != WidgetInfo.BANK_ITEM_CONTAINER.getId() ) {
            return;
        }
        
        if ( event.getActionParam1() != WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId() && entries.length != 10 ) {
            return;
        }
        
        if ( event.getActionParam1() != WidgetInfo.BANK_ITEM_CONTAINER.getId() && entries.length != 8 ) {
            return;
        }
        
        int current = selectedEntry;
        
        if ( shiftModifier ){
            return;
        }
        
        if ( controlModifier ){
            current = 4;
        }
        
        //1 -> withdraw 1
        //2 -> withdraw 5
        //3 -> withdraw 10
        //4 -> withdraw X
        //6 -> withdraw All
        
        int count = entries.length;
        
        MenuEntry tmp = entries[ count - 1 ];
        entries[ count - 1 ] = entries[ count - current ];
        entries[ count - current ] = tmp;
        client.setMenuEntries( entries );
    }
    
    public boolean onClick ( int x, int y ) {
        int option = overlay.getClickedOption( x, y );
        if ( option <= 0 ) {
            return false;
        }
        
        selectedEntry = option;
        
        configManager.setConfiguration( CONFIG_GROUP, "selected", option );
        
        return true;
    }
}
