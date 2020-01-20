package org.bredkowiak.mongorest.beacon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.validation.ValidEnum;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@ApiModel(description = "An object responsible for managing event cycles in specified area")
@Getter
@Setter
public class BeaconDTO {

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

    @ValidEnum(regexp = "LONGBOARD|SKATEBOARD", groups = {ValidationCreate.class, ValidationUpdate.class})
    @ApiModelProperty(required = true)
    private MainCategory mainCategory;

    @Range(min = 5, max = 50, message = "{validation.radius.range}",
            groups = {ValidationCreate.class, ValidationUpdate.class})
    @ApiModelProperty(allowableValues = "range[5, 50]", required = true)
    private Integer radius;

    @Range(min = 3, max = 14, message = "{validation.eventDuration.range}",
            groups = {ValidationCreate.class, ValidationUpdate.class})
    @ApiModelProperty(allowableValues = "range[3, 14]", required = true)
    private Integer eventDuration; // Event re-roll interval (in days) in area;
//    private boolean active;


}
