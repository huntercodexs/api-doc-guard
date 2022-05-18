package com.huntercodexs.sample.config.oauth2.security;

import com.huntercodexs.sample.config.oauth2.model.OperatorEntity;
import com.huntercodexs.sample.config.oauth2.model.RoleEntity;
import com.huntercodexs.sample.config.oauth2.repository.OperatorRepository;
import com.huntercodexs.sample.config.oauth2.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    OperatorRepository operatorRepository;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String usernameCred = authentication.getPrincipal().toString();
        String passwordCred = authentication.getCredentials().toString();

        if (usernameCred.equals("")) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        try {

            OperatorEntity operatorEntity = operatorRepository.findByUsername(usernameCred);

            if (operatorEntity == null || operatorEntity.getUsername().equals("")) {
                throw new BadCredentialsException("Invalid Credentials");
            }

            if (isValidCredentials(passwordCred, operatorEntity.getPassword())) {
                RoleEntity roleEntity = roleRepository.findRole(operatorEntity.getRole());
                customUserDetailsService.setUserCredentialsFromDatabase(roleEntity.getName(), usernameCred, passwordCred);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameCred);
                return new UsernamePasswordAuthenticationToken(userDetails, passwordCred, userDetails.getAuthorities());
            }

        } catch (BadCredentialsException bc) {
            bc.printStackTrace();
        }

        throw new BadCredentialsException("Invalid Credentials");

    }

    protected boolean isValidCredentials(String passwordCredentials, String passwordAuthenticate) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordBCrypt = bCryptPasswordEncoder.encode(passwordAuthenticate);
        return bCryptPasswordEncoder.matches(passwordCredentials, passwordBCrypt);

    }

}
