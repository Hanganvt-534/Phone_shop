package com.project.phone_shop.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.phone_shop.DTO.Request.AuthRequest;
import com.project.phone_shop.DTO.Request.IntrospectRequest;
import com.project.phone_shop.DTO.Request.LogoutRequest;
import com.project.phone_shop.DTO.Response.AuthResponse;
import com.project.phone_shop.DTO.Response.IntrospectResponse;
import com.project.phone_shop.DTO.Request.RefreshRequest;
import com.project.phone_shop.Entity.InvalidatedToken;
import com.project.phone_shop.Entity.User;
import com.project.phone_shop.Repository.IntrospectRepository;
import com.project.phone_shop.Repository.UserRepository;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthService {

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    final UserRepository userRepository;

    final IntrospectRepository introspectRepository;

    public AuthResponse login(AuthRequest authRequest) {

        PasswordEncoder passwordEncoder =
                new BCryptPasswordEncoder(10);

        var user = userRepository
                .findByUsername(authRequest.getUsername())
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_EXISTED)
                );

        boolean authenticated = passwordEncoder.matches(
                authRequest.getPassword(),
                user.getPassword()
        );

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    private String generateToken(User user) {

        // Header của JWT
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Payload / Claims
        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder()

                        // Subject = ID của user
                        .subject(user.getId().toString())

                        // Ai phát hành token
                        .issuer("phone_shop.com")

                        // Thời điểm tạo token
                        .issueTime(new Date())

                        // Thời điểm token hết hạn
                        .expirationTime(
                                new Date(
                                        Instant.now()
                                                .plus(
                                                        VALID_DURATION,
                                                        ChronoUnit.SECONDS
                                                )
                                                .toEpochMilli()
                                )
                        )

                        // ID riêng của JWT
                        .jwtID(UUID.randomUUID().toString())

                        // Quyền của user
                        .claim("scope", buildScope(user))

                        .build();

        // Tạo Payload từ claims
        Payload payload =
                new Payload(jwtClaimsSet.toJSONObject());

        // Tạo JWS object
        JWSObject jwsObject =
                new JWSObject(header, payload);

        try {

            // Ký JWT bằng secret key
            jwsObject.sign(
                    new MACSigner(SIGNER_KEY.getBytes())
            );

            // Trả JWT dạng String
            return jwsObject.serialize();

        } catch (JOSEException e) {

            log.error("Cannot create token", e);

            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }


    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        boolean isValid = true;
        SignedJWT jwt = null;

        try {
            jwt =  verifyToken(token, false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .usedId(Objects.nonNull(jwt) ? jwt.getJWTClaimsSet().getSubject() : null)
                .valid(isValid)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }


    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);

            Long jit = Long.valueOf(signToken.getJWTClaimsSet().getJWTID());
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            introspectRepository.save(invalidatedToken);
        } catch (AppException | JOSEException | ParseException exception){
            log.info("Token already expired");
        }
    }

    public AuthResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        Long jit = Long.valueOf(signedJWT.getJWTClaimsSet().getJWTID());
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        introspectRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthResponse.builder().token(token).build();
    }
}

