package com.immortals.authapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immortals.authapp.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "user_name"),
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_phone", columnList = "contactNumber")

        }, uniqueConstraints = {}, schema = "auth")

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"userAddresses", "roles"})
public class User extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "auth.user_sequence", allocationSize = 1,
            initialValue = 1)
    @Column(name = "user_id",unique = true, nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long userId;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 16)
    @Column(name = "user_name", nullable = false)
    private String userName;

    @JsonIgnore
    @NotNull(message = "Password cannot be empty")
    @Column(name = "password")
    private String password;

    @Email(message = "Email is not in correct format")
    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @Email(message = "Email is not in correct format")
    @Column(name = "alternate_email", nullable = true)
    private String alternateEmail;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @NotBlank
    @Column(name = "phone_code", nullable = false)
    private String phoneCode;

    @Column(length = 10)
    @Pattern(regexp = "^(\\+91)?[6-9][0-9]{9}$", message = "Contact number invalid")
    private String contactNumber;

    @Column(length = 10)
    @Pattern(regexp = "^(\\+91)?[6-9][0-9]{9}$", message = "Alternate contact invalid")
    private String alternateContact;

    @Column(name = "phone_number_verified", nullable = false)
    private Boolean phoneNumberVerified;

    @Column(name = "login_time", nullable = true)
    private Instant login;

    @Column(name = "logout_time", nullable = true)
    private Instant logout;

    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "account_locked", nullable = false)
    private Boolean accountLocked;

    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired;

    @Column(name = "active_ind", nullable = false)
    private Boolean activeInd;

@JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private List<UserAddress> userAddresses;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")},schema = "auth")
    private Set<Roles> roles;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getUserId() != null && Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
