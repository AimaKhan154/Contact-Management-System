package org.contactmgmt.service;

import org.contactmgmt.dto.ContactDto;
import org.contactmgmt.dto.ContactEmailDto;
import org.contactmgmt.dto.ContactPhoneDto;
import org.contactmgmt.entity.AppUser;
import org.contactmgmt.entity.Contact;
import org.contactmgmt.entity.ContactEmail;
import org.contactmgmt.entity.ContactPhone;
import org.contactmgmt.repository.AppUserRepository;
import org.contactmgmt.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AppUserRepository userRepository;

    public Page<ContactDto> getContacts(Long userId, String searchQuery, Pageable pageable) {
        Page<Contact> contacts;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            contacts = contactRepository.searchContacts(userId, searchQuery.trim(), pageable);
        } else {
            contacts = contactRepository.findByUserId(userId, pageable);
        }
        return contacts.map(this::mapToDto);
    }

    public ContactDto getContactById(Long contactId, Long userId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to view this contact");
        }
        return mapToDto(contact);
    }

    @Transactional
    public ContactDto createContact(ContactDto contactDto, Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = new Contact();
        contact.setUser(user);
        updateContactFields(contact, contactDto);
        
        Contact savedContact = contactRepository.save(contact);
        logger.info("Created contact {} for user {}", savedContact.getId(), userId);
        return mapToDto(savedContact);
    }

    @Transactional
    public ContactDto updateContact(Long contactId, ContactDto contactDto, Long userId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this contact");
        }

        updateContactFields(contact, contactDto);
        Contact updatedContact = contactRepository.save(contact);
        logger.info("Updated contact {} for user {}", updatedContact.getId(), userId);
        return mapToDto(updatedContact);
    }

    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this contact");
        }

        contactRepository.delete(contact);
        logger.info("Deleted contact {} for user {}", contactId, userId);
    }

    private void updateContactFields(Contact contact, ContactDto dto) {
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setTitle(dto.getTitle());

        // Keep persistent collection references and mutate in-place to avoid
        // orphanRemoval/dereferenced-collection errors on Hibernate flush.
        List<ContactEmail> existingEmails = contact.getEmails();
        if (existingEmails == null) {
            existingEmails = new ArrayList<>();
            contact.setEmails(existingEmails);
        } else {
            existingEmails.clear();
        }
        if (dto.getEmails() != null) {
            for (ContactEmailDto e : dto.getEmails()) {
                ContactEmail ce = new ContactEmail(e.getEmailAddress(), e.getLabel());
                ce.setContact(contact);
                existingEmails.add(ce);
            }
        }

        List<ContactPhone> existingPhones = contact.getPhones();
        if (existingPhones == null) {
            existingPhones = new ArrayList<>();
            contact.setPhones(existingPhones);
        } else {
            existingPhones.clear();
        }
        if (dto.getPhones() != null) {
            for (ContactPhoneDto p : dto.getPhones()) {
                ContactPhone cp = new ContactPhone(p.getPhoneNumber(), p.getLabel());
                cp.setContact(contact);
                existingPhones.add(cp);
            }
        }
    }

    private ContactDto mapToDto(Contact contact) {
        ContactDto dto = new ContactDto();
        dto.setId(contact.getId());
        dto.setFirstName(contact.getFirstName());
        dto.setLastName(contact.getLastName());
        dto.setTitle(contact.getTitle());

        if (contact.getEmails() != null) {
            dto.setEmails(contact.getEmails().stream().map(e -> {
                ContactEmailDto eDto = new ContactEmailDto();
                eDto.setId(e.getId());
                eDto.setEmailAddress(e.getEmailAddress());
                eDto.setLabel(e.getLabel());
                return eDto;
            }).collect(Collectors.toList()));
        }

        if (contact.getPhones() != null) {
            dto.setPhones(contact.getPhones().stream().map(p -> {
                ContactPhoneDto pDto = new ContactPhoneDto();
                pDto.setId(p.getId());
                pDto.setPhoneNumber(p.getPhoneNumber());
                pDto.setLabel(p.getLabel());
                return pDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    public String exportContactsToCSV(Long userId) {
        List<Contact> contacts = contactRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        try (StringWriter sw = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setHeader("Title", "First Name", "Last Name", "Email", "Phone").build())) {
            
            for (Contact contact : contacts) {
                String primaryEmail = contact.getEmails() != null && !contact.getEmails().isEmpty() ? contact.getEmails().get(0).getEmailAddress() : "";
                String primaryPhone = contact.getPhones() != null && !contact.getPhones().isEmpty() ? contact.getPhones().get(0).getPhoneNumber() : "";
                csvPrinter.printRecord(contact.getTitle(), contact.getFirstName(), contact.getLastName(), primaryEmail, primaryPhone);
            }
            csvPrinter.flush();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export contacts");
        }
    }

    @Transactional
    public void importContactsFromCSV(MultipartFile file, Long userId) {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
            
            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            for (CSVRecord csvRecord : csvParser) {
                Contact contact = new Contact();
                contact.setUser(user);
                contact.setTitle(csvRecord.get("Title"));
                contact.setFirstName(csvRecord.get("First Name"));
                contact.setLastName(csvRecord.get("Last Name"));

                String email = csvRecord.get("Email");
                if (email != null && !email.trim().isEmpty()) {
                    List<ContactEmail> emails = new ArrayList<>();
                    ContactEmail ce = new ContactEmail(email, "Personal");
                    ce.setContact(contact);
                    emails.add(ce);
                    contact.setEmails(emails);
                }

                String phone = csvRecord.get("Phone");
                if (phone != null && !phone.trim().isEmpty()) {
                    List<ContactPhone> phones = new ArrayList<>();
                    ContactPhone cp = new ContactPhone(phone, "Mobile");
                    cp.setContact(contact);
                    phones.add(cp);
                    contact.setPhones(phones);
                }
                contactRepository.save(contact);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
}
