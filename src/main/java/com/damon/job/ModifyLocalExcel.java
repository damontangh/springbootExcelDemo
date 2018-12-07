package com.damon.job;

import com.damon.entity.WebsiteTemp;
import com.damon.entity.WebsiteTempTwo;
import com.damon.excelUtil.ExcelUtil;
import com.damon.father.FatherClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ModifyLocalExcel extends FatherClass<WebsiteTemp>{

    /**
     * 读取并修改excel的值
     * @param filePath
     */
    public void readAndModifyExcel(String filePath){
        try {
            ExcelUtil excelUtil = new ExcelUtil();
            List<WebsiteTempTwo> websiteTemps = excelUtil.mapExcelToPOJO(filePath, WebsiteTempTwo.class);
            if (websiteTemps == null || websiteTemps.size() == 0) return;
            System.out.println("list size=" + websiteTemps.size());
            for (int i = 0; i < websiteTemps.size(); i++) {
                WebsiteTempTwo eachEntity = websiteTemps.get(i);
                //把旧的和新的栏目名称的网站名给截掉
                String oldPath = eachEntity.getOldChannelPath();
                String newPath = eachEntity.getNewChannelPath();
                if (oldPath != null && oldPath.length() != 0)
                    eachEntity.setOldChannelPath(eachEntity.getOldChannelPath().replace(eachEntity.getWebsiteName(),""));
                if (newPath != null && newPath.length() != 0)
                    eachEntity.setNewChannelPath(eachEntity.getNewChannelPath().replace(eachEntity.getWebsiteName(),""));
            }
            excelUtil.outputStreamThenClose(excelUtil.mapPOJOToExcel(filePath,websiteTemps),filePath);
        } catch (Exception e) {
            log.error(String.format("readAndModifyExcel error,msg:[%s]",e));
        }
    }

    /**
     * 对excel进行查重
     * @param filePath
     */
    public void checkDuplication(String filePath){
        ExcelUtil excelUtil = new ExcelUtil();
        List<WebsiteTemp> websiteTemps = null;
        try {
            websiteTemps = excelUtil.mapExcelToPOJO(filePath, WebsiteTemp.class);
            if (websiteTemps == null || websiteTemps.size() == 0) return;
            System.out.println("原始映射list size=" + websiteTemps.size());
            Set<WebsiteTemp> newList = deleteDuplication(websiteTemps);
            System.out.println("排重后list size=" + newList.size());
        } catch (Exception e) {
            log.error(String.format("readAndModifyExcel error,msg:[%s]",e));
        }

    }

    public void findDuplicatedData(String filePath){
        ExcelUtil excelUtil = new ExcelUtil();
        List<WebsiteTemp> websiteTemps = null;
        try {
            websiteTemps = excelUtil.mapExcelToPOJO(filePath, WebsiteTemp.class);
            if (websiteTemps == null || websiteTemps.size() == 0) return;
            System.out.println("原始映射list size=" + websiteTemps.size());
            List<WebsiteTemp> newList = findDuplication(websiteTemps);
            System.out.println("重复的数据量list size=" + newList.size());
            System.out.println(String.format("以下是重复项:"));
            if (newList != null && newList.size() != 0){
                for (int i = 0; i < newList.size(); i++) {
                    WebsiteTemp temp = newList.get(i);
                    System.out.println(String.format("网站名:[%s],栏目名:[%s],别名:[%s]",temp.getWebsiteName(),
                            temp.getOldChannelPath(),temp.getChannel()));
                }
            }
        } catch (Exception e) {
            log.error(String.format("readAndModifyExcel error,msg:[%s]",e));
        }
    }

}
