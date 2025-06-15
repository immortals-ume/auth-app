package com.immortals.authapp.model.entity;

import com.immortals.authapp.model.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter
@Entity
@Table(name = "role", schema = "auth")
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Roles extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false , unique = true, updatable = false )
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 20)
    private String roleName;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id" ),
            inverseJoinColumns = @JoinColumn(name = "permission_id" ),schema = "auth")
    private Set<Permissions> permissions;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @Column(name = "active_ind", nullable = false)
    private Boolean activeInd;

}