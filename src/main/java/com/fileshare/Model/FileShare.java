package com.fileshare.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "file_shares", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "file_id", "shared_with_user_id" })
})
@Data
@NoArgsConstructor
public class FileShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File file;

    @ManyToOne
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sharedByUser;

    @ManyToOne
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sharedWithUser;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
