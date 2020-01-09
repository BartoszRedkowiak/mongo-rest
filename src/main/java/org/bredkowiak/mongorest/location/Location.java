package org.bredkowiak.mongorest.location;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Locations")
@Getter
@Setter
public class Location {

    @Id
    private String id;
    private Double latitude;
    private Double longitude;
    private String name;

}
