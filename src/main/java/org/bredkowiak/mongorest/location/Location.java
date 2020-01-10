package org.bredkowiak.mongorest.location;

import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.Category;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Document(collection = "Locations")
@Getter
@Setter
public class Location {

    @Id
    private String id;
    private Double latitude;
    private Double longitude;
    private String name;
    private Category categories;

}
