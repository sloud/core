package com.reynke.sloud.core.dependencyinjection;

import com.google.inject.AbstractModule;
import com.reynke.sloud.core.ICorePlugin;
import com.reynke.sloud.core.controller.ControllerFactory;
import com.reynke.sloud.core.controller.IControllerFactory;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public class CoreModule extends AbstractModule {

    private ICorePlugin corePlugin;

    public CoreModule(ICorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    protected void configure() {
        bind(ICorePlugin.class).toInstance(corePlugin);
        bind(IControllerFactory.class).to(ControllerFactory.class);
    }
}
