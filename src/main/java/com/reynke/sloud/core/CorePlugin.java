package com.reynke.sloud.core;

import com.google.inject.*;
import com.reynke.sloud.core.dependencyinjection.CoreModule;
import com.reynke.sloud.databaseutilities.configuration.DatabaseConfiguration;
import com.reynke.sloud.databaseutilities.configuration.DatabaseType;
import com.reynke.sloud.databaseutilities.configuration.IDatabaseConfiguration;
import com.reynke.sloud.databaseutilities.database.IDatabase;
import com.reynke.sloud.databaseutilities.database.IDatabaseEntitiesAware;
import com.reynke.sloud.databaseutilities.dependencyinjection.DatabaseUtilitiesModule;
import com.reynke.sloud.databaseutilities.exception.DatabaseUtilitiesException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
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

        List<Module> modules = new ArrayList<>();

        modules.add(new CoreModule(this));
        modules.add(this.setUpDatabaseUtilitiesModule());

        Plugin[] plugins = this.getServer().getPluginManager().getPlugins();

        this.getLogger().log(Level.INFO, "Collecting dependency injection modules from plugins implementing \"" + IModuleAware.class.getName() + "\" ...");

        // Loading dependency injection modules from plugins
        for (Plugin plugin : plugins) {

            // Make sure the plugin is a dependency injection module aware plugin
            if (!(plugin instanceof IModuleAware)) {
                continue;
            }

            this.getLogger().log(Level.INFO, "Loading dependency injection module from \"" + plugin.getName() + "\" ...");

            modules.add(((IModuleAware) plugin).getModule());

            this.getLogger().log(Level.INFO, "Successfully loaded dependency injection module from \"" + plugin.getName() + "\" ...");
        }

        injector = Guice.createInjector(modules);
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    private void setUpConfiguration() {
        this.saveDefaultConfig();
    }

    private Module setUpDatabaseUtilitiesModule() {

        this.getLogger().log(Level.INFO, "Configuring database connection ...");

        FileConfiguration fileConfiguration = this.getConfig();
        IDatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        databaseConfiguration.setHost(fileConfiguration.getString("database.host"));
        databaseConfiguration.setPort(fileConfiguration.getInt("database.port"));
        databaseConfiguration.setDatabaseName(fileConfiguration.getString("database.databaseName"));
        databaseConfiguration.setUsername(fileConfiguration.getString("database.username"));
        databaseConfiguration.setPassword(fileConfiguration.getString("database.password"));
        databaseConfiguration.setDatabaseType(this.getDatabaseTypeFromConfig(fileConfiguration));

        this.getLogger().log(Level.INFO, "Using database \"" + databaseConfiguration.getDatabaseName() + "\" on \"" + databaseConfiguration.getHost() + ":" + databaseConfiguration.getPort() + "\".");

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

        this.getLogger().log(Level.INFO, "Trying to set up dependency injection module for \"DatabaseUtilities\" ...");

        Module databaseUtilitiesModule = new DatabaseUtilitiesModule(databaseConfiguration);

        this.getLogger().log(Level.INFO, "Successfully set dependency injection module for \"DatabaseUtilities\".");

        return databaseUtilitiesModule;
    }

    private DatabaseType getDatabaseTypeFromConfig(MemorySection config) {

        String databaseType = config.getString("database.databaseType");

        switch (databaseType) {

            default:
            case "mysql":
                return DatabaseType.MY_SQL;

            case "postgresql":
                return DatabaseType.POSTGRE_SQL;

            case "mariadb":
                return DatabaseType.MARIA_DB;
        }
    }
}
