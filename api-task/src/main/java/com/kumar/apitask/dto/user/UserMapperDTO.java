package com.kumar.apitask.dto.user;

import com.kumar.apitask.entities.UserEntity;

public class UserMapperDTO {

	public static UserEntity fromDTO(UserRequestDTO dto) {
		return new UserEntity(null, dto.getUsername(), dto.getEmail(), dto.getPassword(), null, null, null, null, null, null);
	}

	public static UserResponseDTO fromEntity(UserEntity entity) {
		return new UserResponseDTO(entity.getId(), entity.getUsername(), entity.getEmail());
	}
	
}
