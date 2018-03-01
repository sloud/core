package com.reynke.sloud.core.listener;

import com.reynke.sloud.core.controller.IController;
import com.reynke.sloud.databaseutilities.entity.IEntity;
import org.bukkit.event.Listener;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IListener<T extends IEntity> extends Listener {

    /**
     * @param entityType The type of the entity that holds information about
     *                   which controller should be responsible fot this listener.
     * @return The controller responsible for this listener.
     */
    IController getController(T entityType);
}
