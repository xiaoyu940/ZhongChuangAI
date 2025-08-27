package zc.ai.service.keystore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvToJsonConverter {

    public static void main(String[] args) {


        String inputCsvPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\pdf-meta.csv";
        String outputJsonPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\vctor.json";

        try {
            List<EmbeddingItem> items = convertCsvToEmbeddingItems(inputCsvPath);
            saveAsJson(items, outputJsonPath);
            System.out.println("转换成功！生成文件: " + outputJsonPath);
            System.out.println("共转换了 " + items.size() + " 条记录");
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<EmbeddingItem> convertCsvToEmbeddingItems(String csvFilePath) throws IOException {
        List<EmbeddingItem> items = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                EmbeddingItem item = new EmbeddingItem();

                // 构建文本内容：合并三个summary字段
                StringBuilder textBuilder = new StringBuilder();
                if (csvRecord.isSet("summary1")) {
                    textBuilder.append(csvRecord.get("summary1")).append(" ");
                }
                if (csvRecord.isSet("summary2")) {
                    textBuilder.append(csvRecord.get("summary2")).append(" ");
                }
                if (csvRecord.isSet("summary3")) {
                    textBuilder.append(csvRecord.get("summary3"));
                }
                item.setText(textBuilder.toString().trim());

                // 构建metadata
                Map<String, String> metadata = new HashMap<>();
                if (csvRecord.isSet("page_num")) {
                    metadata.put("page_num", csvRecord.get("page_num"));
                }
                if (csvRecord.isSet("tag")) {
                    metadata.put("tag", csvRecord.get("tag"));
                }
                item.setMetadata(metadata);

                items.add(item);
            }
        }

        return items;
    }

    public static void saveAsJson(List<EmbeddingItem> items, String outputPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 美化输出格式

        Map<String, Object> output = new HashMap<>();
        output.put("items", items);

        objectMapper.writeValue(new File(outputPath), output);
    }
}

