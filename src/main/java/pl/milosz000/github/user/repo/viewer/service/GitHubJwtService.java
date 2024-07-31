package pl.milosz000.github.user.repo.viewer.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Service
public class GitHubJwtService {

    private static final String GITHUB_APP_ID = "957165";
    private static final long TOKEN_TTL_MS = 600000;
    private static final String DER_KEY_FILEPATH = "/Users/miloszmazur/Documents/GitHubUserRepoViewer/src/main/resources/key.der";

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(DER_KEY_FILEPATH));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public String generateJWT() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our private key
        Key signingKey = getPrivateKey();

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(GITHUB_APP_ID)
                .signWith(signingKey, signatureAlgorithm);


        long expMillis = nowMillis + TOKEN_TTL_MS;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);


        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }
}
