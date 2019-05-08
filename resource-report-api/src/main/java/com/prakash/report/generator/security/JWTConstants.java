package com.prakash.report.generator.security;

import io.jsonwebtoken.SignatureAlgorithm;

public class JWTConstants {
    public static final SignatureAlgorithm SIGN_ALGORITHM = SignatureAlgorithm.HS256;
    static final String SECRET_KEY = "qwertyuiop";
    static final String TOKEN_PREFIX = "Bearer";
    static final int JWT_TTL = 60 * 60 * 60; // 1 hour
}
