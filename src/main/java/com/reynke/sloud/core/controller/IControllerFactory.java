package com.reynke.sloud.core.controller;

import com.reynke.sloud.core.exception.CoreException;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IControllerFactory {
    IController getController(Class<?> classType) throws CoreException;
}
