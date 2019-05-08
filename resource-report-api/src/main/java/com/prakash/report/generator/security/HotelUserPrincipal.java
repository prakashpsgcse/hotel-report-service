package com.prakash.report.generator.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.prakash.report.generator.domain.HotelUser;

public class HotelUserPrincipal implements UserDetails {

    private HotelUser hotelUser;

    public HotelUser getHotelUser() {
        return hotelUser;
    }

    public void setHotelUser(HotelUser hotelUser) {
        this.hotelUser = hotelUser;
    }

    // TODO implement ROLE based auth
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(hotelUser.getRole()));
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return hotelUser.getPassword();
    }

    @Override
    public String getUsername() {
        return hotelUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
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
