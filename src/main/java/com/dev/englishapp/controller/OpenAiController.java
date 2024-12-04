package com.dev.englishapp.controller;

import com.dev.englishapp.openAiClient.OpenAiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/openai")
public class OpenAiController {

    private final OpenAiClient openAiClient;


    public OpenAiController(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;

    }

    @PostMapping("/ask")
    public ResponseEntity<String> getChatResponse(@RequestBody Map<String, String> payload) throws IOException {
        String prompt = payload.get("prompt");
        String response = openAiClient.getResponse(prompt);
        return ResponseEntity.ok(response);
    }
}
