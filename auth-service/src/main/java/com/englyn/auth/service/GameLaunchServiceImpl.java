package com.englyn.auth.service;
import com.englyn.auth.entity.UserEntity;
import com.englyn.auth.model.GameLaunchRequest;
import com.englyn.auth.model.GameResponse;
import com.englyn.auth.repo.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpHeaders;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
public class GameLaunchServiceImpl implements GameLaunchService {

    @Value("${provider1.url}")
    private String provider1Url;

    @Value("${provider1.privateKeyPath}")
    private String privateKeyPath;

    @Value("${provider2.url}")
    private String provider2Url;

    @Value("${provider2.username}")
    private String provider2User;

    @Value("${provider2.password}")
    private String provider2Password;

    @Autowired
    private final UserRepository userRepository;  // Assuming you have a UserRepository

    @Autowired
    public GameLaunchServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GameResponse launchGame(GameLaunchRequest request) {
        // Validate token
        if (!isValidToken(request.getToken())) {
            return new GameResponse(null, "ERROR", "Invalid token. Unauthorized.");
        }
        GameResponse gameProviderUrl = new GameResponse();
        try {
            // Make a call to the simulated casino game provider API
            if ("superspins".equals(request.getGameid())) {
                // Call provider 1
                gameProviderUrl.setUrl(callProvider1(request));
                gameProviderUrl.setMessage("Game URL retrieved successfully");
                gameProviderUrl.setStatus("OK");
            } else if ("jackpotslots".equals(request.getGameid())) {
                // Call provider 2
                gameProviderUrl.setUrl(callProvider2(request));
                gameProviderUrl.setMessage("Game URL retrieved successfully");
                gameProviderUrl.setStatus("OK");
            } else {
                return new GameResponse(null, "ERROR", "Invalid game id.");
            }
        }catch (Exception e){
            return new GameResponse(null, "ERROR", "Unexpected error from the API: "+e.getLocalizedMessage());
        }

        return gameProviderUrl;
    }

    private boolean isValidToken(String token) {
        // Query the actual database to check if the token exists
        Optional<UserEntity> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        return userOptional.isPresent();
    }

    private String callProvider1(GameLaunchRequest request) {
        // Prepare and send request to provider 1
        String signature = calculateSHA1Signature(request.getToken() + "|" + request.getGameid());

        // Prepare the JSON payload
        String jsonPayload = createProvider1Payload(signature, request.getToken(), request.getGameid());
        return callProvider(request, jsonPayload);

    }

    private String callProvider2(GameLaunchRequest request) {
        // Prepare the JSON payload
        String jsonPayload = createProvider2Payload(request.getToken(), request.getGameid());
        return callProvider(request, jsonPayload);
    }

    private String calculateSHA1Signature(String data) {
        try {
            // Load private key from PEM file
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = readPrivateKeyFromPemFile(privateKeyPath);

            // Create a Signature object using SHA-1 with RSA
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);

            // Update the signature with the input data
            signature.update(data.getBytes());

            // Get the final signature value
            byte[] signatureBytes = signature.sign();

            // Convert the signature to a Base64-encoded string
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            // Handle the exception (e.g., log or throw a runtime exception)
            throw new RuntimeException("Error calculating SHA-1 signature", e);
        }
    }

    private PrivateKey readPrivateKeyFromPemFile(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            PemReader pemReader = new PemReader(reader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    private String createProvider1Payload(String signature, String token, String gameId) {
        // Create a JSON payload with the specified format
        String payloadTemplate = "{\n" +
                "    \"signature\":\"%s\",\n" +
                "    \"params\": {\n" +
                "        \"token\":\"%s\",\n" +
                "        \"gameid\":\"%s\"\n" +
                "    }\n" +
                "}";

        return String.format(payloadTemplate, base64Encode(signature), token, gameId);
    }

    private String createProvider2Payload(String token, String gameId) {
        // Create a JSON payload with the specified format
        String payloadTemplate = "{\n" +
                "    \"params\": {\n" +
                "        \"token\":\"%s\",\n" +
                "        \"gameid\":\"%s\"\n" +
                "    }\n" +
                "}";

        return String.format(payloadTemplate, token, gameId);
    }
    private String base64Encode(String data) {
        // Encode the input data to Base64
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private ResponseEntity<String> makeHttpPostRequest(String url, String jsonPayload) {
        try {
            // Create RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            if (provider2Url.equals(url))
                restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(provider2User, provider2Password));
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HttpEntity with headers and payload
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            // Make the HTTP POST request and get the response
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // Optionally, you can return the entire ResponseEntity if needed
            return responseEntity;
        } catch (Exception e) {
            // Handle the exception (e.g., log or throw a runtime exception)
            throw new RuntimeException("Error making HTTP POST request to Provider 1", e);
        }
    }

    private String callProvider(GameLaunchRequest request, String jsonPayload) {
        // Prepare the JSON payload
        try {
            ResponseEntity<String> responseEntity = makeHttpPostRequest(provider1Url, jsonPayload);
            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            // Extract values from JSON
            String status = rootNode.path("status").asText();
            String message = rootNode.path("message").asText();
            // Check if the response has a successful status
            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                // Process the values as needed
                if ("OK".equals(status)) {
                    return message;
                } else {
                    throw new RuntimeException("Error in response: " + message);
                }
            } else if (responseEntity.getStatusCode().is4xxClientError()) {
                throw new RuntimeException(message);
            } else{
                throw new RuntimeException("Non-successful HTTP status: " + responseEntity.getStatusCode().value());
            }
        } catch (Exception e) {
            // Handle the exception (e.g., log or throw a runtime exception)
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }


}
