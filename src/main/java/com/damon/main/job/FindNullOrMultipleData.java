package com.damon.main.job;

import com.damon.main.entity.Website;
import com.damon.main.entity.WebsiteTemp;
import com.damon.main.excelUtil.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
@Slf4j
public class FindNullOrMultipleData {

    @PersistenceContext
    EntityManager entityManager;
    /**
     *根据excel的数据，去检索mysql，记录下查不到的，或者查到的数据有多条的
     */
    public void searchAndCompare(String filePath){
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            List<WebsiteTemp> websiteTemps = excelUtil.mapExcelToPOJO(filePath, WebsiteTemp.class);
            for (int i = 0; i < websiteTemps.size(); i++) {
                WebsiteTemp each = websiteTemps.get(i);
                String website = each.getWebsiteName();
                String channelPath = each.getOldChannelPath();
                if (channelPath == null)
                    channelPath = "";
                channelPath = channelPath.replace(website,"");
                String channel = each.getNewChannelPath();
                //这是根据3个条件去查询
//                Query query = entityManager.createQuery("select w from Website w where w.website = ?1 and w.channel = ?2  and w.channelPath = ?3");
                //这是根据2个条件去查询
                Query query = entityManager.createQuery("select w from Website w where w.website = ?1 and w.channel = ?2");
                query.setParameter(1,website);
                query.setParameter(2,channel);
//                query.setParameter(3,channelPath);
                List<Website> resultList = query.getResultList();
                if (resultList == null || resultList.size() == 0)
                    System.out.println(String.format("查不到数据，网站名:[%s],栏目名:[%s%s],别名:[%s]",website,website,channelPath,channel));
                if (resultList != null && resultList.size() > 1)
                    System.out.println(String.format("查到[%s]条数据，网站名:[%s],栏目名:[%s%s],别名:[%s]",resultList.size(),website,website,channelPath,channel));
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

}
