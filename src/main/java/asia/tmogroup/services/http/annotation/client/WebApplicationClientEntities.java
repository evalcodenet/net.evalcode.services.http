package net.evalcode.services.http.annotation.client;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * WebApplicationClientEntities
 *
 * Manually reference entities to include in
 * generated web application client.
 *
 * @author carsten.schipke@gmail.com
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebApplicationClientEntities
{
  // PROPERTIES
  Class<?>[] value();
}
