package com.reynke.sloud.core.controller;

import com.google.inject.ImplementedBy;
import com.reynke.sloud.core.exception.CoreException;
import com.reynke.sloud.databaseutilities.entity.IEntity;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
@ImplementedBy(ControllerFactory.class)
public interface IControllerFactory {

    IController getController(Class<? extends IEntity> entityType) throws CoreException;
}
