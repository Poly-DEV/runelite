package net.runelite.client.plugins.nabloot;

import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class NabLootPanel extends PluginPanel {
    @Inject
    private Client client;
    
    private final NabLootConfig config;
    
    @Inject
    public NabLootPanel( NabLootConfig config ){
        super();
        this.config = config;
    }
}
