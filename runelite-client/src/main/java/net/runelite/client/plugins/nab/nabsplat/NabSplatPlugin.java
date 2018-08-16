package net.runelite.client.plugins.nab.nabsplat;

import com.google.common.eventbus.Subscribe;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor( name = "NabSplat", description = "Highlight hitsplats", tags = { "hit splat" } )
public class NabSplatPlugin extends Plugin {
    @Subscribe
    private void onHitSplat( HitsplatApplied event ){
    
    }
}
