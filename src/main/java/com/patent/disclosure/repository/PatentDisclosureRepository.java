package com.patent.disclosure.repository;

import com.patent.disclosure.model.PatentDisclosure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatentDisclosureRepository
        extends JpaRepository<PatentDisclosure, Long> {

    Optional<PatentDisclosure> findByDisclosureId(String disclosureId);
}