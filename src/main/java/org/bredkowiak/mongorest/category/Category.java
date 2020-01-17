package org.bredkowiak.mongorest.category;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumSet;

@Getter
@Setter
@ApiModel(description = "An object containing available categories for locations and beacons")
public class Category {

    private MainCategory mainCategory;
    private EnumSet<SubCategory> subCategories;

}
