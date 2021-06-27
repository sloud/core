package com.reynke.sloud.core.dependencyinjection;

import com.google.inject.AbstractModule;
import com.reynke.sloud.core.ICorePlugin;
import com.reynke.sloud.core.IModuleAware;
import com.reynke.sloud.core.controller.ControllerFactory;
import com.reynke.sloud.core.controller.IControllerFactory;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public class CoreModule extends AbstractModule {
    private final ICorePlugin corePlugin;

    public CoreModule(ICorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    protected void configure() {
        bind(ICorePlugin.class).toInstance(corePlugin);
        bind(IControllerFactory.class).to(ControllerFactory.class);

        corePlugin.getLogger().log(Level.INFO, "Successfully installed dependency injection module from \"DatabaseUtilities\".");

        // Loading dependency injection modules from plugins
        for (Plugin plugin : corePlugin.getServer().getPluginManager().getPlugins()) {
            // Make sure the plugin is a dependency injection module aware plugin
            if (!(plugin instanceof IModuleAware moduleAwarePlugin)) {
                continue;
            }

            install(moduleAwarePlugin.getModule());
            corePlugin.getLogger().log(Level.INFO, "Successfully installed dependency injection module from Plugin \"" + plugin.getName() + "\".");
        }
    }
}
