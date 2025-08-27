package zc.ai.service.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于固定目录结构为PDF页码打标签的工具
 */
public class PdfTaggerByFixedToc {
static Logger logger = LoggerFactory.getLogger(PdfTaggerByFixedToc.class);
    /**
     * 目录项类，包含章节名称和起始页码
     */
    static class TocItem {
        private String title;
        private int startPage;

        public TocItem(String title, int startPage) {
            this.title = title;
            this.startPage = startPage;
        }

        public String getTitle() {
            return title;
        }

        public int getStartPage() {
            return startPage;
        }

        @Override
        public String toString() {
            return String.format("%s (起始页: %d)", title, startPage);
        }
    }

    /**
     * 页面标签类
     */
    static class PageTag {
        private int pageNum;
        private String tag;

        public PageTag(int pageNum, String tag) {
            this.pageNum = pageNum;
            this.tag = tag;
        }

        public int getPageNum() {
            return pageNum;
        }

        public String getTag() {
            return tag;
        }

        @Override
        public String toString() {
            return String.format("第%03d页: %s", pageNum, tag);
        }
    }

    // 预定义的目录结构（根据您提供的目录整理）
    private static final List<TocItem> TOC_ITEMS = createTocList();

    /**
     * 创建固定的目录列表
     */
    private static List<TocItem> createTocList() {
        List<TocItem> toc = new ArrayList<>();

        // 根据您提供的目录结构添加所有项目
        toc.add(new TocItem("中国银行简介", 1));
        toc.add(new TocItem("荣誉与奖项", 2));
        toc.add(new TocItem("释义", 4));
        toc.add(new TocItem("重要提示", 5));
        toc.add(new TocItem("财务摘要", 6));
        toc.add(new TocItem("公司基本情况", 9));
        toc.add(new TocItem("董事长致辞", 10));
        toc.add(new TocItem("行长致辞", 12));
        toc.add(new TocItem("管理层讨论与分析 - 综合财务回顾", 14));
        toc.add(new TocItem("管理层讨论与分析 - 业务回顾", 28));
        toc.add(new TocItem("管理层讨论与分析 - 风险管理", 54));
        toc.add(new TocItem("管理层讨论与分析 - 资本管理", 64));
        toc.add(new TocItem("管理层讨论与分析 - 机构管理、人力资源开发与管理", 65));
        toc.add(new TocItem("管理层讨论与分析 - 展望", 68));
        toc.add(new TocItem("环境、社会、治理 - 环境责任", 69));
        toc.add(new TocItem("环境、社会、治理 - 社会责任", 74));
        toc.add(new TocItem("环境、社会、治理 - 治理责任", 76));
        toc.add(new TocItem("股份变动和股东情况", 78));
        toc.add(new TocItem("董事、监事、高级管理人员", 84));
        toc.add(new TocItem("公司治理", 95));
        toc.add(new TocItem("董事会报告", 108));
        toc.add(new TocItem("监事会报告", 115));
        toc.add(new TocItem("重要事项", 118));
        toc.add(new TocItem("董事、监事、高级管理人员关于年度报告的确认意见", 120));
        toc.add(new TocItem("审计报告", 121));
        toc.add(new TocItem("财务报表", 129));
        toc.add(new TocItem("合并及母公司资产负债表", 131));
        toc.add(new TocItem("合并及母公司利润表", 133));
        toc.add(new TocItem("合并所有者权益变动表", 135));
        toc.add(new TocItem("母公司所有者权益变动表", 137));
        toc.add(new TocItem("合并及母公司现金流量表", 139));
        toc.add(new TocItem("财务报表附注 - 一、 基本情况", 141));
        toc.add(new TocItem("财务报表附注 - 二、 财务报表的编制基础", 141));
        toc.add(new TocItem("财务报表附注 - 三、 遵循企业会计准则的声明", 142));
        toc.add(new TocItem("财务报表附注 - 四、 重要会计政策", 142));
        toc.add(new TocItem("财务报表附注 - 五、 在执行会计政策中所作出的重要会计估计和判断", 170));
        toc.add(new TocItem("财务报表附注 - 六、 税项", 173));
        // 七、 财务报表主要项目附注
        toc.add(new TocItem("财务报表附注 - 七、1 现金及存放中央银行款项", 174));
        toc.add(new TocItem("财务报表附注 - 七、2 存放同业款项", 175));
        toc.add(new TocItem("财务报表附注 - 七、3 拆出资金", 176));
        toc.add(new TocItem("财务报表附注 - 七、4 衍生金融工具及套期会计", 177));
        toc.add(new TocItem("财务报表附注 - 七、5 买入返售金融资产", 190));
        toc.add(new TocItem("财务报表附注 - 七、6 发放贷款和垫款", 191));
        toc.add(new TocItem("财务报表附注 - 七、7 金融投资", 199));
        toc.add(new TocItem("财务报表附注 - 七、8 长期股权投资", 208));
        toc.add(new TocItem("财务报表附注 - 七、9 投资性房地产", 210));
        toc.add(new TocItem("财务报表附注 - 七、10 固定资产", 211));
        toc.add(new TocItem("财务报表附注 - 七、11 在建工程", 215));
        toc.add(new TocItem("财务报表附注 - 七、12 使用权资产", 216));
        toc.add(new TocItem("财务报表附注 - 七、13 无形资产", 218));
        toc.add(new TocItem("财务报表附注 - 七、14 商誉", 220));
        toc.add(new TocItem("财务报表附注 - 七、15 其他资产", 220));
        toc.add(new TocItem("财务报表附注 - 七、16 资产减值准备", 222));
        toc.add(new TocItem("财务报表附注 - 七、17 向中央银行借款", 226));
        toc.add(new TocItem("财务报表附注 - 七、18 同业及其他金融机构存放款项", 226));
        toc.add(new TocItem("财务报表附注 - 七、19 拆入资金", 227));
        toc.add(new TocItem("财务报表附注 - 七、20 交易性金融负债", 227));
        toc.add(new TocItem("财务报表附注 - 七、21 卖出回购金融资产款", 227));
        toc.add(new TocItem("财务报表附注 - 七、22 吸收存款", 228));
        toc.add(new TocItem("财务报表附注 - 七、23 应付职工薪酬", 230));
        toc.add(new TocItem("财务报表附注 - 七、24 应交税费", 233));
        toc.add(new TocItem("财务报表附注 - 七、25 预计负债", 234));
        toc.add(new TocItem("财务报表附注 - 七、26 租赁负债", 234));
        toc.add(new TocItem("财务报表附注 - 七、27 应付债券", 235));
        toc.add(new TocItem("财务报表附注 - 七、28 递延所得税", 240));
        toc.add(new TocItem("财务报表附注 - 七、29 其他负债", 244));
        toc.add(new TocItem("财务报表附注 - 七、30 股票增值权计划", 244));
        toc.add(new TocItem("财务报表附注 - 七、31 股本、资本公积及其他权益工具", 245));
        toc.add(new TocItem("财务报表附注 - 七、32 盈余公积、一般风险准备及未分配利润", 250));
        toc.add(new TocItem("财务报表附注 - 七、33 少数股东权益", 252));
        toc.add(new TocItem("财务报表附注 - 七、34 利息净收入", 253));
        toc.add(new TocItem("财务报表附注 - 七、35 手续费及佣金净收入", 254));
        toc.add(new TocItem("财务报表附注 - 七、36 投资收益", 254));
        toc.add(new TocItem("财务报表附注 - 七、37 公允价值变动收益", 255));
        toc.add(new TocItem("财务报表附注 - 七、38 汇兑收益", 255));
        toc.add(new TocItem("财务报表附注 - 七、39 其他业务收入", 255));
        toc.add(new TocItem("财务报表附注 - 七、40 税金及附加", 256));
        toc.add(new TocItem("财务报表附注 - 七、41 业务及管理费", 256));
        toc.add(new TocItem("财务报表附注 - 七、42 信用减值损失", 258));
        toc.add(new TocItem("财务报表附注 - 七、43 其他业务成本", 258));
        toc.add(new TocItem("财务报表附注 - 七、44 所得税费用", 259));
        toc.add(new TocItem("财务报表附注 - 七、45 其他综合收益", 260));
        toc.add(new TocItem("财务报表附注 - 七、46 每股收益", 263));
        toc.add(new TocItem("财务报表附注 - 七、47 合并范围的变动", 263));
        toc.add(new TocItem("财务报表附注 - 七、48 现金流量表补充资料", 264));
        toc.add(new TocItem("财务报表附注 - 七、49 金融资产转让", 265));
        toc.add(new TocItem("财务报表附注 - 七、50 在结构化主体中的权益", 266));
        toc.add(new TocItem("财务报表附注 - 七、51 金融资产和金融负债的抵销", 269));
        toc.add(new TocItem("财务报表附注 - 七、52 资产负债表日后事项", 270));
        toc.add(new TocItem("财务报表附注 - 八、 分部报告", 270));
        // 九、 或有事项及承诺
        toc.add(new TocItem("财务报表附注 - 九、1 法律诉讼及仲裁", 276));
        toc.add(new TocItem("财务报表附注 - 九、2 抵质押资产", 276));
        toc.add(new TocItem("财务报表附注 - 九、3 接受的抵质押物", 276));
        toc.add(new TocItem("财务报表附注 - 九、4 资本性承诺", 277));
        toc.add(new TocItem("财务报表附注 - 九、5 经营租赁", 277));
        toc.add(new TocItem("财务报表附注 - 九、6 国债兑付承诺", 278));
        toc.add(new TocItem("财务报表附注 - 九、7 信用承诺", 278));
        toc.add(new TocItem("财务报表附注 - 九、8 证券承销承诺", 279));
        toc.add(new TocItem("财务报表附注 - 十、 关联交易", 280));
        // 十一、 金融风险管理
        toc.add(new TocItem("财务报表附注 - 十一、1 概述", 288));
        toc.add(new TocItem("财务报表附注 - 十一、2 信用风险", 288));
        toc.add(new TocItem("财务报表附注 - 十一、3 市场风险", 321));
        toc.add(new TocItem("财务报表附注 - 十一、4 流动性风险", 334));
        toc.add(new TocItem("财务报表附注 - 十一、5 公允价值", 345));
        toc.add(new TocItem("财务报表附注 - 十一、6 资本管理", 353));
        toc.add(new TocItem("财务报表附注 - 十一、7 保险风险", 356));
        toc.add(new TocItem("财务报表附注 - 十二、 扣除非经常性损益的净利润", 357));
        toc.add(new TocItem("财务报表附注 - 十三、 净资产收益率及每股收益计算表", 358));
        toc.add(new TocItem("股东参考资料", 361));
        toc.add(new TocItem("组织架构", 363));
        toc.add(new TocItem("机构名录", 364));

        // 添加一个虚拟的结束项，用于处理最后一页
        toc.add(new TocItem("文档结束", 365));

        return toc;
    }

    /**
     * 根据页码找到对应的目录项标签
     * @param pageNum 页码
     * @return 对应的章节标签
     */
    private static String findTagForPage(int pageNum) {
        // 遍历目录项，找到第一个起始页码大于当前页码的项
        for (int i = 1; i < TOC_ITEMS.size(); i++) {
            TocItem currentItem = TOC_ITEMS.get(i - 1);
            TocItem nextItem = TOC_ITEMS.get(i);

            if (pageNum >= currentItem.getStartPage() && pageNum < nextItem.getStartPage()) {
                return currentItem.getTitle();
            }
        }

        // 处理最后一页的情况
        TocItem lastItem = TOC_ITEMS.get(TOC_ITEMS.size() - 2); // 倒数第二项是最后一个有效目录项
        if (pageNum >= lastItem.getStartPage()) {
            return lastItem.getTitle();
        }

        return "未知章节";
    }

    /**
     * 为PDF文档的所有页面生成标签
     * @param pdfFilePath PDF文件路径
     * @param outputJsonPath 输出JSON文件路径
     */
    public static void generatePageTags(String pdfFilePath, String outputJsonPath) throws IOException {
        List<PageTag> pageTags = new ArrayList<>();

        // 获取PDF总页数
        int totalPages = 0;
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            totalPages = document.getNumberOfPages();
            logger.info("PDF总页数: " + totalPages);
        }

        // 为每一页生成标签
        for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
            String tag = findTagForPage(pageNum);
            pageTags.add(new PageTag(pageNum, tag));

            if (pageNum <= 10 || pageNum % 50 == 0 || pageNum == totalPages) {
                logger.info("第" + pageNum + "页: " + tag);
            }
        }

        // 输出到JSON文件
        if (outputJsonPath != null && !outputJsonPath.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(outputJsonPath), pageTags);
            logger.info("标签已导出至: " + outputJsonPath);
        }
    }

    public static void main(String[] args) {
        try {
            String pdfPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\中国银行公司年报.pdf"; // 替换为您的PDF路径
            String outputPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\page_tags.json";

            generatePageTags(pdfPath, outputPath);

            logger.info("\n标签生成完成！");

        } catch (IOException e) {
            logger.info("处理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}