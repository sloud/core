package com.reynke.sloud.core;

import com.google.inject.Injector;
import org.bukkit.plugin.Plugin;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public interface ICorePlugin extends Plugin {
    String PLUGIN_NAME = "Core";

    /**
     * @return The core injector.
     */
    Injector getInjector();
}
