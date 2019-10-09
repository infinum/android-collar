package co.infinum.collar.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Should be used with {@link ConvertAttributes}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertAttribute {

    String value();

    String defaultValue() default "";

    /**
     * Keep this event in entire app scope
     */
    boolean isSuper() default false;
}
