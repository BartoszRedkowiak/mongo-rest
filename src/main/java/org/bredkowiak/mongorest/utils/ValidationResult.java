package org.bredkowiak.mongorest.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationResult {

    private boolean passed = true;
    private List<String> causes = new ArrayList<>();

    public ValidationResult() {
    }

    public ValidationResult(boolean passed) {
        this.passed = passed;
    }

    public ValidationResult(boolean passed, String cause) {
        this.passed = passed;
        this.causes.add(cause);
    }

    public void addCause(String cause){
        this.setPassed(false);
        this.causes.add(cause);
    }

    public String presentCauses() {
        return "Validation tests failed. Causes: " + this.causes.toString();
    }
}
