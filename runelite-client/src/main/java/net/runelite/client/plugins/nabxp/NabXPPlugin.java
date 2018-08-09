package net.runelite.client.plugins.nabxp;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ExperienceChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@PluginDescriptor( name = "NabXP", description = "Track XP Drops", tags = { "xp" } )
@Slf4j
public class NabXPPlugin extends Plugin {
    @Inject
    private Client client;
    
    private Connection database;
    
    public static final File DATABASE_DIR = new File( RUNELITE_DIR, "sql" );
    
    private Map< Skill, Integer > previousXP = new HashMap<>(  );
    
    @Override
    public void startUp () {
        DATABASE_DIR.mkdirs();
    }
    
    @Subscribe
    public void onGameStateChanged ( final GameStateChanged gameStateChanged ) {
        if ( gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ) {
            if ( database != null ){
                try {
                    database.commit();
                    database.close();
                    database = null;
                    previousXP.clear();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Subscribe
    public void onExperienceChanged(ExperienceChanged event)
    {
        Skill skill = event.getSkill();
        int xp = client.getSkillExperience(skill);
        if ( !previousXP.containsKey( skill ) ){
            previousXP.put( skill, xp );
            return;
        }
        int dif = xp - previousXP.get( skill );
        previousXP.put( skill, xp );
        
        try {
            tryConnect();
    
            Statement s = database.createStatement();
        
            String sql = "INSERT INTO DROPS (ID, SKILL, XP, TOTAL, TIME) VALUES ( NULL, " + skill.ordinal() + ", "+ dif + ", "+  xp +", '" + new Timestamp( System.currentTimeMillis() ) + "' )";
        
            s.execute( sql );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
    
    private void tryConnect () throws SQLException {
        if ( database != null ) { return; }
        database = DriverManager.getConnection( "jdbc:hsqldb:file:" + DATABASE_DIR.getAbsolutePath() + "/" + client.getLocalPlayer().getName() + "/xp", "sa", "" );
        
        //Run create script
        Statement s = database.createStatement();
        s.addBatch( "CREATE TABLE IF NOT EXISTS DROPS( ID INT PRIMARY KEY IDENTITY , SKILL INT, XP INT, TOTAL INT, TIME TIMESTAMP );" );
        s.executeBatch();
        s.close();
    }
}
