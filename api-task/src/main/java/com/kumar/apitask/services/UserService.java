package com.kumar.apitask.services;

import java.util.List;

import com.kumar.apitask.dto.security.LoginRequestDTO;
import com.kumar.apitask.dto.security.TokenResponseDTO;
import com.kumar.apitask.dto.user.UserRequestDTO;
import com.kumar.apitask.dto.user.UserResponseDTO;

public interface UserService {

	UserResponseDTO register(UserRequestDTO dto);

	TokenResponseDTO login(LoginRequestDTO dto);

	UserResponseDTO me();

	List<UserResponseDTO> findAll();

}
