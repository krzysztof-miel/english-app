package com.dev.englishapp.openAiClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class OpenAiClient {

    @Value("${openai.url}")
    private String OPEN_AI_URL;

    @Value("${openai.key}")
    private String OPEN_AI_KEY;

    private URL url;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public String getResponseJson(String prompt) throws IOException {

        try {
            url = new URL(OPEN_AI_URL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + OPEN_AI_URL, e);
        }


        HttpPost httpPost = new HttpPost(String.valueOf(url));
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Authorization", "Bearer " + OPEN_AI_KEY);


        String json = String.format("{\"model\": \"gpt-3.5-turbo\"," +
                        " \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]," +
                        " \"max_tokens\": 1000}",
                prompt.replaceAll("\"", "\\\""));

        httpPost.setEntity(new StringEntity(json, StandardCharsets.UTF_8));


        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {

                System.out.println("Non-OK response received: " +  statusCode);
            }

            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println("Response body: " + responseBody);

            return responseBody;
        } catch (IOException e) {
            System.out.println("Error while executing request to OpenAI API: " + e);
            throw e;
        }
    }

    public String getResponse(String prompt) throws IOException {

        String responseBody = getResponseJson(prompt);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);

        String content = rootNode.path("choices").get(0).path("message").path("content").asText();

        return content;
    }

}
