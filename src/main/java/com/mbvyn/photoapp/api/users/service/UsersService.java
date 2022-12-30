package com.mbvyn.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mbvyn.photoapp.api.users.shared.UserDTO;

public interface UsersService extends UserDetailsService {
	UserDTO createUser(UserDTO userDetails);
	UserDTO getUserDetailsByEmail(String email);
}
