package com.reynke.sloud.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.reynke.sloud.core.dependencyinjection.CoreModule;
import com.reynke.sloud.databaseutilities.database.IDatabase;
import com.reynke.sloud.databaseutilities.exception.DatabaseUtilitiesException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
@Singleton
public class CorePlugin extends JavaPlugin implements ICorePlugin {

    private Injector injector;

    @Override
    public void onEnable() {

        super.onEnable();

        this.setUpConfiguration();
        this.setUpInjector();

        this.getLogger().log(Level.INFO, "Done.");
    }

    @Override
    public void onDisable() {

        super.onDisable();

        // Close the database connection on disable.
        try {

            this.getLogger().log(Level.INFO, "Trying to close database connection ...");
            this.getInjector().getInstance(IDatabase.class).closeDatabaseConnection();

        } catch (DatabaseUtilitiesException e) {

            this.getLogger().log(Level.SEVERE, "Error closing database connection: " + e.getMessage());
            e.printStackTrace();

        } finally {

            this.getLogger().log(Level.INFO, "Successfully closed database connection.");
        }
    }

    private void setUpInjector() {
        injector = Guice.createInjector(new CoreModule(this));
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    private void setUpConfiguration() {
        this.saveDefaultConfig();
    }
}
