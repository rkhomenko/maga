package org.khomenko.maga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.io.ServerMain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class MethodEntry {
    Method method;
    boolean withArgument;
}

public class HttpRouteHandlers {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    private Class<?> aClass;
    private Map<HttpMethod, Collection<MethodEntry>> methodHandlers;

    private HttpRouteHandlers() {
        methodHandlers = new HashMap<>();
    }

    public class HttpMethodHandler {
        private Object object;
        private Method method;

        private HttpMethodHandler() {}

        public Object getObject() {
            return object;
        }

        public Method getMethod() {
            return method;
        }
    }

    public String getClassName() {
        return aClass.getName();
    }

    public HttpMethodHandler getHandler(HttpMethod type, boolean withArgument) {
        Collection<MethodEntry> methodEntryCollection = methodHandlers.get(type);

        if (methodEntryCollection == null) {
            return null;
        }

        for (var entry : methodEntryCollection) {
            if (entry.withArgument == withArgument) {
                HttpMethodHandler httpMethodHandler = new HttpMethodHandler();
                httpMethodHandler.method = entry.method;
                try {
                    httpMethodHandler.object = aClass.getDeclaredConstructor().newInstance();
                }
                catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return httpMethodHandler;
            }
        }

        return null;
    }

    public static HttpRouteHandlers fromClass(Class<?> aClass) {
        HttpRouteHandlers routeHandlers = new HttpRouteHandlers();
        routeHandlers.aClass = aClass;

        logger.debug("Find method handlers in class " + aClass.getName());
        for (Method method : aClass.getDeclaredMethods()) {
            RouteHandler routeHandler = method.getAnnotation(RouteHandler.class);
            if (routeHandler == null) {
                continue;
            }

            MethodEntry methodEntry = new MethodEntry();
            methodEntry.method = method;
            methodEntry.withArgument = routeHandler.withArgument();

            Collection<MethodEntry> methodEntryCollection =
                    routeHandlers.methodHandlers.get(routeHandler.type());
            if (methodEntryCollection == null) {
                methodEntryCollection = new ArrayList<>();
                methodEntryCollection.add(methodEntry);
                routeHandlers.methodHandlers.put(routeHandler.type(), methodEntryCollection);
            } else {
                methodEntryCollection.add(methodEntry);
            }

            logger.debug("Find method " + routeHandler.type() + " withArgument=" + routeHandler.withArgument());
        }

        return routeHandlers;
    }
}
