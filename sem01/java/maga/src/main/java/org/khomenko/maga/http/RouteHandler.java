package org.khomenko.maga.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RouteHandler {
    HttpMethod type();
    boolean withArgument() default false;
}
