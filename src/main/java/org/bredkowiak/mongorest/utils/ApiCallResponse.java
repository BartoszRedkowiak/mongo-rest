package org.bredkowiak.mongorest.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCallResponse {

    private boolean success;
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String objectId;
    private String message;

    public ApiCallResponse(boolean success, String objectId, String message) {
        this.success = success;
        this.timestamp = LocalDateTime.now();
        this.objectId = objectId;
        this.message = message;
    }

    public ApiCallResponse(boolean success, String message) {
        this.success = success;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public ApiCallResponse(ValidationResult validationResult) {
        this.success = validationResult.isPassed();
        this.timestamp = LocalDateTime.now();
        this.message = validationResult.presentCauses();
    }





}
