package dev.collab_sync.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_member_id", nullable = false)
    private DocumentMember documentMember;

    @Column(length = 255)
    private String summary;

    @Lob
    private String beforeContent;

    @Lob
    private String afterContent;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    public void onCreate() {
        if (modifiedAt == null) {
            modifiedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof History history)) return false;
        return Objects.equals(id, history.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
