package org.bredkowiak.mongorest.user;

import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.security.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@Getter
@Setter
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private int enabled;
    private Set<Role> roles;
    private Set<MainCategory> categories; //enumSet causes modelMapper failure

}
