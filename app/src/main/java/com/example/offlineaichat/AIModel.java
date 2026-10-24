package com.example.offlineaichat;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AIModel {

    static class KnowledgeEntry {
        String question;
        String answer;

        KnowledgeEntry(String q, String a) {
            this.question = q;
            this.answer = a;
        }
    }

    private final List<KnowledgeEntry> knowledge = new ArrayList<>();

    public void load(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String q = obj.optString("question", "");
                String a = obj.optString("answer", "");

                if (!q.isEmpty() && !a.isEmpty()) {
                    knowledge.add(new KnowledgeEntry(q, a));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String respond(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "Aap kuch likho. Main reply dunga.";
        }

        String cleanInput = normalize(userInput);

        KnowledgeEntry bestMatch = null;
        double bestScore = 0.0;

        for (KnowledgeEntry entry : knowledge) {
            double score = similarity(cleanInput, normalize(entry.question));
            if (score > bestScore) {
                bestScore = score;
                bestMatch = entry;
            }
        }

        if (bestMatch != null && bestScore > 0.22) {
            return bestMatch.answer;
        }

        return "Maine is sawal ka answer local JSON data me nahi paya. Aur data add karo.";
    }

    private String normalize(String text) {
        if (text == null) return "";
        text = text.toLowerCase().trim();
        text = text.replaceAll("[^a-z0-9\\s]", "");
        return text;
    }

    private double similarity(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        String[] aWords = a.split("\\s+");
        String[] bWords = b.split("\\s+");

        int matches = 0;
        for (String wa : aWords) {
            for (String wb : bWords) {
                if (wa.equals(wb)) {
                    matches++;
                }
            }
        }

        int total = aWords.length + bWords.length;
        if (total == 0) return 0.0;

        return (double) matches / (double) total;
    }
}
