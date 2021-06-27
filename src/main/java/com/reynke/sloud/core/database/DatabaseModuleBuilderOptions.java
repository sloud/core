package com.reynke.sloud.core.database;

import com.reynke.sloud.databaseutilities.configuration.Hbm2ddlOption;
import com.reynke.sloud.databaseutilities.entity.IEntity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseModuleBuilderOptions {
    private List<String> packages = new ArrayList<>();
    private List<Class<? extends IEntity<?>>> annotatedClasses = new ArrayList<>();
    private Hbm2ddlOption defaultHbm2ddlOption = Hbm2ddlOption.VALIDATE;

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public List<Class<? extends IEntity<?>>> getAnnotatedClasses() {
        return annotatedClasses;
    }

    public void setAnnotatedClasses(List<Class<? extends IEntity<?>>> annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public Hbm2ddlOption getDefaultHbm2ddlOption() {
        return defaultHbm2ddlOption;
    }

    public void setDefaultHbm2ddlOption(Hbm2ddlOption defaultHbm2ddlOption) {
        this.defaultHbm2ddlOption = defaultHbm2ddlOption;
    }
}
