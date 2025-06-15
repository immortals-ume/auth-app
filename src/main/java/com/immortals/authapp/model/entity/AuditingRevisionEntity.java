package com.immortals.authapp.model.entity;


import com.immortals.authapp.audit.AuditingRevisionListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo", schema = "audit")
@Getter
@Setter
@RevisionEntity(AuditingRevisionListener.class)
public class AuditingRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq_gen")
    @SequenceGenerator(
            name = "revinfo_seq_gen",
            sequenceName = "audit.revinfo_seq",
            allocationSize = 50
    )
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(nullable = false)
    private long timestamp;

    private String username;
}
