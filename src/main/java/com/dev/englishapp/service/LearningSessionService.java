package com.dev.englishapp.service;

import com.dev.englishapp.model.Translation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


public interface LearningSessionService {

    boolean createSession(String userEmail);

    boolean hasActiveSession(String userEmail);

    Translation translateAndAddWord(String userEmail, String originalWord) throws IOException;

    List<Translation> getTranslations(String userEmail);

    byte[] generatePdf(String userEmail) throws IOException;

    void endSession(String userEmail);
}
