package org.contactmgmt.dto;

import jakarta.validation.constraints.NotBlank;

public class ContactEmailDto {
    private Long id;

    @NotBlank
    private String emailAddress;

    @NotBlank
    private String label;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
