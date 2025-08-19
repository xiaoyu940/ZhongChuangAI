package zc.ai.service.chart;

import java.util.ArrayList;
import java.util.List;

public class OrgNode {
    public <E> OrgNode(String pos, String name, ArrayList<OrgNode> children) {
        this.name=name;
        this.title=pos;
        this.children= children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OrgNode> getChildren() {
        return children;
    }

    public void setChildren(List<OrgNode> children) {
        this.children = children;
    }

    private String name;
    private String title;
    private List<OrgNode> children;

    // 构造方法/getters/setters
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