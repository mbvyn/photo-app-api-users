package com.mbvyn.photoapp.api.users.ui.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbvyn.photoapp.api.users.service.UsersService;
import com.mbvyn.photoapp.api.users.shared.UserDTO;
import com.mbvyn.photoapp.api.users.ui.model.UserRequestModel;
import com.mbvyn.photoapp.api.users.ui.model.UserResponseModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private Environment env;
	
	@Autowired
	UsersService usersService;

	@GetMapping("/status/check")
	public String status() {
		return "Working on port " + env.getProperty("local.server.port") + ", expiration token = " + env.getProperty("token.expiration_time");
	}

	@PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
				 produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserResponseModel> createUser(@Valid @RequestBody UserRequestModel userDetails) {
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
		UserDTO createdUser = usersService.createUser(userDTO);
		
		UserResponseModel returnValue = modelMapper.map(createdUser, UserResponseModel.class); 
		
		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}
	
	@PreAuthorize("principal == #userId")
	@GetMapping(value="/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId) {
		
		UserDTO userDTO = usersService.getUserByUserId(userId);
		UserResponseModel returnValue = new ModelMapper().map(userDTO, UserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	}
}
