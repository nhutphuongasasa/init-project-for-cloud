package com.cloud.vendor_service.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 100, message = "Slug must be at most 100 characters long")
    private String slug;

    @JsonProperty("logo_url")
    @NotBlank(message = "Logo URL is required")
    @Size(max = 500, message = "Logo URL must be at most 500 characters long")
    private String logoUrl;

    @Size(max = 500, message = "Description must be at most 500 characters long")
    private String description;
}
