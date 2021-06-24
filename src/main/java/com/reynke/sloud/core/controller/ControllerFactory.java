package com.reynke.sloud.core.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.reynke.sloud.core.exception.CoreException;
import com.reynke.sloud.databaseutilities.entity.IEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and caches controllers implementing {@link IController}.
 *
 * @author Nicklas Reincke (contact@reynke.com)
 */
@Singleton
public class ControllerFactory implements IControllerFactory {
    private final Injector injector;
    private static final Map<String, IController> controllerCache;

    static {
        controllerCache = new HashMap<>();
    }

    @Inject
    public ControllerFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public IController getController(Class<? extends IEntity<?>> entityType) throws CoreException {
        // Load controller from cache if it was found in cache
        if (controllerCache.containsKey(entityType.getName())) {
            return controllerCache.get(entityType.getName());
        }

        // Get the @Controller annotation from the entity class.
        Controller controllerAnnotation = entityType.getAnnotation(Controller.class);

        // Check if it is even there ...
        if (controllerAnnotation == null) {
            throw new CoreException("The desired entity type is not annotated with a controller annotation.");
        }

        // Get the controller type related to the entity to tell the injector where
        // to inject dependencies and finally return the created controller.
        IController controller = injector.getInstance(controllerAnnotation.type());

        // Add controller to cache.
        controllerCache.put(entityType.getName(), controller);

        return controller;
    }
}
