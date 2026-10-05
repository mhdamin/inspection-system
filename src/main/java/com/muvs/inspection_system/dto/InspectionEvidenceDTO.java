package com.muvs.inspection_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
import java.util.Map;

/** Atomic inspection snapshot with bounded JPEG/PNG photographs. */
@Data
public class InspectionEvidenceDTO {
    @Min(0) private long odometer;
    @NotBlank @Pattern(regexp = "Full|3/4|1/2|1/4|Empty") private String fuelLevel;
    @NotNull @Size(max = 20) private List<@Valid Point> exterior;
    @NotNull @Size(max = 6) private Map<String, @NotNull @Size(max = 2000) String> interior;
    @NotNull @Size(max = 4) private Map<String, @NotNull @Size(max = 50) String> tyres;
    @NotNull @Size(max = 12) private List<@Valid Photo> photos;
    @Size(max = 200) private String acknowledgedBy;
    private boolean acknowledged;

    @Data
    public static class Point {
        @Min(101) @Max(120) private int id;
        @NotBlank @Pattern(regexp = "Normal|Abnormal|N/A|Not Inspected") private String status;
        @Size(max = 2000) private String notes;
    }

    @Data
    public static class Photo {
        @NotBlank @Size(max = 100) private String label;
        @NotBlank @Size(max = 250000)
        @Pattern(regexp = "data:image/(jpeg|png);base64,[A-Za-z0-9+/=]+")
        private String data;
    }
}
