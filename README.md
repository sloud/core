# Sloud Core Plugin

Core plugin for Sloud components.

## Features

* Dependency injection module
* Automatic discovery of other plugins that consist of a dependency injection module
  * This is done via finding plugins that implement the interface `IModuleAware`
  * This allows you to use and inject the following classes into your code
    * `ICorePlugin`: This is this plugin
    * `IControllerFactory`: This helps you find or create an appropriate controller for a class that is annotated with `@Controller`
* Database utilities to create a dependency injection module that can be used to connect
  to a database, validate, create or update the underlying schema and read and write from and to the database.

## Creating a database connection

Each plugin should have its own database connection. To establish this you may use the following
example to be able to inject a database via dependency injection into your own codebase:

```java
package com.reynke.sloud.yourplugin.dependencyinjection;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class YourPluginModule extends AbstractModule {
  private final IYourPlugin yourPlugin;

  public YourPluginModule(IYourPlugin yourPlugin) {
    this.yourPlugin = yourPlugin;
  }

  @Override
  protected void configure() {
    // Get your plugin's configuration file
    FileConfiguration fileConfiguration = this.yourPlugin.getConfig();

    // Get the configuration section with the key `database`.
    // The path to the configuration section may differ in your case, but you have to ensure that underlying keys are correct.
    ConfigurationSection configSection = fileConfiguration.getConfigurationSection("database");

    if (configSection == null) {
      throw new DatabaseUtilitiesException("Configuration section \"database\" not found.");
    }

    DatabaseUtilitiesModule module = new DatabaseModuleBuilder().buildFromConfigSection(
            configSection,
            // The second parameter is optional but allows for more advanced configuration options.
            // A very important option is to set or change the `hbm2ddl` option.
            new DatabaseModuleBuilderOptions()
    );

    // Use Guice's `install` method to install the database utilities module into THIS module.
    // This allows you to inject `IDatabase`, and more classes, into your classes.
    install(module);
  }
}
```

Example configuration file for the plugin module above:

```yaml
# `config.yml` from your plugin folder.
# The key `database` may differ, but you have to ensure that underlying keys are correct.
database:
  hbm2ddl: "validate" # This is optional and defaults to `validate` or whatever your custom `DatabaseModuleBuilderOptions` have got defined
  host: "127.0.0.1"
  port: 3306
  databaseName: ""
  username: "root"
  password: ""
  databaseType: "mysql5"
```

Available database types:

* `mysql5`, `mysql8`
* `mariadb`
* `postgresql9`, `postgresql10`

Regarding the `hbm2ddl` option (Hibernate schema generation strategy):

* `validate` - Validates the schema, makes no changes to the database.
* `update` - Updates the schema.
* `create` - Creates the schema, destroying previous data.
* `create-drop` - Creates the schema, destroying previous data and drop the schema when the SessionFactory is closed explicitly, typically when the application stops. This option might be especially useful for testing purposes.

## Install Java 16

Install Homebrew and run the following:

```shell
brew tap AdoptOpenJDK/openjdk
brew install --cask adoptopenjdk16-jre
```

## Changelog

**1.1.0**

* Detach database usage from Core plugin
  * Each plugin itself should decide whether it needs a database
  * Database utilities still exist
    * A database module for dependency injection can still be created (documented under #creating-a-database-connection)
* Better documentation

**1.0.3**

* Update dependency for database utilities
* New database types

**1.0.2**

* Code cleanup
* Update Gradle
* Set Java version compatibility to `16`
* Use new database utilities dep
* Update deps
* Use Paper instead of Spigot

**1.0.1**

- Outsourced code, to load database utilities and other modules,
into core module to install modules instead of loading them in

**1.0.0**

- Initial release
