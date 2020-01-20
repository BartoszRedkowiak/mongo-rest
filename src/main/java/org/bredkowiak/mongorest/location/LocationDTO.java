package org.bredkowiak.mongorest.location;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.Category;
import org.bredkowiak.mongorest.validation.ValidCategory;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel(description = "An object containing single location data")
public class LocationDTO {

    @Null(groups = ValidationCreate.class, message = "{validation.id.null}")
    @Size(min = 24, max = 24, groups = ValidationUpdate.class, message = "{validation.id.size}")
    @ApiModelProperty(notes = "Property of new object has to be null")
    private String id;

    @Range(min = -85, max = 85, message = "{validation.latitude.range}",
            groups = {ValidationCreate.class, ValidationUpdate.class})
    @ApiModelProperty(allowableValues = "range[-85,85]", required = true)
    private Double latitude;

    @Range(min = -180, max = 180, message = "{validation.longitude.range}",
            groups = {ValidationCreate.class, ValidationUpdate.class})
    @ApiModelProperty(allowableValues = "range[-180, 180]", required = true)
    private Double longitude;

    @Size(min = 5, max = 30, message = "{validation.name.length}",
            groups = {ValidationCreate.class, ValidationUpdate.class})
    private String name;

    @ValidCategory(groups = {ValidationCreate.class, ValidationUpdate.class})
    private Category categories;

//    @Null(groups = ValidationCreate.class, message = "{validation.activeEvent.assertFalse}")
    @ApiModelProperty(notes = "Property of new object has to be null")
    private boolean activeEvent = false;

}
