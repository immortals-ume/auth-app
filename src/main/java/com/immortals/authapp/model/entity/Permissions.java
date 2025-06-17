package com.immortals.authapp.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immortals.authapp.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "permission", schema = "auth")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Permissions extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "permission_name", length = 50)
    @NotNull
    private String permissionName;

    @Column(name = "active_ind", length = 50)
    @NotNull
    private Boolean activeInd;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore // Prevent recursion in APIs
    private Set<Roles> roles;
}