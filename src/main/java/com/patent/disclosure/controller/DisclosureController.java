package com.patent.disclosure.controller;

import com.patent.disclosure.model.PatentDisclosure;
import com.patent.disclosure.repository.PatentDisclosureRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/disclosures")
public class DisclosureController {


    private final PatentDisclosureRepository repository;

    private final Path uploadDirectory =
            Paths.get("uploads");


    public DisclosureController(
            PatentDisclosureRepository repository) {

        this.repository = repository;
    }


    // =========================================================
    // 1. SUBMIT PATENT DISCLOSURE
    // =========================================================

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> submitDisclosure(

            @Valid
            @RequestParam("applicantName")
            String applicantName,

            @Valid
            @RequestParam("email")
            String email,

            @Valid
            @RequestParam("department")
            String department,

            @Valid
            @RequestParam("patentTitle")
            String patentTitle,

            @Valid
            @RequestParam("patentDescription")
            String patentDescription,

            @RequestParam("document")
            MultipartFile document) {


        try {

            // -------------------------
            // Required field validation
            // -------------------------

            if (applicantName.isBlank()
                    || email.isBlank()
                    || department.isBlank()
                    || patentTitle.isBlank()
                    || patentDescription.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message",
                                "All required fields must be filled."
                        ));
            }


            // -------------------------
            // Document validation
            // -------------------------

            if (document.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message",
                                "Please upload a supporting document."
                        ));
            }


            // Maximum 10 MB

            if (document.getSize()
                    > 10 * 1024 * 1024) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message",
                                "Document size must not exceed 10 MB."
                        ));
            }


            String originalFileName =
                    document.getOriginalFilename();


            if (originalFileName == null
                    || originalFileName.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message",
                                "Invalid document name."
                        ));
            }


            // -------------------------
            // File type validation
            // -------------------------

            String lowerFileName =
                    originalFileName.toLowerCase();


            if (!(lowerFileName.endsWith(".pdf")
                    || lowerFileName.endsWith(".doc")
                    || lowerFileName.endsWith(".docx"))) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message",
                                "Only PDF, DOC and DOCX files are allowed."
                        ));
            }


            // -------------------------
            // Create uploads folder
            // -------------------------

            Files.createDirectories(
                    uploadDirectory
            );


            // -------------------------
            // Generate stored filename
            // -------------------------

            String storedFileName =
                    UUID.randomUUID()
                            + "_"
                            + originalFileName;


            Path targetPath =
                    uploadDirectory.resolve(
                            storedFileName
                    );


            Files.copy(
                    document.getInputStream(),
                    targetPath
            );


            // -------------------------
            // Generate Disclosure ID
            // -------------------------

            String datePart =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter
                                            .ofPattern("yyyyMMdd")
                            );


            String disclosureId =
                    "PD-"
                            + datePart
                            + "-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();


            // -------------------------
            // Create database record
            // -------------------------

            PatentDisclosure disclosure =
                    new PatentDisclosure();


            disclosure.setDisclosureId(
                    disclosureId
            );

            disclosure.setApplicantName(
                    applicantName.trim()
            );

            disclosure.setEmail(
                    email.trim()
            );

            disclosure.setDepartment(
                    department.trim()
            );

            disclosure.setPatentTitle(
                    patentTitle.trim()
            );

            disclosure.setPatentDescription(
                    patentDescription.trim()
            );

            disclosure.setDocumentName(
                    originalFileName
            );

            disclosure.setStatus(
                    "SUBMITTED"
            );

            disclosure.setSubmittedAt(
                    LocalDateTime.now()
            );


            // -------------------------
            // Save to H2 database
            // -------------------------

            PatentDisclosure saved =
                    repository.save(disclosure);


            // -------------------------
            // Success response
            // -------------------------

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success", true,

                                    "message",
                                    "Patent disclosure submitted successfully.",

                                    "disclosureId",
                                    saved.getDisclosureId(),

                                    "status",
                                    saved.getStatus()
                            )
                    );


        } catch (IOException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "success", false,

                                    "message",
                                    "Unable to save the uploaded document."
                            )
                    );
        }
    }


    // =========================================================
    // 2. GET ALL DISCLOSURES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<PatentDisclosure>>
    getAllDisclosures() {

        return ResponseEntity.ok(
                repository.findAll()
        );
    }


    // =========================================================
    // 3. GET ONE DISCLOSURE
    // =========================================================

    @GetMapping("/{disclosureId}")
    public ResponseEntity<?> getDisclosure(

            @PathVariable
            String disclosureId) {


        return repository
                .findByDisclosureId(disclosureId)

                .<ResponseEntity<?>>map(
                        disclosure ->
                                ResponseEntity.ok(
                                        disclosure
                                )
                )

                .orElseGet(
                        () ->
                                ResponseEntity
                                        .status(
                                                HttpStatus.NOT_FOUND
                                        )
                                        .body(
                                                Map.of(
                                                        "success",
                                                        false,

                                                        "message",
                                                        "Disclosure not found."
                                                )
                                        )
                );
    }


    // =========================================================
    // 4. APPROVE DISCLOSURE
    // =========================================================

    @PutMapping("/{disclosureId}/approve")
    public ResponseEntity<?> approveDisclosure(

            @PathVariable
            String disclosureId) {


        return repository
                .findByDisclosureId(disclosureId)

                .map(
                        disclosure -> {

                            disclosure.setStatus(
                                    "APPROVED"
                            );

                            disclosure.setRejectionReason(
                                    null
                            );

                            disclosure.setReviewedAt(
                                    LocalDateTime.now()
                            );


                            PatentDisclosure updated =
                                    repository.save(
                                            disclosure
                                    );


                            return ResponseEntity.ok(
                                    Map.of(
                                            "success",
                                            true,

                                            "message",
                                            "Disclosure approved successfully.",

                                            "disclosureId",
                                            updated.getDisclosureId(),

                                            "status",
                                            updated.getStatus()
                                    )
                            );
                        }
                )

                .orElseGet(
                        () ->
                                ResponseEntity
                                        .status(
                                                HttpStatus.NOT_FOUND
                                        )
                                        .body(
                                                Map.of(
                                                        "success",
                                                        false,

                                                        "message",
                                                        "Disclosure not found."
                                                )
                                        )
                );
    }


    // =========================================================
    // 5. REJECT DISCLOSURE
    // =========================================================

    @PutMapping("/{disclosureId}/reject")
    public ResponseEntity<?> rejectDisclosure(

            @PathVariable
            String disclosureId,

            @RequestBody
            Map<String, String> request) {


        String reason =
                request.get("reason");


        // Rejection reason is mandatory

        if (reason == null
                || reason.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Rejection reason is required."
                            )
                    );
        }


        return repository
                .findByDisclosureId(disclosureId)

                .map(
                        disclosure -> {

                            disclosure.setStatus(
                                    "REJECTED"
                            );

                            disclosure.setRejectionReason(
                                    reason.trim()
                            );

                            disclosure.setReviewedAt(
                                    LocalDateTime.now()
                            );


                            PatentDisclosure updated =
                                    repository.save(
                                            disclosure
                                    );


                            return ResponseEntity.ok(
                                    Map.of(
                                            "success",
                                            true,

                                            "message",
                                            "Disclosure rejected successfully.",

                                            "disclosureId",
                                            updated.getDisclosureId(),

                                            "status",
                                            updated.getStatus()
                                    )
                            );
                        }
                )

                .orElseGet(
                        () ->
                                ResponseEntity
                                        .status(
                                                HttpStatus.NOT_FOUND
                                        )
                                        .body(
                                                Map.of(
                                                        "success",
                                                        false,

                                                        "message",
                                                        "Disclosure not found."
                                                )
                                        )
                );
    }
}