package com.iztech.utms.service;

import com.iztech.utms.model.Application;
import com.iztech.utms.model.Document;
import com.iztech.utms.model.Document.DocumentType;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DocumentRepository;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    // Root location for file storage
    private final Path rootLocation = Paths.get("uploads");

    private static final String ALLOWED_CONTENT_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Transactional
    public void storeDocument(String username, Long applicationId, String docTypeStr, MultipartFile file) {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            if (!Objects.equals(file.getContentType(), ALLOWED_CONTENT_TYPE)) {
                throw new RuntimeException("Invalid file type. Only PDF is allowed per Rule PR-11.");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new RuntimeException("File size exceeds the 5MB limit (PR-11).");
            }

            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Security: Only the student owner can upload
            if (!application.getStudent().getUsername().equals(username)) {
                throw new RuntimeException(
                        "Security Alert: You are not authorized to upload documents for this application.");
            }

            DocumentType type = DocumentType.valueOf(docTypeStr);
            String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String storageFileName = "APP_" + applicationId + "_" + type.name() + "_" + System.currentTimeMillis()
                    + ".pdf";

            Path destinationFile = this.rootLocation.resolve(Paths.get(storageFileName)).normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Check if document of this type already exists for this application
            java.util.Optional<Document> existingDocOpt = documentRepository
                    .findByApplicationIdAndDocumentType(applicationId, type);

            Document document;
            if (existingDocOpt.isPresent()) {
                document = existingDocOpt.get();
                // Delete old file if possible to save space
                try {
                    Files.deleteIfExists(Paths.get(document.getFilePath()));
                } catch (IOException ignored) {
                    // Log or ignore if file deletion fails
                }
                // Update existing entity
                document.setFilePath(destinationFile.toString());
                document.setFileSize(file.getSize());
            } else {
                // Create new entity
                document = Document.builder()
                        .application(application)
                        .documentType(type)
                        .filePath(destinationFile.toString())
                        .fileSize(file.getSize())
                        .build();
            }

            documentRepository.save(document);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    // WP-4 ADDITION: Load file for viewing
    public Resource loadFileAsResource(Long documentId, String username) {
        try {
            Document doc = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            // Access Control: Owner OR OIDB/Dean/YGK/Admin can view
            // In a real scenario, we'd check the user's role here more robustly.
            // For now, we assume if the user is authenticated and asks via the Controller's
            // role check, it's safe.

            Path filePath = Paths.get(doc.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + doc.getFilePath());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }
}