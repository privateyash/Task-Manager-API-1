package com.kumar.apitask.services;

import java.util.ArrayList;
import java.util.List;

import com.kumar.apitask.exceptions.ApiTaskLoginException;
import com.kumar.apitask.exceptions.ApiTaskResourceNotFoundException;
import com.kumar.apitask.exceptions.ApiTaskSQLException;
import com.kumar.apitask.repositories.RoleRepository;
import com.kumar.apitask.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kumar.apitask.dto.security.LoginRequestDTO;
import com.kumar.apitask.dto.security.TokenResponseDTO;
import com.kumar.apitask.dto.user.UserMapperDTO;
import com.kumar.apitask.dto.user.UserRequestDTO;
import com.kumar.apitask.dto.user.UserResponseDTO;
import com.kumar.apitask.entities.RoleEntity;
import com.kumar.apitask.entities.UserEntity;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final TokenJwtService tokenJwtService;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, TokenJwtService tokenJwtService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.tokenJwtService = tokenJwtService;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public UserResponseDTO register(UserRequestDTO dto) {
		if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new ApiTaskSQLException("Username already exists");
		}

		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new ApiTaskSQLException("E-mail already exists");
		}

		UserEntity user = UserMapperDTO.fromDTO(dto);

		user.setEnabled(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonLocked(true);
		user.setAccountNonExpired(true);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		List<RoleEntity> roles = new ArrayList<>();
		roles.add(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException()));

		user.setRoles(roles);

		return UserMapperDTO.fromEntity(userRepository.save(user));
	}

	@Override
	public TokenResponseDTO login(LoginRequestDTO dto) {
		UserEntity user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new ApiTaskLoginException("Username not found"));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new ApiTaskLoginException("Invalid password");
		}

		List<String> roles = user.getRoles().stream().map(role -> role.toString()).toList();

		return tokenJwtService.createAccessToken(user.getUsername(), roles);
	}

	@Override
	public UserResponseDTO me() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new ApiTaskResourceNotFoundException("User not found"));

		return UserMapperDTO.fromEntity(user);
	}

	@Override
	public List<UserResponseDTO> findAll() {
		return userRepository.findAll().stream().map(UserMapperDTO::fromEntity).toList();
	}

}
