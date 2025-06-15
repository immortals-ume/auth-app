package com.immortals.authapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immortals.authapp.model.audit.Auditable;
import com.immortals.authapp.model.enums.AddressStatus;
import com.immortals.authapp.model.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "user_address", schema = "auth")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserAddress extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_address_sequence")
    @SequenceGenerator(name = "user_address_sequence", sequenceName = "auth.user_address_sequence", allocationSize = 1,
            initialValue = 1)
    private Long userAddressId;

    @Column(name = "address_uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String addressUuid;

    @Size(max = 50)
    private String label;

    @Column(name = "address_line1", nullable = false)
    @NotBlank
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 255)
    private String landmark;

    @Column(nullable = false, length = 6)
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressStatus status;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "is_po_box", nullable = false)
    private Boolean isPoBox;

    @Size(max = 50)
    private String timezone;

    @Column(name = "language_code", length = 5)
    private String languageCode;

    @Column(name = "formatted_address", columnDefinition = "TEXT")
    private String formattedAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "state_id")
    private States states;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "city_id")
    private City city;
}

