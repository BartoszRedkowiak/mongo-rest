package org.bredkowiak.mongorest.beacon;

import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Beacon {

    private String id;
    private Double latitude;
    private Double longitude;
    private MainCategory mainCategory;
    private int radius;
    private int interval; // Event re-roll interval (in days) in area;
    private boolean active;
    private String jobName; //TODO create DTO class to hide this data

}
