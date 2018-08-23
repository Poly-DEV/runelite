package net.runelite.client.plugins.nab.nabbank;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.BankItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nab.UserDatabase;
import net.runelite.client.util.QueryRunner;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@PluginDescriptor( name = "Nab Bank Manager", description = "Control and record your bank", tags = { "loot", "bank" } )
@Slf4j
public class NabBankPlugin extends Plugin {
    @Inject
    private Client client;
    
    @Inject
    private ItemManager itemManager;
    
    @Inject
    private QueryRunner queryRunner;
    
    private int itemsHash;
    
    private UserDatabase userDatabase;
    
    public static final File DATABASE_DIR = new File( RUNELITE_DIR, "sql" );
    
    private Map< Integer, Integer > bankItems; // < id, quantity >
    
    @Override
    public void startUp () {
        DATABASE_DIR.mkdirs();
    }
    
    @Subscribe
    public void onGameStateChanged ( final GameStateChanged gameStateChanged ) {
        if ( gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ) {
            if ( userDatabase != null ){
                try {
                    userDatabase.getDatabase().commit();
                    userDatabase.getDatabase().close();
                    userDatabase = null;
                    bankItems = null;
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Subscribe
    public void onGameTick ( GameTick event ) {
        Widget widgetBankTitleBar = client.getWidget( WidgetInfo.BANK_TITLE_BAR );
        
        if ( widgetBankTitleBar == null || widgetBankTitleBar.isHidden() ) {
            return;
        }
    
        try {
            save( widgetBankTitleBar );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        
        testShit();
    }
    
    private void testShit (){
    //    Widget widgetBankTitleBar = client.getWidget( WidgetInfo.BANK_TITLE_BAR );
    //    Widget bankItemContainer = client.getWidget( WidgetInfo.BANK_ITEM_CONTAINER );
    //
    //    if ( widgetBankTitleBar == null || widgetBankTitleBar.isHidden() || widgetBankTitleBar.getText().contains( "Showing" ) || widgetBankTitleBar.getText().contains( "Tab" ) ) {
    //        return;
    //    }
    //
    //    WidgetItem coins = getItems()[ 39 ];
    //
    //    Widget test = bankItemContainer.createChild( 96, 5 );
    //    WidgetItem item = test.getWidgetItem( 0 );
    //    log.info( "{}", item.getId() );
    }
    
    private void save ( Widget widgetBankTitleBar ) throws SQLException {
        WidgetItem[] items = getItems();
        
        if ( widgetBankTitleBar == null || widgetBankTitleBar.isHidden() || widgetBankTitleBar.getText().contains( "Showing" ) || widgetBankTitleBar.getText().contains( "Tab" ) ) {
            return;
        }
        
        if ( items.length == 0 || !isBankDifferent( items ) ) {
            return;
        }
    
        tryConnect();
    
        List<WidgetItem> diff = getDifferences( items );
        
        bankItems = new HashMap<>(  );
        for( WidgetItem item : items ){
            bankItems.put( item.getId(), item.getQuantity() );
        }
        
        Statement statement = userDatabase.getDatabase().createStatement();
        for ( WidgetItem item : diff ) {
            int id = item.getId();
            int quantity = item.getQuantity();
            String name = itemManager.getItemComposition( id ).getName().replace( "'", "\'\'" );
            String sql = "MERGE INTO ITEMS AS t USING (VALUES(" + id + "," + quantity + ",'" + name + "')) AS vals(id, quantity, name) " +
                    "ON t.id=vals.id " +
                    "WHEN MATCHED THEN UPDATE SET t.QUANTITY = vals.quantity " +
                    "WHEN NOT MATCHED THEN INSERT VALUES vals.id, vals.quantity, vals.name;";
            statement.addBatch( sql );
        }
        statement.executeBatch();
    }
    
    private boolean isBankDifferent ( WidgetItem[] items ) {
        Map< Integer, Integer > mapCheck = new HashMap<>();
        
        for ( WidgetItem widgetItem : items ) {
            mapCheck.put( widgetItem.getId(), widgetItem.getQuantity() );
        }
        
        int curHash = mapCheck.hashCode();
        
        if ( curHash != itemsHash ) {
            itemsHash = curHash;
            return true;
        }
        
        return false;
    }
    
    private List< WidgetItem > getDifferences ( WidgetItem[] newItems ){
        if ( bankItems == null ){
            return Arrays.asList( newItems );
        }
        
        List< WidgetItem > ret = new ArrayList<>(  );
        
        for( WidgetItem item : newItems ){
            if ( bankItems.containsKey( item.getId() ) ){
                if ( bankItems.get( item.getId() ) != item.getQuantity() ){
                    ret.add( item );
                }
            } else {
                ret.add( item );
            }
        }
        
        return ret;
    }
    
    public WidgetItem[] getItems () {
        return queryRunner.runQuery( new BankItemQuery() );
    }
    
    private void tryConnect () {
        try {
            if ( userDatabase != null && userDatabase.getDatabase().isValid( 2 ) ) {
                return;
            }
            userDatabase = new UserDatabase( client.getLocalPlayer().getName(), "bank" );
    
            //Run create script
            Statement s = userDatabase.getDatabase().createStatement();
            s.addBatch( "CREATE TABLE IF NOT EXISTS ITEMS( id INT PRIMARY KEY, quantity INT, name VARCHAR(32) );" ); //ITEM TABLE
            s.executeBatch();
            s.close();
        } catch ( SQLException e ){
            e.printStackTrace();
        }
    }
}
