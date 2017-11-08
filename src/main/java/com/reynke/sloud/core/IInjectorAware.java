package com.reynke.sloud.core;

import com.google.inject.Injector;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface IInjectorAware {

    void setUpInjector();

    Injector getInjector();
}
