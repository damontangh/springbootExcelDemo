package com.damon.main.controller;

import com.damon.main.entity.WebsiteChannel;
import com.damon.main.excelUtil.ExcelUtil;
import com.damon.main.excelUtil.Person;
import com.damon.main.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


@Controller
@RequestMapping(value = "/excel")
public class ExcelController {
    @Autowired
    ExcelService excelService;

    @RequestMapping(value = "/mapExcel")
    @ResponseBody
    public String mapUploadedExcelToListPOJO(@RequestParam("excelFile") MultipartFile file){
        try {
            List<Person> people = ExcelUtil.mapUploadedExcelToPOJO(file, Person.class);
            return people.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/findByWebChannel")
    @ResponseBody
    public String mapExcelFindWebChannel(@RequestParam("excelFile") MultipartFile file){
        try {
            List<WebsiteChannel> websiteChannelList = ExcelUtil.mapUploadedExcelToPOJO(file, WebsiteChannel.class);
            String result = excelService.searchByWebChannel(websiteChannelList);
            return "ddgz的结果有" + result;
        } catch (Exception e) {
            return e.toString();
        }
    }

    @RequestMapping(value = "/upload")
    public String uploadExcel(){
        return "importExcel";
    }

    @RequestMapping(value = "/download")
    public void downLoadFile(HttpServletResponse response){
        try {
            excelService.downLoadFile(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/downloadExcel")
    public void downLoadExcelFile(HttpServletResponse response){
        try {
            excelService.downLoadExcel(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
