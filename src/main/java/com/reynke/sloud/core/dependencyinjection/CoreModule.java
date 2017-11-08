package com.reynke.sloud.core.dependencyinjection;

import com.google.inject.PrivateModule;
import com.reynke.sloud.core.ICorePlugin;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
public class CoreModule extends PrivateModule {

    private ICorePlugin corePlugin;

    public CoreModule(ICorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    protected void configure() {
        bind(ICorePlugin.class).toInstance(corePlugin);
        expose(ICorePlugin.class);
    }
}
