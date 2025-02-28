package kr.co.proten.llmops.core.exception;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;

public class AccessTokenExpiredException extends ExpiredJwtException {
    public AccessTokenExpiredException(Header header, Claims claims, String message) {
        super(header, claims, message);
    }

}
