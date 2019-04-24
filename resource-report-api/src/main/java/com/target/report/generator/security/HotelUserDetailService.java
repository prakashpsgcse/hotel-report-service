package com.target.report.generator.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.target.report.generator.dao.HotelUserDAO;
import com.target.report.generator.domain.HotelUser;

@Component
public class HotelUserDetailService implements UserDetailsService {

    @Autowired
    private HotelUserDAO hotelUserDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<HotelUser> user = hotelUserDAO.findById(username);
        if (user.isPresent()) {
            HotelUserPrincipal hotelUserPrincipal = new HotelUserPrincipal();
            hotelUserPrincipal.setHotelUser(user.get());
            return hotelUserPrincipal;
        }
        else {
            throw new UsernameNotFoundException("Hotel user not exists");
        }

    }

}
