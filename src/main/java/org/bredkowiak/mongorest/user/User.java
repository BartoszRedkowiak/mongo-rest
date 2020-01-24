package org.bredkowiak.mongorest.user;

import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Document
@Getter
@Setter
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String authToken;
    private LocalDateTime tokenExpirationDate;
    private EnumSet<MainCategory> categories;

}
