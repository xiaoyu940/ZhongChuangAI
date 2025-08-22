package zc.ai.service.example;

import java.util.*;
import java.util.stream.Collectors;

public class VectorUtils {

    // 文本向量化方法
    public static List<Double> textToVector(String text) {
        List<Double> vector = new ArrayList<>();
        String[] words = text.split("\\s+");
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            word = word.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "").toLowerCase();
            if (!word.isEmpty() && word.length() > 1) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // 生成固定长度的向量（128维）
        for (int i = 0; i < 128; i++) {
            if (i < wordCount.size()) {
                String word = (String) wordCount.keySet().toArray()[i];
                vector.add(wordCount.get(word).doubleValue());
            } else {
                vector.add(0.0);
            }
        }

        return normalizeVector(vector);
    }

    // 向量归一化
    private static List<Double> normalizeVector(List<Double> vector) {
        double sum = vector.stream().mapToDouble(d -> d * d).sum();
        double magnitude = Math.sqrt(sum);

        if (magnitude > 0) {
            return vector.stream()
                    .map(d -> d / magnitude)
                    .collect(Collectors.toList());
        }
        return vector;
    }

    // 计算余弦相似度
    public static double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 将向量转换为字符串表示
    public static String vectorToString(List<Double> vector) {
        return vector.stream()
                .map(d -> String.format("%.6f", d))
                .collect(Collectors.joining(","));
    }
}