package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
//implements UserDetails dung cua
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "facebook_account_id")
    private Long facebookAccountId;

    @Column(name = "google_account_id")
    private Long googleAccountId;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // laays ra cac quyen roles
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + getRole().getName()));
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        return authorities;
    }

    @Override
    public String getUsername() { // dung gia tri truong do de dang nhap
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() { // Check acc co thoi luong vo thoi han
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // khong the khoa duoc user
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
