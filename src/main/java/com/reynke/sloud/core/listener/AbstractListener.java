package com.reynke.sloud.core.listener;

import com.google.inject.Inject;
import com.reynke.sloud.core.controller.IController;
import com.reynke.sloud.core.controller.IControllerFactory;
import com.reynke.sloud.core.exception.CoreException;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public abstract class AbstractListener<T> implements IListener<T> {
    private final IControllerFactory controllerFactory;

    @Inject
    public AbstractListener(IControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @Override
    public IController getController(T classType) {
        try {
            return controllerFactory.getController(classType.getClass());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }
}
