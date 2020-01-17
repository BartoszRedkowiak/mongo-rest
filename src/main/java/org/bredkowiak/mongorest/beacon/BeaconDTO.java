package org.bredkowiak.mongorest.beacon;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.bredkowiak.mongorest.category.MainCategory;

@ApiModel(description = "An object responsible for managing event cycles in specified area")
@Getter
@Setter
public class BeaconDTO {

    private String id;
    private Double latitude;
    private Double longitude;
    private MainCategory mainCategory;
    private Integer radius;
    private Integer interval; // Event re-roll interval (in days) in area;
//    private boolean active;

    public BeaconDTO() {
    }

    public BeaconDTO(Beacon beacon) {
        this.id = beacon.getId();
        this.latitude = beacon.getLatitude();
        this.longitude = beacon.getLongitude();
        this.mainCategory = beacon.getMainCategory();
        this.radius = beacon.getRadius();
        this.interval = beacon.getInterval();
    }



}
