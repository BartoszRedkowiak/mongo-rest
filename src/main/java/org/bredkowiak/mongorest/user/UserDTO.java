package org.bredkowiak.mongorest.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.security.Role;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@ApiModel(description = "Object containing user data")
public class UserDTO {

    @Null(groups = ValidationCreate.class, message = "{validation.id.null}")
    @Size(min = 24, max = 24, groups = ValidationUpdate.class, message = "{validation.id.size}")
    @ApiModelProperty(notes = "Property of new object has to be null")
    private String id;

    @Size(min = 5, max = 20, groups = {ValidationCreate.class , ValidationUpdate.class}, message = "{validation.username.size}") //FIXME switch to regex later
    @ApiModelProperty(allowableValues = "range[5,20]", required = true, notes = "") //TODO notes
    private String username;

    @ApiModelProperty(notes = "") //TODO notes
    @Size(min = 5, max = 20, groups = {ValidationCreate.class , ValidationUpdate.class}, message = "{validation.password.size}") //FIXME switch to regex later
    private String password;

    @ApiModelProperty(required = true) //TODO notes
//    @ValidEnum(regexp = "LONGBOARD|SKATEBOARD", groups = {ValidationCreate.class, ValidationUpdate.class}) //FIXME doesn't work on EnumSet
    private Set<MainCategory> categories; //enumSet causes modelMapper failure

    @Email //TODO swtich to regex
    private String email;

    //FIXME users can't have access to these fields
    private Integer enabled;
    private Set<Role> roles;

}
