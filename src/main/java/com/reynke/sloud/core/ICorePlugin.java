package com.reynke.sloud.core;

import com.google.inject.Injector;
import org.bukkit.plugin.Plugin;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface ICorePlugin extends Plugin, IInjectorAware {

    String PLUGIN_NAME = "Core";

    void setUpConfiguration();

    void setUpDatabaseUtilities();

    Injector getDatabaseUtilitiesInjector();
}
