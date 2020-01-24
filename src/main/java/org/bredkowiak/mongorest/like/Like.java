package org.bredkowiak.mongorest.like;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Like {

    @NotNull
    private String userId;

    public Like(@NotNull String userId) {
        this.userId = userId;
    }
}
