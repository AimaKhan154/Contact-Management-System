package org.contactmgmt.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ContactDto {
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String title;

    private List<ContactEmailDto> emails;
    private List<ContactPhoneDto> phones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<ContactEmailDto> getEmails() { return emails; }
    public void setEmails(List<ContactEmailDto> emails) { this.emails = emails; }

    public List<ContactPhoneDto> getPhones() { return phones; }
    public void setPhones(List<ContactPhoneDto> phones) { this.phones = phones; }
}
