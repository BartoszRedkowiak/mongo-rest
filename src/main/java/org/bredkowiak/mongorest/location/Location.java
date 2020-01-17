package org.bredkowiak.mongorest.location;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Locations")
@Getter
@Setter
@ApiModel(description = "An object containing single location data")
public class Location {

    @Id
    private String id;
    private Double latitude;
    private Double longitude;
    private String name;
    private Category categories;
    private boolean activeEvent = false;

}
