package com.damon.main.startJob;

import com.damon.main.job.CompareWebUrlWithMysql;
import com.damon.main.job.FindNullOrMultipleData;
import com.damon.main.job.ModifyLocalExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
public class StartJob {

    @Autowired
    ModifyLocalExcel modifyLocalExcel;

    @Autowired
    FindNullOrMultipleData findNullOrMultipleData;

    @Autowired
    CompareWebUrlWithMysql compareWebUrlWithMysql;

    @Scheduled(fixedDelay = 1000*1000)
    public void run() throws Exception {
//        String filePath = "C:\\Users\\lenovo\\Desktop\\modify\\查重.xls";
//        String filepath = "C:\\Users\\lenovo\\Desktop\\compare\\媒体云第二批网站已收集部分-20180224.xlsx";
        String filepath = "C:\\\\Users\\\\lenovo\\\\Desktop\\\\compare\\compare.xls";
        String mysqlFilepath = "C:\\Users\\lenovo\\Desktop\\compare\\website.xls";
        compareWebUrlWithMysql.compareWebsite(filepath,mysqlFilepath);
        compareWebUrlWithMysql.compareUrl(filepath,mysqlFilepath);
//        compareWebUrlWithMysql.checkData(mysqlFilepath);
        //        modifyLocalExcel.readAndModifyExcel(filePath);
//        modifyLocalExcel.checkDuplication(filePath);
//        findNullOrMultipleData.searchAndCompare(filePath);
//        modifyLocalExcel.findDuplicatedData(filePath);
        System.out.println("END");
    }

}
