package org.bredkowiak.mongorest.validation;

import org.bredkowiak.mongorest.category.Category;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.EnumSet;

public class CategoryValidator implements ConstraintValidator<ValidCategory, Category> {

    private EnumSet longboardCategories = EnumSet.of(SubCategory.DANCING,
            SubCategory.FREESTYLE,
            SubCategory.DOWNHILL,
            SubCategory.CRUISING);

    private EnumSet skateboardCategories = EnumSet.of(SubCategory.PARK,
            SubCategory.RAMP,
            SubCategory.BOWL,
            SubCategory.BANK,
            SubCategory.KICKER,
            SubCategory.RAIL,
            SubCategory.LEDGE,
            SubCategory.GAP,
            SubCategory.STAIRS);


    @Override
    public boolean isValid(Category category, ConstraintValidatorContext constraintValidatorContext) {
        MainCategory mainCategory = category.getMainCategory();
        EnumSet<SubCategory> subCategories = category.getSubCategories();

        if (mainCategory == null || subCategories == null || subCategories.isEmpty()) return false;

        if (mainCategory.equals(MainCategory.LONGBOARD)) {
            if (!Collections.disjoint(subCategories, skateboardCategories)) return false;
        } else {
            if (!Collections.disjoint(subCategories, longboardCategories)) return false;
        }
        return true;
    }
}
