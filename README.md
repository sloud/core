# Sloud Core Plugin

Core plugin for Sloud components.

## Features

* Dependency injection container
* Automatic discovery of other plugins that consist of a dependency injection module
  * This is done via finding plugins that implement the interface `IModuleAware`
* Database layer
  * Automatically discovers plugins implementing the interface `IDatabaseEntitiesAware`
    to load packages and classes containing annotated entities that should be relevant for the database schema 

## Database configuration

> A file under `plugins/Core/config.yml` will automatically be generated the first time the plugin runs.

```yaml
# `config.yml` from the `Core` plugin folder
database:
  hbm2ddl: validate
  host: 127.0.0.1
  port: 3306
  databaseName: ""
  username: root
  password: ""
  databaseType: mysql5
```

Available database types:

* `mysql5`, `mysql8`
* `mariadb`
* `postgresql9`, `postgresql10`

Regarding the `hbm2ddl` option (Hibernate schema generation strategy):

* `validate` - Validate the schema, makes no changes to the database.
* `update` - Update the schema.
* `create` - Creates the schema, destroying previous data.
* `create-drop` - Creates the schema, destroying previous data and drop the schema when the SessionFactory is closed explicitly, typically when the application stops. This option might be especially useful for testing purposes.

> Every time you choose something different from `validate`, the plugin does
> as described, automatically reverts to `validate` and saves the `config.yml`.
> 
> This happens to ensure that nothing unexpected happens to your database.

## Install Java 16

Install Homebrew and run the following:

```shell
brew tap AdoptOpenJDK/openjdk
brew install --cask adoptopenjdk16-jre
```

## Changelog

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
