package org.contactmgmt.dto;

import jakarta.validation.constraints.NotBlank;

public class ContactPhoneDto {
    private Long id;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String label;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
