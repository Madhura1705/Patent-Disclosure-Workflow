package com.patent.disclosure.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "patent_disclosures")
public class PatentDisclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "disclosure_id", unique = true, nullable = false)
    private String disclosureId;

    @NotBlank(message = "Applicant name is required")
    @Column(nullable = false)
    private String applicantName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;

    @NotBlank(message = "Patent title is required")
    @Column(nullable = false)
    private String patentTitle;

    @NotBlank(message = "Patent description is required")
    @Column(nullable = false, length = 5000)
    private String patentDescription;

    private String documentName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private String rejectionReason;

    private LocalDateTime reviewedAt;


    // Constructor
    public PatentDisclosure() {
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }


    public String getDisclosureId() {
        return disclosureId;
    }

    public void setDisclosureId(String disclosureId) {
        this.disclosureId = disclosureId;
    }


    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public String getPatentTitle() {
        return patentTitle;
    }

    public void setPatentTitle(String patentTitle) {
        this.patentTitle = patentTitle;
    }


    public String getPatentDescription() {
        return patentDescription;
    }

    public void setPatentDescription(String patentDescription) {
        this.patentDescription = patentDescription;
    }


    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }


    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }


    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}