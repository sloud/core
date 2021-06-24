package com.reynke.sloud.core.dependencyinjection;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.reynke.sloud.core.ICorePlugin;
import com.reynke.sloud.core.IModuleAware;
import com.reynke.sloud.core.controller.ControllerFactory;
import com.reynke.sloud.core.controller.IControllerFactory;
import com.reynke.sloud.databaseutilities.configuration.DatabaseConfiguration;
import com.reynke.sloud.databaseutilities.configuration.DatabaseType;
import com.reynke.sloud.databaseutilities.configuration.Hbm2ddlOption;
import com.reynke.sloud.databaseutilities.configuration.IDatabaseConfiguration;
import com.reynke.sloud.databaseutilities.database.IDatabaseEntitiesAware;
import com.reynke.sloud.databaseutilities.dependencyinjection.DatabaseUtilitiesModule;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        install(this.setUpDatabaseUtilitiesModule());

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

    private Module setUpDatabaseUtilitiesModule() {
        corePlugin.getLogger().log(Level.INFO, "Configuring database connection ...");

        FileConfiguration fileConfiguration = corePlugin.getConfig();
        IDatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        databaseConfiguration.setHost(fileConfiguration.getString("database.host"));
        databaseConfiguration.setPort(fileConfiguration.getInt("database.port"));
        databaseConfiguration.setDatabaseName(fileConfiguration.getString("database.databaseName"));
        databaseConfiguration.setUsername(fileConfiguration.getString("database.username"));
        databaseConfiguration.setPassword(fileConfiguration.getString("database.password"));
        databaseConfiguration.setDatabaseType(this.getDatabaseTypeFromConfig(fileConfiguration));
        databaseConfiguration.setHbm2ddlOption(this.getHbm2ddlOptionFromConfig(fileConfiguration));

        this.secureHbm2ddlOptionInConfig(fileConfiguration);

        corePlugin.getLogger().log(Level.INFO, "Using database \"" + databaseConfiguration.getDatabaseName() + "\" on \"" + databaseConfiguration.getHost() + ":" + databaseConfiguration.getPort() + "\".");

        databaseConfiguration.setPackages(new ArrayList<>());
        databaseConfiguration.setAnnotatedClasses(new ArrayList<>());

        Plugin[] plugins = corePlugin.getServer().getPluginManager().getPlugins();

        corePlugin.getLogger().log(Level.INFO, "Loading annotated classes and packages containing them from plugins implementing \"" + IDatabaseEntitiesAware.class.getName() + "\" ...");

        for (Plugin plugin : plugins) {
            // Don't import packages and annotated classes held by this plugin
            if (plugin.getName().equals(corePlugin.getName())) {
                continue;
            }

            // Make sure the plugin is a database entities aware plugin
            if (!(plugin instanceof IDatabaseEntitiesAware databaseEntitiesAwarePlugin)) {
                continue;
            }

            corePlugin.getLogger().log(Level.INFO, "Loading annotated classes and packages from \"" + plugin.getName() + "\" ...");

            databaseConfiguration.getPackages().addAll(databaseEntitiesAwarePlugin.getPackages());
            databaseConfiguration.getAnnotatedClasses().addAll(databaseEntitiesAwarePlugin.getAnnotatedClasses());

            corePlugin.getLogger().log(Level.INFO, "Successfully loaded annotated classes and packages from \"" + plugin.getName() + "\" ...");
        }

        corePlugin.getLogger().log(Level.INFO, "Trying to set up dependency injection module for \"DatabaseUtilities\" ...");

        Module databaseUtilitiesModule = new DatabaseUtilitiesModule(databaseConfiguration);

        corePlugin.getLogger().log(Level.INFO, "Successfully set dependency injection module for \"DatabaseUtilities\".");

        return databaseUtilitiesModule;
    }

    /**
     * Secures the hbm2ddl option and sets it to "validate" to not
     * accidentally recreate, drop or delete database tables.
     *
     * @param config The configuration file.
     */
    private void secureHbm2ddlOptionInConfig(FileConfiguration config) {
        config.set("database.hbm2ddl", Hbm2ddlOption.VALIDATE.getValue());

        try {
            config.save(new File(corePlugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Hbm2ddlOption getHbm2ddlOptionFromConfig(FileConfiguration config) {
        String hbm2ddl = config.getString("database.hbm2ddl");

        if (hbm2ddl == null) {
            return Hbm2ddlOption.VALIDATE;
        }

        return switch (hbm2ddl) {
            case "validate" -> Hbm2ddlOption.VALIDATE;
            case "update" -> Hbm2ddlOption.UPDATE;
            case "create" -> Hbm2ddlOption.CREATE;
            case "create-drop" -> Hbm2ddlOption.CREATE_DROP;
            default -> throw new IllegalStateException("Unexpected hbm2ddl option: " + hbm2ddl);
        };
    }

    private DatabaseType getDatabaseTypeFromConfig(MemorySection config) {
        String databaseType = config.getString("database.databaseType");

        if (databaseType == null) {
            throw new IllegalStateException("Database type not set");
        }

        return switch (databaseType) {
            case "mysql5" -> DatabaseType.MY_SQL_5;
            case "mysql8" -> DatabaseType.MY_SQL_8;
            case "mariadb" -> DatabaseType.MARIA_DB;
            case "postgresql9" -> DatabaseType.POSTGRE_SQL_9;
            case "postgresql10" -> DatabaseType.POSTGRE_SQL_10;
            default -> throw new IllegalStateException("Unexpected database type: " + databaseType);
        };
    }
}
