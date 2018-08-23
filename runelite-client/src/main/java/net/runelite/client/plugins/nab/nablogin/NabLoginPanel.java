package net.runelite.client.plugins.nab.nablogin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class NabLoginPanel extends PluginPanel {
    private final GridBagConstraints constraints = new GridBagConstraints();
    
    private final JPanel container = new JPanel();
    private final JPanel accountsPanel = new JPanel();
    
    private JScrollPane accountsWrapper;
    
    private NabLoginPlugin plugin;
    
    @Getter( value = AccessLevel.PACKAGE )
    private java.util.List< NabAccount > accounts = new ArrayList<>();
    
    @Inject
    NabLoginPanel ( Client client, NabLoginPlugin plugin ) {
        super( false );
        
        this.plugin = plugin;
        
        setLayout( new BorderLayout() );
        setBackground( ColorScheme.DARK_GRAY_COLOR );
        
        container.setLayout( new BorderLayout( 5, 5 ) );
        container.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        container.setBackground( ColorScheme.DARK_GRAY_COLOR );
        
        accountsPanel.setLayout( new GridBagLayout() );
        accountsPanel.setBackground( ColorScheme.DARK_GRAY_COLOR );
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        
        container.add( accountsPanel, BorderLayout.NORTH );
        add( container );
    }
    
    public void load ( Collection< NabAccount > accounts ) {
        this.accounts.clear();
        for ( NabAccount account : accounts ) {
            this.accounts.add( new NabAccount( account.getAccount(), account.getUsername() ) );
        }
        
        updateAccounts();
    }
    
    void updateAccounts () {
        SwingUtilities.invokeLater( () -> {
            int i = 0;
            accountsPanel.removeAll();
            Collection< NabAccount > tmp = new ArrayList<>( accounts );
            for ( NabAccount account : tmp ) {
                NabAccountPanel panel = new NabAccountPanel( plugin, account.getAccount(), account.getUsername() );
                
                if ( i++ > 0 ) {
                    JPanel marginWrapper = new JPanel( new BorderLayout() );
                    marginWrapper.setBackground( ColorScheme.DARK_GRAY_COLOR );
                    marginWrapper.setBorder( new EmptyBorder( 5, 0, 0, 0 ) );
                    marginWrapper.add( panel, BorderLayout.NORTH );
                    accountsPanel.add( marginWrapper, constraints );
                } else {
                    accountsPanel.add( panel, constraints );
                }
                
                constraints.gridy++;
            }
        });
    }
}
