package com.reynke.sloud.core;

import com.google.inject.Module;

/**
 * Interface that states that classes implementing this class are aware of a
 * dependency injection module that can be loaded into the main injector.
 *
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IModuleAware {

    /**
     * Returns the dependency injection module responsible for this class.
     *
     * @return The dependency injection module.
     */
    Module getModule();
}
