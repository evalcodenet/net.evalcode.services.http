package net.evalcode.services.http.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.google.inject.BindingAnnotation;


/**
 * Roles
 *
 * @author carsten.schipke@gmail.com
 */
@Documented
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Roles
{

}
