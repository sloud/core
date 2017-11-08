package com.reynke.sloud.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.reynke.sloud.core.dependencyinjection.CoreModule;
import com.reynke.sloud.databaseutilities.configuration.DatabaseConfiguration;
import com.reynke.sloud.databaseutilities.configuration.IDatabaseConfiguration;
import com.reynke.sloud.databaseutilities.database.IDatabase;
import com.reynke.sloud.databaseutilities.database.IDatabaseEntitiesAware;
import com.reynke.sloud.databaseutilities.dependencyinjection.DatabaseUtilitiesModule;
import com.reynke.sloud.databaseutilities.exception.DatabaseUtilitiesException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
@Singleton
public class CorePlugin extends JavaPlugin implements ICorePlugin {

    private Injector injector;
    private Injector databaseUtilitiesInjector;

    @Override
    public void onEnable() {

        super.onEnable();

        this.setUpInjector();
        this.setUpConfiguration();
        this.setUpDatabaseUtilities();
    }

    @Override
    public void onDisable() {

        super.onDisable();

        // Close the database connection on disable.
        try {

            this.getLogger().log(Level.INFO, "Trying to close database connection ...");
            databaseUtilitiesInjector.getInstance(IDatabase.class).closeDatabaseConnection();

        } catch (DatabaseUtilitiesException e) {

            this.getLogger().log(Level.SEVERE, "Error closing database connection: " + e.getMessage());
            e.printStackTrace();

        } finally {

            this.getLogger().log(Level.INFO, "Successfully closed database connection.");
        }
    }

    @Override
    public void setUpInjector() {
        injector = Guice.createInjector(new CoreModule(this));
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public Injector getDatabaseUtilitiesInjector() {
        return databaseUtilitiesInjector;
    }

    @Override
    public void setUpConfiguration() {
        this.saveDefaultConfig();
    }

    @Override
    public void setUpDatabaseUtilities() {

        this.getLogger().log(Level.INFO, "Configuring database connection ...");

        FileConfiguration fileConfiguration = this.getConfig();
        IDatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        databaseConfiguration.setHost(fileConfiguration.getString("database.host"));
        databaseConfiguration.setPort(fileConfiguration.getInt("database.port"));
        databaseConfiguration.setDatabaseName(fileConfiguration.getString("database.databaseName"));
        databaseConfiguration.setUsername(fileConfiguration.getString("database.username"));
        databaseConfiguration.setPassword(fileConfiguration.getString("database.password"));

        databaseConfiguration.setPackages(new ArrayList<>());
        databaseConfiguration.setAnnotatedClasses(new ArrayList<>());

        Plugin[] plugins = this.getServer().getPluginManager().getPlugins();

        this.getLogger().log(Level.INFO, "Loading annotated classes and packages containing them from plugins implementing \"" + IDatabaseEntitiesAware.class.getName() + "\" ...");

        for (Plugin plugin : plugins) {

            // Don't import packages and annotated classes held by this plugin
            if (plugin.getName().equals(this.getName())) {
                continue;
            }

            // Make sure the plugin is a database entities aware plugin
            if (!(plugin instanceof IDatabaseEntitiesAware)) {
                continue;
            }

            this.getLogger().log(Level.INFO, "Loading annotated classes and packages from \"" + plugin.getName() + "\" ...");

            IDatabaseEntitiesAware databaseEntitiesAwarePlugin = (IDatabaseEntitiesAware) plugin;

            databaseConfiguration.getPackages().addAll(databaseEntitiesAwarePlugin.getPackages());
            databaseConfiguration.getAnnotatedClasses().addAll(databaseEntitiesAwarePlugin.getAnnotatedClasses());

            this.getLogger().log(Level.INFO, "Successfully loaded annotated classes and packages from \"" + plugin.getName() + "\" ...");
        }

        this.getLogger().log(Level.INFO, "Trying to set up the database connection ...");

        databaseUtilitiesInjector = injector.createChildInjector(new DatabaseUtilitiesModule(databaseConfiguration));

        this.getLogger().log(Level.INFO, "Successfully set up the database connection!");
    }
}
