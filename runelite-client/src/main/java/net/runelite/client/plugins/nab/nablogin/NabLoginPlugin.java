package net.runelite.client.plugins.nab.nablogin;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SessionOpen;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

@PluginDescriptor( name = "NabLogin", description = "Easily select which account to login to", tags = { "login" } )
@Slf4j
public class NabLoginPlugin extends Plugin {
    @Inject
    private Client client;
    
    @Inject
    private ClientToolbar clientToolbar;
    
    @Inject
    private ConfigManager configManager;
    
    private NavigationButton button;
    private NabLoginPanel panel;
    
    private String username = "";
    
    public void changeUser ( String username ) {
        this.username = username;
        applyUsername();
    }
    
    @Override
    protected void startUp () throws Exception {
        panel = injector.getInstance( NabLoginPanel.class );
        
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass( getClass(), "icon.png" );
        
        button = NavigationButton.builder().tooltip( "Accounts" ).icon( icon ).priority( 1 ).panel( panel ).build();
        panel.load( getAccounts() );
        
        clientToolbar.addNavigation( button );
        
        applyUsername();
    }
    
    @Override
    protected void shutDown () {
        saveAccounts();
        clientToolbar.removeNavigation( button );
    }
    
    private Collection< NabAccount > getAccounts () {
        String data = configManager.getConfiguration( "nablogin", "accounts" );
        if ( data == null ) {
            data = "{}";
        }
        
        ArrayList< NabAccount > ret = new ArrayList<>();
        
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse( data ).getAsJsonObject();
        for ( String account : json.keySet() ) {
            JsonElement e = json.get( account );
            
            ret.add( new NabAccount( account, e.isJsonNull() ? "null" : e.getAsString() ) );
        }
        
        return ret;
    }
    
    private void saveAccounts () {
        JsonObject json = new JsonObject();
        for ( NabAccount account : panel.getAccounts() ) {
            json.addProperty( account.getAccount(), account.getUsername() );
        }
        
        configManager.setConfiguration( "nablogin", "accounts", json.toString() );
    }
    
    private boolean needsUpdate = false;
    private void addCurrentAccount () {
        needsUpdate = true;
    }
    
    @Subscribe
    public void onGameStateChanged ( GameStateChanged event ) {
        if ( event.getGameState() == GameState.LOGIN_SCREEN ) {
            applyUsername();
        } else if ( event.getGameState() == GameState.LOADING ) {
            for ( NabAccount account : panel.getAccounts() ) {
                if ( account.getAccount().equalsIgnoreCase( client.getUsername() ) ) {
                    if ( !account.getUsername().equalsIgnoreCase( client.getLocalPlayer().getName() ) ) {
                        SwingUtilities.invokeLater( () -> {
                            panel.getAccounts().remove( account );
                        } );
                        break;
                    }
                    return;
                }
            }
            addCurrentAccount();
        }
    }
    
    @Subscribe
    private void onGameTick( GameTick event ){
        if ( !needsUpdate || client.getLocalPlayer().getName().equals( "" ) ){
            return;
        }
        panel.getAccounts().add( new NabAccount( client.getUsername(), client.getLocalPlayer().getName() ) );
        panel.updateAccounts();
        saveAccounts();
        needsUpdate = false;
    }
    
    @Subscribe
    public void onSessionOpen ( SessionOpen event ) {
        // configuation for the account is available now, so update the username
        applyUsername();
    }
    
    private void applyUsername () {
        GameState gameState = client.getGameState();
        
        if ( gameState == GameState.LOGIN_SCREEN ) {
            client.setUsername( username );
        }
        
        client.setUsername( username );
    }
}
