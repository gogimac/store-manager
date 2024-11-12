package ro.ing.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OpenAiService {

    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final Logger logger = Logger.getLogger(OpenAiService.class.getName());

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for OpenAiService, injecting the API key from application properties.
     * @param apiKey the OpenAI API key for authorization
     */
    public OpenAiService(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        logger.log(Level.INFO, "OpenAiService initialized with API key present: {0}", apiKey != null);
    }

    /**
     * Initializes the service and logs the API key status.
     * Note: Avoid logging sensitive information in production.
     */
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "OpenAiService initialized successfully. API key loaded: {0}", apiKey != null ? "Yes" : "No");
    }

    /**
     * Sends a request to the OpenAI API to generate text based on a provided prompt.
     * @param prompt the prompt to send to OpenAI for generating a response
     * @return the generated text response from OpenAI, or an error message if the request fails
     */
    public String generateText(String prompt) {
        logger.log(Level.INFO, "Preparing request to OpenAI for prompt: {0}", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");  // Set to an accessible model
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 100);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            logger.log(Level.INFO, "Serialized request body for OpenAI: {0}", requestBodyJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            logger.log(Level.INFO, "Sending request to OpenAI API...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.log(Level.INFO, "Received response from OpenAI API: {0}", response.body());

            JsonNode responseJson = objectMapper.readTree(response.body());

            // Check if there's an error field in the response
            if (responseJson.has("error")) {
                String errorMessage = responseJson.path("error").path("message").asText();
                logger.log(Level.SEVERE, "Error received from OpenAI API: {0}", errorMessage);
                return "Error from OpenAI API: " + errorMessage;
            }

            // Extract the generated text from a successful response
            if (responseJson.has("choices") && responseJson.path("choices").has(0)) {
                String generatedText = responseJson.path("choices").get(0).path("text").asText().trim();
                logger.log(Level.INFO, "Generated text from OpenAI: {0}", generatedText);
                return generatedText;
            } else {
                logger.log(Level.SEVERE, "Unexpected response format from OpenAI API: {0}", response.body());
                return "Unexpected response format from OpenAI API";
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while generating text with OpenAI API", e);
            return "Error generating text due to network issue";

        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Request was interrupted while generating text with OpenAI API", e);
            Thread.currentThread().interrupt();  // Restore interrupted state
            return "Request was interrupted";
        }
    }
}
