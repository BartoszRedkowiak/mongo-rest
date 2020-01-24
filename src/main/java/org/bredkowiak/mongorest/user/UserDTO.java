package org.bredkowiak.mongorest.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Getter
@Setter
@ApiModel(description = "Object containing user data")
public class UserDTO {

    @ApiModelProperty //TODO
    private String username;

    @ApiModelProperty //TODO
    private String password;

    @ApiModelProperty //TODO
    private EnumSet<MainCategory> categories;

    private String authToken;

    private LocalDateTime tokenExpirationDate;

}
