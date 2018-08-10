package net.runelite.client.plugins.nab.nabwithdraw;

import net.runelite.api.Client;
import net.runelite.client.input.MouseListener;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

public class NabWithdrawMouseListener extends MouseListener {
    @Inject
    private Client client;

    @Inject
    private NabWithdrawPlugin plugin;

    @Override
    public MouseEvent mouseReleased ( MouseEvent mouseEvent ) {
        Point point = mouseEvent.getPoint();
        int button = mouseEvent.getButton();

        if ( button != 1 ){
            return mouseEvent;
        }

        if ( plugin.onClick( point.x - 5, point.y - 20 ) ) {
            mouseEvent.consume();
        }

        return mouseEvent;
    }
}
