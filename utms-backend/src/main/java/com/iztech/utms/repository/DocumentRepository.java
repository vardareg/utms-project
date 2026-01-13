package com.iztech.utms.repository;

import com.iztech.utms.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByApplicationId(Long applicationId);

    java.util.Optional<Document> findByApplicationIdAndDocumentType(Long applicationId,
            com.iztech.utms.model.Document.DocumentType documentType);
}