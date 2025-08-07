package dev.collab_sync.repository;

import dev.collab_sync.domain.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {
}
