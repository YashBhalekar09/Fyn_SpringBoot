//package com.InsuranceProposerCrud.JWT;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.Collections;
//
//import com.InsuranceProposerCrud.entity.UserJWT; // adjust as per your entity
//
//public class CustomUserDetails implements UserDetails {
//
//    private final UserJWT user;
//
//    public CustomUserDetails(UserJWT user) {
//        this.user = user;
//    }
//
//    public String getEmail() {
//        return user.getEmail();
//    }
//
//    public String getFullName() {
//        return user.getFullName();
//    }
//
//    public String getAddress() {
//        return user.getAddress();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getUsername();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
