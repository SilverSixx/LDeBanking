package com.silversixx.bankingapp.entity;

import com.silversixx.bankingapp.security.authorities.Role;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
    private String email;
    private String password;
    private String fullName;
    private String dob;
    private String gender;
    private String address;
    private String phoneNumber;
    private String accountNumber;
    private BigDecimal accountBalance;
    @ElementCollection(fetch = FetchType.EAGER) // Fetch the roles eagerly when loading a user
    private Set<Role> roles = new HashSet<>();
    @CreationTimestamp
    private LocalDateTime createAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
}

