package org.bredkowiak.mongorest.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryValidator.class)
@Documented
public @interface ValidCategory {

    //TODO finish or delete

    String message() default "Category is incomplete or contains wrong mainCategory/subCategories combination";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
