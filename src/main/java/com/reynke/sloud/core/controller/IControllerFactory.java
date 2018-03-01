package com.reynke.sloud.core.controller;

import com.reynke.sloud.core.exception.CoreException;
import com.reynke.sloud.databaseutilities.entity.IEntity;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IControllerFactory {

    IController getController(Class<? extends IEntity> entityType) throws CoreException;
}
