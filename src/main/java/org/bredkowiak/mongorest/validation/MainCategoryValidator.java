package org.bredkowiak.mongorest.validation;

import org.bredkowiak.mongorest.category.MainCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;

public class MainCategoryValidator implements ConstraintValidator<MainCat, String> {

    private EnumSet mainCategories = EnumSet.of(MainCategory.LONGBOARD,
            MainCategory.SKATEBOARD);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return mainCategories.contains(MainCategory.valueOf(value));
    }
}
