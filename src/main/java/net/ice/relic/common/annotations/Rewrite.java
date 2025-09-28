package net.ice.relic.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.SOURCE)
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE, ANNOTATION_TYPE})
public @interface Rewrite {

    String since() default "";

    boolean forRemoval() default false;

    String reason() default "";
}
