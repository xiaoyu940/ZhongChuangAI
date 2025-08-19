package zc.ai.service.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ChartController {

    @Autowired
    private ChartService chartService;

    // 返回PNG图片
    @GetMapping(value = "/api/chart/pie", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getChart() throws IOException {
        return chartService.generatePieChart();
    }

    @GetMapping(value = "/api/chart/org", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getOrgChart() throws IOException {
        OrgNode root = buildOrgTree(); // 获取组织结构数据
        return chartService.generateOrgChart(root);
    }

    public OrgNode buildOrgTree() {
        OrgNode root = new OrgNode("CEO", "张伟", new ArrayList<>());

        OrgNode cto = new OrgNode("CTO", "李强", new ArrayList<>());
        cto.getChildren().add(new OrgNode("Dev", "王码农", null));
        cto.getChildren().add(new OrgNode("QA", "赵测试", null));

        OrgNode cfo = new OrgNode("CFO", "刘会计", new ArrayList<>());
        cfo.getChildren().add(new OrgNode("Finance", "钱出纳", null));

        root.getChildren().add(cto);
        root.getChildren().add(cfo);

        return root;
    }
}