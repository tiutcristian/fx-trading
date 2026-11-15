package com.banking.sofware.design.fx_trading.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Secures the FX Trading API as an OAuth2 resource server.
 *
 * Spring Security 6 equivalent of the lab's {@code @EnableResourceServer} +
 * {@code ResourceServerConfigurerAdapter}: CORS is allowed and every request must carry a
 * valid {@code Authorization: Bearer <TOKEN>} header, otherwise it is rejected with 401.
 */
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

	/**
	 * Validates tokens with the symmetric key from {@code security.oauth2.resource.jwt.key-value}.
	 * It must match the key user-administration signs tokens with (HMAC256).
	 * java-jwt is used because Spring's default Nimbus decoder rejects HS256 keys under 256 bits.
	 */
	@Bean
	public JwtDecoder jwtDecoder(@Value("${security.oauth2.resource.jwt.key-value}") String key,
			ObjectMapper objectMapper) {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).build();
		return token -> {
			DecodedJWT decoded;
			try {
				decoded = verifier.verify(token);
			} catch (JWTVerificationException e) {
				throw new BadJwtException("Invalid JWT: " + e.getMessage(), e);
			}
			try {
				Map<String, Object> headers = parse(objectMapper, decoded.getHeader());
				Map<String, Object> claims = parse(objectMapper, decoded.getPayload());
				Jwt.Builder builder = Jwt.withTokenValue(token)
						.headers(h -> h.putAll(headers))
						.claims(c -> c.putAll(claims));
				if (decoded.getIssuedAtAsInstant() != null) {
					builder.issuedAt(decoded.getIssuedAtAsInstant());
				}
				if (decoded.getExpiresAtAsInstant() != null) {
					builder.expiresAt(decoded.getExpiresAtAsInstant());
				}
				return builder.build();
			} catch (Exception e) {
				throw new BadJwtException("Malformed JWT", e);
			}
		};
	}

	private static Map<String, Object> parse(ObjectMapper objectMapper, String base64UrlJson) throws Exception {
		byte[] json = Base64.getUrlDecoder().decode(base64UrlJson.getBytes(StandardCharsets.UTF_8));
		return objectMapper.readValue(json, new TypeReference<>() {});
	}

}
