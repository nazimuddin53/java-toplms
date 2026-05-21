package com.toplms.web.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Form-backing object for creating a course. */
public class CourseForm {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 160, message = "Title must be 2–160 characters")
    private String title;

    @Size(max = 2000, message = "Description is too long")
    private String description;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
