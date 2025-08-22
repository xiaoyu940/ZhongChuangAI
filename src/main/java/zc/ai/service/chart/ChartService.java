package zc.ai.service.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.json.impl.JSONObject;
import org.jfree.data.xy.DefaultXYZDataset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class ChartService {

    @Value("${zc.ai.service.project-dir}")
    private String projectDir = null;
    // 生成饼图PNG字节数组
    public byte[] generatePieChart() throws IOException {
        // 1. 创建数据集
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Android", 60);
        dataset.setValue("iOS", 25);
        dataset.setValue("其他", 15);

        // 2. 创建图表
        JFreeChart chart = ChartFactory.createPieChart(
                "手机操作系统占比",  // 标题
                dataset,           // 数据
                true,              // 显示图例
                true,              // 显示提示
                false              // 不生成URL
        );

        // 3. 解决中文乱码
        Font font = new Font("Microsoft YaHei", Font.BOLD, 16);
        chart.getTitle().setFont(font);
        chart.getLegend().setItemFont(font);

        // 4. 转为字节数组
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(output, chart, 600, 400);
        return output.toByteArray();
    }

    public byte[] generateOrgChart(OrgNode root) throws IOException {
        // 1. 创建模拟数据
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double[] x = {1, 2, 2, 3, 3};  // 节点X坐标
        double[] y = {5, 4, 4, 3, 3};  // 节点Y坐标
        dataset.addSeries("Org", new double[][]{x, y, new double[x.length]});

        // 2. 生成散点图模拟树状结构
        JFreeChart chart = ChartFactory.createScatterPlot(
                "组织结构图",
                "", "",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // 3. 自定义样式
        chart.getTitle().setFont(new Font("Microsoft YaHei", Font.BOLD, 16));

        // 4. 输出为PNG
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(output, chart, 600, 400);
        return output.toByteArray();
    }

    public  String pie(String jsonStr, String chartTitle, String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 将 JSON 字符串解析为 Map
            Map<String, Object> map = mapper.readValue(jsonStr, Map.class);

            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                dataset.setValue(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
            }

            // 创建饼图
            JFreeChart chart = ChartFactory.createPieChart(
                    chartTitle,   // 图表标题
                    dataset,      // 数据集
                    true,         // 是否显示图例
                    true,         // 是否生成提示
                    false         // 是否生成URL链接
            );

            File file = new File(this.projectDir + outputFile);
            // 保存为 PNG 文件
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            return file.getName();
        }catch (Exception e){

        }

        return "数据有误，生成pie图处理出错";
    }

    public  void bar(String jsonStr,
                                      String chartTitle,
                                      String categoryAxisLabel,
                                      String valueAxisLabel,
                                      String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // JSON 转换为 Map
        Map<String, Object> map = mapper.readValue(jsonStr, Map.class);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            dataset.addValue(Double.parseDouble(entry.getValue().toString()), "数量", entry.getKey());
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle,         // 图表标题
                categoryAxisLabel,  // 分类轴标题 (X轴)
                valueAxisLabel,     // 数值轴标题 (Y轴)
                dataset             // 数据集
        );

        // 保存为 PNG 文件
        ChartUtils.saveChartAsPNG(new File(outputFile), chart, 800, 600);
    }

    public  void line(String jsonStr,
                                       String chartTitle,
                                       String categoryAxisLabel,
                                       String valueAxisLabel,
                                       String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // JSON 转换为 Map
        Map<String, Object> map = mapper.readValue(jsonStr, Map.class);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            dataset.addValue(Double.parseDouble(entry.getValue().toString()), "数量", entry.getKey());
        }

        // 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
                chartTitle,         // 图表标题
                categoryAxisLabel,  // 分类轴标题 (X轴)
                valueAxisLabel,     // 数值轴标题 (Y轴)
                dataset             // 数据集
        );

        // 保存为 PNG 文件
        ChartUtils.saveChartAsPNG(new File(outputFile), chart, 800, 600);
    }
}