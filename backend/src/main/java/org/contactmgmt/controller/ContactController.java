package org.contactmgmt.controller;

import jakarta.validation.Valid;
import org.contactmgmt.dto.ContactDto;
import org.contactmgmt.dto.MessageResponse;
import org.contactmgmt.security.UserDetailsImpl;
import org.contactmgmt.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    private Long getUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public ResponseEntity<Page<ContactDto>> getContacts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contactService.getContacts(getUserId(), search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id, getUserId()));
    }

    @PostMapping
    public ResponseEntity<ContactDto> createContact(@Valid @RequestBody ContactDto contactDto) {
        return ResponseEntity.ok(contactService.createContact(contactDto, getUserId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable Long id, @Valid @RequestBody ContactDto contactDto) {
        return ResponseEntity.ok(contactService.updateContact(id, contactDto, getUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id, getUserId());
        return ResponseEntity.ok(new MessageResponse("Contact deleted successfully!"));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {
        String csvData = contactService.exportContactsToCSV(getUserId());
        byte[] output = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.csv");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(output);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> importContacts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("No file uploaded"));
        }
        contactService.importContactsFromCSV(file, getUserId());
        return ResponseEntity.ok(new MessageResponse("Contacts imported successfully!"));
    }
}
