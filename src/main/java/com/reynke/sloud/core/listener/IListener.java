package com.reynke.sloud.core.listener;

import com.reynke.sloud.core.controller.IController;
import org.bukkit.event.Listener;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IListener<T> extends Listener {

    /**
     * @param classType The type of the class that holds information about
     *                  which controller should be responsible fot this listener.
     * @return The controller responsible for this listener.
     */
    IController getController(T classType);
}
