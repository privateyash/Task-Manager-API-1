package com.kumar.apitask.services;

import java.util.List;

import com.kumar.apitask.dto.security.TokenResponseDTO;

public interface TokenJwtService {

	TokenResponseDTO createAccessToken(String username, List<String> roles);

	boolean validateToken(String token);

	String getUsername(String token);

}
