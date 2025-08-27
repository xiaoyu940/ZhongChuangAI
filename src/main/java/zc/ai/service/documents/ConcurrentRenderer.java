package zc.ai.service.documents;

import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

@Service
public class ConcurrentRenderer {
Logger logger = LoggerFactory.getLogger(ConcurrentRenderer.class);
    @Autowired
    private Configuration freeMarkerConfig;

    public ConcurrentRenderer(Configuration config) {
        this.freeMarkerConfig = config;
    }

    /**
     * 并发渲染主方法
     */
    public String renderConcurrently(String templateContent, Object dataModel)
            throws Exception {

        // 解析分段
        List<String> segments = parseSegments(templateContent);
        logger.info("发现 " + segments.size() + " 个分段");

        // 为每个分段创建独立线程
        List<Future<String>> futures = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            final String segmentContent = segments.get(i);
            final int segmentIndex = i;

            Callable<String> task = () -> {
                logger.info("开始渲染分段 " + segmentIndex);
                long startTime = System.currentTimeMillis();

                try {
                    String result = renderSegment(segmentContent, dataModel, "segment_" + segmentIndex);
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("分段 " + segmentIndex + " 渲染完成, 耗时: " + duration + "ms");
                    return result;
                } catch (Exception e) {
                    logger.error("分段 " + segmentIndex + " 渲染失败: " + e.getMessage());
                    throw e;
                }
            };

            // 为每个分段创建新线程
            FutureTask<String> futureTask = new FutureTask<>(task);
            Thread thread = new Thread(futureTask, "RenderThread-Segment-" + segmentIndex);
            thread.start();

            threads.add(thread);
            futures.add(futureTask);
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 收集结果
        StringBuilder result = new StringBuilder();
        for (Future<String> future : futures) {
            result.append(future.get());
        }

        return result.toString();
    }

    /**
     * 解析分段
     */
    private List<String> parseSegments(String templateContent) {
        List<String> segments = new ArrayList<>();

        // 使用正则表达式查找分段标记
        Pattern pattern = Pattern.compile("<!--AI-SEGMENT-->");
        Matcher matcher = pattern.matcher(templateContent);

        int lastIndex = 0;
        List<Integer> splitPoints = new ArrayList<>();

        // 找到所有分段标记位置
        while (matcher.find()) {
            splitPoints.add(matcher.start());
        }

        // 如果没有分段标记，整个模板作为一个分段
        if (splitPoints.isEmpty()) {
            segments.add(templateContent);
            return segments;
        }

        // 根据分段标记切割模板
        for (int i = 0; i < splitPoints.size(); i++) {
            int start = (i == 0) ? 0 : splitPoints.get(i - 1);
            int end = splitPoints.get(i);

            String segment = templateContent.substring(start, end).trim();
            if (!segment.isEmpty()) {
                segments.add(segment);
            }
        }

        // 添加最后一段
        String lastSegment = templateContent.substring(splitPoints.get(splitPoints.size() - 1)).trim();
        if (!lastSegment.isEmpty()) {
            segments.add(lastSegment);
        }

        return segments;
    }

    /**
     * 渲染单个分段
     */
    private String renderSegment(String segmentContent, Object dataModel, String templateName)
            throws Exception {

        // 创建临时模板
        Template template = new Template(
                templateName,
                new StringReader(segmentContent),
                freeMarkerConfig
        );

        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);

        return writer.toString();
    }
}