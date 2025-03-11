package com.dev.englishapp.service;

import com.dev.englishapp.exception.SessionNotFoundException;
import com.dev.englishapp.model.Translation;
import com.dev.englishapp.openAiClient.OpenAiClient;
import com.dev.englishapp.openAiClient.Prompt;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LearningSessionServiceImpl implements LearningSessionService{

    private final Map<String, List<Translation>> userSessions = new ConcurrentHashMap<>();

    private OpenAiClient openAiClient;

    public LearningSessionServiceImpl(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public boolean createSession(String userEmail) {
        userSessions.put(userEmail, new ArrayList<>());
        return true;
    }

    public boolean hasActiveSession(String userEmail) {
        return userSessions.containsKey(userEmail);
    }

    public Translation translateAndAddWord(String userEmail, String originalWord) throws IOException {
        if (!hasActiveSession(userEmail)) {
            throw new SessionNotFoundException("Brak aktywnej sesji nauki");
        }

        String prompt = Prompt.TRANSLATE.getPrompt() + originalWord;

        System.out.println(prompt);

        String translatedWord = openAiClient.getResponse(prompt);

        Translation translation = new Translation();
        translation.setOriginalWord(originalWord);
        translation.setTranslatedWord(translatedWord);

        userSessions.get(userEmail).add(translation);

        return translation;
    }



    public List<Translation> getTranslations(String userEmail) {
        if (!hasActiveSession(userEmail)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(userSessions.get(userEmail));
    }

    @Override
    public byte[] generatePdf(String userEmail) throws IOException {
        List<Translation> translations = getTranslations(userEmail);

        if (translations.isEmpty()) {
            throw new RuntimeException("Nie można wygenerować PDF dla pustej sesji");
        }

        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font normalFont = new Font(baseFont, 12);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);

            Paragraph title = new Paragraph("Lista przetłumaczonych słówek", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph date = new Paragraph("Data: " + java.time.LocalDate.now(), normalFont);
            date.setSpacingAfter(20);
            document.add(date);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            float[] columnWidths = {1f, 1f};
            table.setWidths(columnWidths);

            PdfPCell headerCell1 = new PdfPCell(new Phrase("Słówko angielskie", headerFont));
            PdfPCell headerCell2 = new PdfPCell(new Phrase("Tłumaczenie polskie", headerFont));

            headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell1.setPadding(8);
            headerCell2.setPadding(8);
            headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(headerCell1);
            table.addCell(headerCell2);

            for (Translation translation : translations) {

                String originalWord = translation.getOriginalWord() != null ? translation.getOriginalWord() : "";
                String translatedWord = translation.getTranslatedWord() != null ? translation.getTranslatedWord() : "";

                PdfPCell cell1 = new PdfPCell(new Phrase(translation.getOriginalWord(), normalFont));
                PdfPCell cell2 = new PdfPCell(new Phrase(translation.getTranslatedWord(), normalFont));

                cell1.setPadding(8);
                cell2.setPadding(8);

                table.addCell(cell1);
                table.addCell(cell2);
            }

            document.add(table);

            Paragraph footer = new Paragraph("Wygenerowano przez aplikację: EnglishApp", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Błąd podczas generowania PDF", e);
        }
    }

    @Override
    public void endSession(String userEmail) {

        userSessions.remove(userEmail);

    }
}
