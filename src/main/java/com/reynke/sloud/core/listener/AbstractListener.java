package com.reynke.sloud.core.listener;

import com.google.inject.Inject;
import com.reynke.sloud.core.controller.IController;
import com.reynke.sloud.core.controller.IControllerFactory;
import com.reynke.sloud.core.exception.CoreException;
import com.reynke.sloud.databaseutilities.entity.IEntity;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public abstract class AbstractListener<T extends IEntity<?>> implements IListener<T> {
    private final IControllerFactory controllerFactory;

    @Inject
    public AbstractListener(IControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @Override
    public IController getController(T entityType) {
        try {
            return controllerFactory.getController((Class<? extends IEntity<?>>) entityType.getClass());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }
}
