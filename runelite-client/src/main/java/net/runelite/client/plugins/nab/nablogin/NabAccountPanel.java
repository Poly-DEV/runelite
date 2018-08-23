package net.runelite.client.plugins.nab.nablogin;

import net.runelite.api.Experience;
import net.runelite.client.ui.ColorScheme;
import net.runelite.http.api.hiscore.HiscoreClient;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import net.runelite.http.api.hiscore.HiscoreResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class NabAccountPanel extends JPanel {
    private static final Dimension ICON_SIZE = new Dimension( 32, 32 );
    
    private final HiscoreClient hiscoreClient = new HiscoreClient();
    
    public NabAccountPanel ( NabLoginPlugin plugin, String login, String username ) {
        BorderLayout layout = new BorderLayout();
        layout.setHgap( 5 );
        setLayout( layout );
        setToolTipText( username );
        setBackground( ColorScheme.DARKER_GRAY_COLOR );
        
        addMouseListener( new MouseAdapter() {
            @Override
            public void mouseReleased ( MouseEvent e ) {
                plugin.changeUser( login );
            }
            
            @Override
            public void mouseEntered ( MouseEvent e ) {
                setBackground( ColorScheme.DARKER_GRAY_HOVER_COLOR );
                for ( Component component : getComponents() ) {
                    component.setBackground( ColorScheme.DARKER_GRAY_HOVER_COLOR );
                }
                setCursor( new Cursor( Cursor.HAND_CURSOR ) );
            }
            
            @Override
            public void mouseExited ( MouseEvent e ) {
                setBackground( ColorScheme.DARKER_GRAY_COLOR );
                for ( Component component : getComponents() ) {
                    component.setBackground( ColorScheme.DARKER_GRAY_COLOR );
                }
                setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        
        setBorder( new EmptyBorder( 5, 5, 5, 0 ) );
        
        // Icon
        JLabel accountIcon = new JLabel();
        accountIcon.setPreferredSize( ICON_SIZE );
        add( accountIcon, BorderLayout.LINE_START );
        
        // Account details panel
        JPanel rightPanel = new JPanel( new GridLayout( 3, 1 ) );
        rightPanel.setBackground( ColorScheme.DARKER_GRAY_COLOR );
        
        // Account name
        JLabel accountName = new JLabel();
        accountName.setForeground( Color.WHITE );
        accountName.setMaximumSize( new Dimension( 0, 0 ) );        // to limit the label's size for
        accountName.setPreferredSize( new Dimension( 0, 0 ) );    // items with longer names
        accountName.setText( username );
        rightPanel.add( accountName );
    
        SwingUtilities.invokeLater( () -> {
        
            HiscoreResult result;
        try {
            result = hiscoreClient.lookup( username, HiscoreEndpoint.NORMAL );
        } catch ( IOException ex ) {
            return;
        }
        
            // Total level
            JLabel totalLevelLabel = new JLabel();
            totalLevelLabel.setForeground( ColorScheme.GRAND_EXCHANGE_PRICE );
            rightPanel.add( totalLevelLabel );
            
            // Combat level
            JLabel combatLevelLabel = new JLabel();
            combatLevelLabel.setForeground( ColorScheme.GRAND_EXCHANGE_ALCH );
            rightPanel.add( combatLevelLabel );
            
            if ( result == null ) {
                totalLevelLabel.setText( "Total: ??" );
                combatLevelLabel.setText( "Combat: ??" );
            } else {
                totalLevelLabel.setText( "Total: " + result.getOverall().getLevel() );
                int combatLevel = Experience.getCombatLevel( result.getAttack().getLevel(), result.getStrength().getLevel(), result.getDefence().getLevel(), result.getHitpoints().getLevel(), result.getMagic().getLevel(), result.getRanged().getLevel(), result.getPrayer().getLevel() );
                combatLevelLabel.setText( "Combat: " + combatLevel );
            }
        } );
        
        add( rightPanel, BorderLayout.CENTER );
    }
}
