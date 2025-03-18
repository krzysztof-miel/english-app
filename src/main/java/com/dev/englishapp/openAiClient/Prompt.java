package com.dev.englishapp.openAiClient;

public enum Prompt {
    FIVE("Wygeneruj 5 losowych słówek po angielsku ze zróżnicowenego poziomu, " +
            "podaj tłumaczenie oraz przykład użycia każdego z nich."),
    EIGHT("Wygeneruj 8 losowych słówek po angielsku ze zróżnicowenego poziomu," +
            "podaj tłumaczenie oraz przykład użycia każdego z nich."),

    TEN("Wygeneruj 10 losowych słówek po angielsku ze zróżnicowenego poziomu, " +
            "ostatnie ma być phrasal verb, " +
            "podaj tłumaczenie oraz przykład użycia każdego z nich."),

    TRANSLATE("Translate the word or phrase into Polish. " +
            "Your answer should only be the translation of the given phrase. " +
            "If given word is not correct your answer should be: - . " +
            "Here is the phrase to translate: ");



    private final String prompt;

    Prompt(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
