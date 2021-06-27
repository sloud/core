package com.reynke.sloud.core.database;

import com.reynke.sloud.databaseutilities.configuration.DatabaseConfiguration;
import com.reynke.sloud.databaseutilities.configuration.DatabaseType;
import com.reynke.sloud.databaseutilities.configuration.Hbm2ddlOption;
import com.reynke.sloud.databaseutilities.configuration.IDatabaseConfiguration;
import com.reynke.sloud.databaseutilities.dependencyinjection.DatabaseUtilitiesModule;
import org.bukkit.configuration.ConfigurationSection;

public class DatabaseModuleBuilder {
    public DatabaseUtilitiesModule buildFromConfigSection(ConfigurationSection config) {
        return buildFromConfigSection(config, new DatabaseModuleBuilderOptions());
    }

    public DatabaseUtilitiesModule buildFromConfigSection(ConfigurationSection config, DatabaseModuleBuilderOptions options) {
        IDatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        databaseConfiguration.setHost(config.getString("host"));
        databaseConfiguration.setPort(config.getInt("port"));
        databaseConfiguration.setDatabaseName(config.getString("databaseName"));
        databaseConfiguration.setUsername(config.getString("username"));
        databaseConfiguration.setPassword(config.getString("password"));
        databaseConfiguration.setDatabaseType(this.getDatabaseTypeFromConfig(config));
        databaseConfiguration.setHbm2ddlOption(this.getHbm2ddlOptionFromConfig(config, options.getDefaultHbm2ddlOption()));

        databaseConfiguration.setPackages(options.getPackages());
        databaseConfiguration.setAnnotatedClasses(options.getAnnotatedClasses());

        return new DatabaseUtilitiesModule(databaseConfiguration);
    }

    private Hbm2ddlOption getHbm2ddlOptionFromConfig(ConfigurationSection config, Hbm2ddlOption defaultOption) {
        String hbm2ddl = config.getString("hbm2ddl");

        if (hbm2ddl == null) {
            return defaultOption;
        }

        return switch (hbm2ddl) {
            case "validate" -> Hbm2ddlOption.VALIDATE;
            case "update" -> Hbm2ddlOption.UPDATE;
            case "create" -> Hbm2ddlOption.CREATE;
            case "create-drop" -> Hbm2ddlOption.CREATE_DROP;
            default -> throw new IllegalStateException("Unexpected hbm2ddl option: " + hbm2ddl);
        };
    }

    private DatabaseType getDatabaseTypeFromConfig(ConfigurationSection config) {
        String databaseType = config.getString("databaseType");

        if (databaseType == null) {
            throw new IllegalStateException("Key `databaseType` not set");
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
