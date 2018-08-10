package net.runelite.client.plugins.nab.nabwithdraw;

import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

public class NabWithdrawKeyboardListener implements KeyListener {
    @Inject
    private NabWithdrawPlugin plugin;

    @Override
    public void keyTyped ( KeyEvent e ) {

    }

    @Override
    public void keyPressed ( KeyEvent e ) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            plugin.setShiftModifier(true);
        }
    }

    @Override
    public void keyReleased ( KeyEvent e ) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            plugin.setShiftModifier(false);
        }
    }
}
