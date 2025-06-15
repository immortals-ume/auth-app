package com.immortals.authapp.model.entity;

import com.immortals.authapp.model.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Entity
@Table(name = "cities", schema = "auth")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@Setter
@EntityListeners(AuditingEntityListener.class)
public class City extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private States states;


    @Column(name = "activeInd", nullable = false)
    private Boolean activeInd;
}
