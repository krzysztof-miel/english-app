package com.dev.englishapp.controller;

import com.dev.englishapp.exception.SessionNotFoundException;
import com.dev.englishapp.model.Translation;
import com.dev.englishapp.service.LearningSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/learning")
@CrossOrigin(origins = "http://localhost:3000")
public class LearningSessionController {

    @Autowired
    private LearningSessionService learningSessionService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Boolean>> startSession(Authentication authentication) {
        boolean success = learningSessionService.createSession(authentication.getName());
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getSessionStatus(Authentication authentication) {
        boolean hasSession = learningSessionService.hasActiveSession(authentication.getName());
        return ResponseEntity.ok(Map.of("active", hasSession));
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translateWord(@RequestBody Map<String, String> request,Authentication authentication){

        try {
            String userEmail = authentication.getName();
            String prompt = request.get("prompt");

            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Słowo nie może być puste"));
            }

            Translation translation = learningSessionService.translateAndAddWord(userEmail, prompt);
            return ResponseEntity.ok(translation);
        } catch (SessionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Wystąpił błąd podczas tłumaczenia"));
        }

    }

    @GetMapping("/translations")
    public ResponseEntity<List<Translation>> getTranslations(Authentication authentication) {
        List<Translation> translations = learningSessionService.getTranslations(authentication.getName());
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPdf(Authentication authentication) {
        try {
            String userEmail = authentication.getName();

            if (!learningSessionService.hasActiveSession(userEmail)) {
                return ResponseEntity.badRequest().body(null);
            }

            byte[] pdfBytes = learningSessionService.generatePdf(userEmail);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            learningSessionService.endSession(userEmail);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vocabulary.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/end")
    public ResponseEntity<Map<String, Boolean>> endSession(Authentication authentication) {
        learningSessionService.endSession(authentication.getName());
        return ResponseEntity.ok(Map.of("success", true));
    }


}
