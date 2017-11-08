package com.reynke.sloud.core.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Nicklas Reincke (contact@reynke.com)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    /**
     * @return The type of the controller related to the entity annotated with this annotation.
     */
    Class<? extends IController> type();
}
