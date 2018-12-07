package com.damon.job;

import com.damon.entity.WebsiteIdUrl;
import com.damon.entity.WebsiteUrl;
import com.damon.excelUtil.ExcelUtil;
import com.trs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class CompareWebUrlWithMysql {

    public void checkData(String mysqlFile) throws Exception {
        List<WebsiteIdUrl> mysqlList = ExcelUtil.mapExcelToPOJO(mysqlFile,WebsiteIdUrl.class);
        for (int i = 0; i < mysqlList.size(); i++) {
            WebsiteIdUrl websiteIdUrl = mysqlList.get(i);
            if ("http://www.drc.gov.cn/".equals(websiteIdUrl.getUrl())){
                System.out.println("有，。。");
            }
        }

    }

    public void compareWebsite(String compareFile,String mysqlFile) throws Exception {
        List<WebsiteUrl> compareList = ExcelUtil.mapExcelToPOJO(compareFile, WebsiteUrl.class);
        List<WebsiteIdUrl> mysqlList = ExcelUtil.mapExcelToPOJO(mysqlFile,WebsiteIdUrl.class);
        log.info("对比表size=" + compareList.size());
        log.info("mysql表size=" + mysqlList.size());
        if (compareList != null && compareList.size() != 0){
            for (int i = 0; i < compareList.size(); i++) {
                WebsiteUrl websiteEntity = compareList.get(i);
                String website = websiteEntity.getWebsite();
                if (website == null) website = "";
                //首先通过网站名比较
                for (int y = 0; y < mysqlList.size(); y++) {
                    WebsiteIdUrl mysqlEntity = mysqlList.get(y);
                    String mysqlWeb = mysqlEntity.getWebsite();
                    if (mysqlWeb == null) break;
                    if (website.equals(mysqlWeb)){
                        websiteEntity.setNote("网站名和mysql能匹配");
                        break;
                    }
                }
            }
            Workbook workbook = ExcelUtil.mapPOJOToExcel(compareFile,compareList);
            ExcelUtil.outputStreamThenClose(workbook,compareFile);
        }
    }

    public void compareUrl(String compareFile,String mysqlFile) throws Exception {
        List<WebsiteUrl> compareList = ExcelUtil.mapExcelToPOJO(compareFile, WebsiteUrl.class);
        List<WebsiteIdUrl> mysqlList = ExcelUtil.mapExcelToPOJO(mysqlFile,WebsiteIdUrl.class);
        log.info("对比表size=" + compareList.size());
        log.info("mysql表size=" + mysqlList.size());
        if (compareList != null && compareList.size() != 0){
            for (int i = 0; i < compareList.size(); i++) {
                WebsiteUrl websiteEntity = compareList.get(i);
                String website = websiteEntity.getWebsite();
                String url = websiteEntity.getUrl();
                if (url == null) url = "";
                boolean urlResult = false;
                for (int y = 0; y < mysqlList.size(); y++) {
                    WebsiteIdUrl mysqlEntity = mysqlList.get(y);
                    String mysqlWeb = mysqlEntity.getWebsite();
                    if (mysqlWeb == null) mysqlWeb = "";
                    String mysqlUrl = mysqlEntity.getUrl();
                    if (mysqlUrl == null) mysqlUrl = "";
                    if (url.equals(mysqlUrl)){
                        urlResult = true;
                        //url一致，再判断网站名是否一致
                        if (website.equals(mysqlWeb)){
                            websiteEntity.setNote("网站名与url都和mysql匹配");
                        }else {
                            websiteEntity.setNote("url和mysql匹配，网站名不匹配");
                        }
                        break;
                    }
                }
                //如果依旧是false，那么判断备注是否有信息，没信息的话那就是网站和url都不匹配的数据
                if (!urlResult){
                    if (StringUtils.isNullOrEmpty(websiteEntity.getNote()))
                        websiteEntity.setNote("网站名和url都不匹配");
                }
            }
            Workbook workbook = ExcelUtil.mapPOJOToExcel(compareFile,compareList);
            ExcelUtil.outputStreamThenClose(workbook,compareFile);
        }

    }

    public void compare(String compareFile,String mysqlFile) throws Exception {
        List<WebsiteUrl> compareList = ExcelUtil.mapExcelToPOJO(compareFile, WebsiteUrl.class);
        List<WebsiteIdUrl> mysqlList = ExcelUtil.mapExcelToPOJO(mysqlFile,WebsiteIdUrl.class);
        log.info("对比表size=" + compareList.size());
        log.info("mysql表size=" + mysqlList.size());
        if (compareList != null && compareList.size() != 0){
            for (int i = 0; i < compareList.size(); i++) {
                WebsiteUrl websiteEntity = compareList.get(i);
                String website = websiteEntity.getWebsite();
                if (website == null) website = "";
                String url = websiteEntity.getUrl();
                if (url == null) url = "";
                boolean result = false;
                //首先通过网站名比较
                for (int y = 0; y < mysqlList.size(); y++) {
                    WebsiteIdUrl mysqlEntity = mysqlList.get(y);
                    String mysqlWeb = mysqlEntity.getWebsite();
                    if (mysqlWeb == null) break;
                    else mysqlWeb = mysqlWeb.trim();
                    if (website.equals(mysqlWeb)){
                        result = true;
                        websiteEntity.setNote("网站名和mysql能匹配");
                        break;
                    }
                }
                if("http://www.drc.gov.cn/".equals(url)){
                    System.out.println("一次阻塞");
                }

                //然后如果result依旧是false，说明网站名没能和mysql的数据匹配上，那么就通过url再比一遍
                if (!result){
                    //这里也要一个boolean变量，用于判断通过url是否有匹配上
                    boolean urlResult = false;
                    for (int y = 0; y < mysqlList.size(); y++) {
                        WebsiteIdUrl mysqlEntity = mysqlList.get(y);
                        String mysqlWeb = mysqlEntity.getWebsite();
                        if (mysqlWeb == null) mysqlWeb = "";
                        String mysqlUrl = mysqlEntity.getUrl();
                        if (mysqlUrl == null) break;
                        if ("国务院发展研究中心".equals(mysqlWeb)){
                            System.out.println("2次阻塞");
                        }
                        if (url.equals(mysqlUrl)){
                            urlResult = true;
                            //url一致，再判断网站名是否一致
                            if (website.equals(mysqlWeb)){
                                websiteEntity.setNote("网站名与url都和mysql匹配");
                            }else {
                                websiteEntity.setNote("url和mysql匹配，网站名不匹配");
                            }
                            break;
                        }
                    }
                    //如果依旧是false，说明url匹配不上，由于最上面已经先匹配了网站，那现在通过url也匹配不上，那就是网站和url都匹配失败
                    if (!urlResult){
                        websiteEntity.setNote("网站名和url都不匹配");
                    }
                }

            }
            //此时已经对实体的备注做了赋值，然后回写到本地
            Workbook workbook = ExcelUtil.mapPOJOToExcel("C:\\Users\\lenovo\\Desktop\\compare\\结果.xls",compareList);
            ExcelUtil.outputStreamThenClose(workbook,"C:\\Users\\lenovo\\Desktop\\compare\\结果.xls");
        }

    }

}
