package net.evalcode.services.http.annotation.client;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * WebApplicationClientType
 *
 * Overwrites the class name of decorated type in
 * generated web application client classes.
 *
 * Optionally defines type conversion for unmarshalling
 * in web application client.
 *
 * @author carsten.schipke@gmail.com
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebApplicationClientType
{
  // PROPERTIES
  String value() default "";
  Type type() default Type.STRING;
  String[] properties() default {};


  /**
   * Type
   *
   * @author carsten.schipke@gmail.com
   */
  public static enum Type
  {
    // PREDEFINED TYPES
    BOOL("Components\\Boolean"),
    INT("Components\\Integer"),
    FLOAT("Components\\Float"),
    STRING("Components\\String"),
    DATE("Components\\Date"),
    URI("Components\\Uri");


    // PROPERTIES
    public final String value;


    // CONSTRUCTION
    Type(final String value)
    {
      this.value=value;
    }
  }
}
