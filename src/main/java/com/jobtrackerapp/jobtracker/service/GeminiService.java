package com.jobtrackerapp.jobtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public String analyzeMatch(String resumeText, String jobDescription) throws Exception {
        String prompt = "You are a resume matcher. Compare the resume and job description below.\n" +
                "Return ONLY a raw JSON object — no markdown, no explanation, just JSON.\n\n" +
                "{\n" +
                "  \"matchScore\": <integer 0-100>,\n" +
                "  \"matchedSkills\": [\"skill1\", \"skill2\"],\n" +
                "  \"missingSkills\": [\"skill1\", \"skill2\"],\n" +
                "  \"suggestions\": [\"tip1\", \"tip2\", \"tip3\"],\n" +
                "  \"summary\": \"one sentence assessment\"\n" +
                "}\n\n" +
                "RESUME:\n" + resumeText + "\n\n" +
                "JOB DESCRIPTION:\n" + jobDescription;

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("temperature", 0.3);

        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode message = objectMapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.set("messages", messages);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        String response = webClientBuilder.build()
                .post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(jsonBody)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                            .map(errorBody -> new RuntimeException("Groq error details: " + errorBody))
                )
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        String text = root
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText();

        return text.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
    }
}