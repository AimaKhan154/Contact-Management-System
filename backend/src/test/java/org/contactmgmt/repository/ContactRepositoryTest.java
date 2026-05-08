package org.contactmgmt.repository;

import org.contactmgmt.entity.AppUser;
import org.contactmgmt.entity.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void searchContacts_shouldReturnMatchingNamesForSpecificUser() {
        AppUser user = new AppUser("repo@example.com", null, "hashed");
        user = appUserRepository.save(user);

        Contact ali = new Contact();
        ali.setUser(user);
        ali.setTitle("Mr.");
        ali.setFirstName("Ali");
        ali.setLastName("Khan");
        contactRepository.save(ali);

        Contact sara = new Contact();
        sara.setUser(user);
        sara.setTitle("Ms.");
        sara.setFirstName("Sara");
        sara.setLastName("Ahmad");
        contactRepository.save(sara);

        Page<Contact> results = contactRepository.searchContacts(user.getId(), "ali", PageRequest.of(0, 10));

        assertEquals(1, results.getTotalElements());
        assertEquals("Ali", results.getContent().get(0).getFirstName());
    }
}
