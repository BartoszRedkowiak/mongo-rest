package org.bredkowiak.mongorest.category;

import lombok.Getter;
import lombok.Setter;

import java.util.EnumSet;

@Getter
@Setter
public class Category {

    private MainCategory mainCategory;
    private EnumSet<SubCategory> subCategories;

}
