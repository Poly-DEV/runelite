package net.runelite.client.plugins.nab.nabloot;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nab.UserDatabase;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.util.Collection;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@PluginDescriptor( name = "Nab Loot Tracker", description = "Track loot that has been dropped", tags = { "loot", "combat", "pvm" } )
@Slf4j
public class NabLootPlugin extends Plugin {
    @Inject
    private Client client;
    
    @Inject
    private NabLootConfig config;
    
    private NabLootPanel lootPanel;
    private NavigationButton navButton;
    
    @Inject
    private ClientToolbar clientToolbar;
    
    @Inject
    private ItemManager itemManager;
    
    private UserDatabase userDatabase;
    
    public static final File DATABASE_DIR = new File( RUNELITE_DIR, "sql" );
    
    @Provides
    private NabLootConfig provideConfig ( ConfigManager configManager ) {
        return configManager.getConfig( NabLootConfig.class );
    }
    
    @Override
    protected void startUp () throws Exception {
        DATABASE_DIR.mkdirs();
        
        lootPanel = injector.getInstance( NabLootPanel.class );
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass( getClass(), "panel_icon.png" );
        
        navButton = NavigationButton.builder().tooltip( "Loot Tracker" ).icon( icon ).priority( 5 ).panel( lootPanel ).build();
        
        clientToolbar.addNavigation( navButton );
    }
    
    @Override
    protected void shutDown () throws Exception {
    }
    
    @Subscribe
    public void onGameStateChanged ( final GameStateChanged gameStateChanged ) {
        if ( gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ) {
            if ( userDatabase != null ){
                try {
                    userDatabase.getDatabase().commit();
                    userDatabase.getDatabase().close();
                    userDatabase = null;
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Subscribe
    public void onNpcLootReceived ( final NpcLootReceived npcLootReceived ) {
        final NPC npc = npcLootReceived.getNpc();
        final Collection< ItemStack > items = npcLootReceived.getItems();
        
        saveNPCLoot( npc, items );
    }
    
    @Subscribe
    public void onPlayerLootReceived ( final PlayerLootReceived playerLootReceived ) {
        final Player player = playerLootReceived.getPlayer();
        final Collection< ItemStack > items = playerLootReceived.getItems();
    }
    
    @Subscribe
    public void onWidgetLoaded ( WidgetLoaded event ) {
        final ItemContainer container;
        switch ( event.getGroupId() ) {
            //USED FOR BARROWS / CLUE SCROLLS... ETC
            //saveWidgetLoot();
        }
    }
    
    private void saveNPCLoot ( NPC npc, Collection< ItemStack > items ){
        tryConnect();
        
        try {
            Statement s = userDatabase.getDatabase().createStatement();
            
            s.execute( "INSERT INTO DROPS (ID, DROP_SOURCE, DROP_TIME) VALUES ( NULL, " + npc.getId() + ", '" + new Timestamp( System.currentTimeMillis() ) + "' )" );
            ResultSet result = s.executeQuery( "SELECT MAX(ID) AS ID FROM DROPS" );
            result.next();
            int id = result.getInt( "ID" );
            
            for ( ItemStack item : items ){
                s.addBatch( "INSERT INTO ITEMS (ID, ITEM_ID, ITEM_COUNT) VALUES ("+ id +", " + item.getId() + ", " + item.getQuantity() + ");" );
            }
            s.executeBatch();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
    
    private void savePlayerLoot( Player player, Collection< ItemStack > items ){
        tryConnect();
    }
    
    private void saveWidgetLoot(){
    
    }
    
    private void tryConnect () {
        try {
            if ( userDatabase != null ) { return; }
            userDatabase = new UserDatabase( client.getLocalPlayer().getName(), "loot" );
            
            //Run create script
            Statement s = userDatabase.getDatabase().createStatement();
            s.addBatch( "CREATE TABLE IF NOT EXISTS ACCOUNTS ( ID INTEGER constraint ACCOUNTS_ID_UINDEX unique, NAME VARCHAR(13) );" );
            s.addBatch( "CREATE TABLE IF NOT EXISTS DROPS( ID INTEGER PRIMARY KEY IDENTITY, DROP_SOURCE INTEGER, DROP_TIME TIMESTAMP);" );
            s.addBatch( "CREATE TABLE IF NOT EXISTS ITEMS( ID INTEGER, ITEM_ID INTEGER, ITEM_COUNT INTEGER, CONSTRAINT ITEMS_DROPS_ID_FK FOREIGN KEY (ID) REFERENCES DROPS (ID));" );
            s.executeBatch();
            s.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
}
