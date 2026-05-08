package org.contactmgmt.service;

import org.contactmgmt.dto.ContactDto;
import org.contactmgmt.entity.AppUser;
import org.contactmgmt.entity.Contact;
import org.contactmgmt.repository.AppUserRepository;
import org.contactmgmt.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    void getContacts_shouldUseSearchQuery_whenProvided() {
        AppUser user = new AppUser();
        user.setId(1L);

        Contact contact = new Contact();
        contact.setId(11L);
        contact.setUser(user);
        contact.setTitle("Mr.");
        contact.setFirstName("Ali");
        contact.setLastName("Khan");

        Page<Contact> page = new PageImpl<>(List.of(contact));
        PageRequest pageable = PageRequest.of(0, 10);

        when(contactRepository.searchContacts(1L, "Ali", pageable)).thenReturn(page);

        Page<ContactDto> result = contactService.getContacts(1L, "Ali", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Ali", result.getContent().get(0).getFirstName());
        verify(contactRepository).searchContacts(1L, "Ali", pageable);
    }

    @Test
    void getContactById_shouldThrow_whenUserDoesNotOwnContact() {
        AppUser owner = new AppUser();
        owner.setId(1L);

        Contact contact = new Contact();
        contact.setId(15L);
        contact.setUser(owner);

        when(contactRepository.findById(15L)).thenReturn(Optional.of(contact));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> contactService.getContactById(15L, 2L));

        assertEquals("Unauthorized to view this contact", ex.getMessage());
    }
}
