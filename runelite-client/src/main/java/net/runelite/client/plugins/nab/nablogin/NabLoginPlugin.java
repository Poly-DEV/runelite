package net.runelite.client.plugins.nab.nablogin;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SessionOpen;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor( name = "NabLogin", description = "Easily select which account to login to", tags = { "login" } )
@Slf4j
public class NabLoginPlugin extends Plugin {
    @Inject
    private Client client;
    
    @Override
    protected void startUp() throws Exception
    {
        applyUsername();
    }
    
    @Subscribe
    public void onGameStateChanged( GameStateChanged event ){
        if ( event.getGameState() == GameState.LOGIN_SCREEN ) {
            applyUsername();
        }
    }
    
    @Subscribe
    public void onSessionOpen(SessionOpen event)
    {
        // configuation for the account is available now, so update the username
        applyUsername();
    }
    
    private void applyUsername () {
        GameState gameState = client.getGameState();
        if ( gameState == GameState.LOGIN_SCREEN ) {
            //String username = config.username();
            //
            //if ( Strings.isNullOrEmpty( username ) ) {
            //    return;
            //}
            //
            //// Save it only once
            //if ( usernameCache == null ) {
            //    usernameCache = client.getPreferences().getRememberedUsername();
            //}
            //
            //client.getPreferences().setRememberedUsername( username );
            client.setUsername( "testguy" );
        }
    }
}
