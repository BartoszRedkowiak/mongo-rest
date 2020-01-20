package org.bredkowiak.mongorest.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MainCategoryValidator.class)
@Documented
public @interface MainCat {

    //TODO finish or delete

    String message() default "Provided main category is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
