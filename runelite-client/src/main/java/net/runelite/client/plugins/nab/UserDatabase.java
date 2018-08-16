package net.runelite.client.plugins.nab;

import lombok.Getter;
import net.runelite.client.RuneLite;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDatabase {
    public static final File SQL_DIR = new File( RuneLite.RUNELITE_DIR, "sql" );
    
    @Getter
    private String username;
    
    @Getter
    private Connection database;
    
    static {
        SQL_DIR.mkdirs();
    }
    
    public UserDatabase ( String username, String database ) {
        this.username = username;
        
        if ( this.database != null ) { return; }
        try {
            this.database = DriverManager.getConnection( "jdbc:hsqldb:file:" + SQL_DIR.getAbsolutePath() + "/" + this.username + "/" + database, "sa", "" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
}
