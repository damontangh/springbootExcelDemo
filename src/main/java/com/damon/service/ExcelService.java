package com.damon.service;

import com.damon.entity.Website;
import com.damon.entity.WebsiteChannel;
import com.damon.entity.WebsiteSourceClassifyBean;
import com.damon.excelUtil.ExcelUtil;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    @Qualifier(value = "entityManagerPrimary")
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    public String searchByWebChannel(List<WebsiteChannel> websiteChannels) throws Exception {
        if (websiteChannels == null || websiteChannels.size() == 0) return null;
        int ddgzNum = 0;
        for (int i = 0; i < websiteChannels.size(); i++) {
            WebsiteChannel eachOne = websiteChannels.get(i);
            String website = eachOne.getWebsite().trim();
            String channel = eachOne.getChannel().trim();
            String channelPath = eachOne.getChannelPath().replace(website,"").trim();
            String channelResult = restTemplateResut(website,channel);
            String pathResult = restTemplateResut(website,channelPath);
            if (channelResult != null && channelResult.length() > 2){
                ObjectMapper mapper = new ObjectMapper();
                WebsiteSourceClassifyBean bean = mapper.readValue(channelResult, WebsiteSourceClassifyBean[].class)[0];
                if (bean != null){
                    if ("ddgz".equals(bean.getData_user())){
                        eachOne.setViaChannelResult("ddgz");
                        ddgzNum ++;
                    }
                }
            }

            if (pathResult != null && pathResult.length() > 2){
                ObjectMapper mapper = new ObjectMapper();
                WebsiteSourceClassifyBean bean = mapper.readValue(pathResult, WebsiteSourceClassifyBean[].class)[0];
                if (bean != null){
                    if ("ddgz".equals(bean.getData_user())){
                        eachOne.setViaChannPathResult("ddgz");
                        ddgzNum ++;
                    }
                }
            }
        }

        ExcelUtil excelUtil = new ExcelUtil();
        String filePath = "C:\\Users\\lenovo\\Desktop\\当代贵州结果.xls";
        Workbook workbook = excelUtil.mapPOJOToExcel(filePath,websiteChannels);
        excelUtil.outputStreamThenClose(workbook,filePath);
        return String.valueOf(ddgzNum);

    }

    public String restTemplateResut(String website,String channel){
        String catagory = restTemplate.getForObject("http://{url}:{port}/website/pagelist/?website={website}&channel={channel}",
                String.class, "10.200.74.16", "7171", website, channel);
        return catagory;
    }

    /**
     * 该方法作用：
     * 用户发出请求下载文件，然后后端去服务器上获取指定的文件，然后用户可以下载这个文件
     * @param response
     * @throws IOException
     */
    public void downLoadFile(HttpServletResponse response) throws IOException {
        //假设这是服务器上文件的位置
        String filePath = "D:\\ids\\Person.xls";
        String fileName = "Person.xls";
        InputStream inputStream = new FileInputStream(new File(filePath));
        //这个是固定写法，attachment是附件，那么浏览器就知道后端返回了一个附件，那么浏览器就会弹出下载对话框供用户下载
        response.setHeader("Content-disposition","attachment;filename=" + fileName);
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream,outputStream);
        if (inputStream != null)
            inputStream.close();
        if (outputStream != null){
            outputStream.flush();
            outputStream.close();
        }
    }

    /**
     * 在内存中生成Workbook，然后设置消息头，用户可以下载Excel
     * @param response
     * @throws Exception
     */
    public void downLoadExcel(HttpServletResponse response) throws Exception {
        String filename = "未命名.xls";
        Query query = entityManager.createQuery("select w from Website w where w.website = ?1");
        query.setParameter(1,"新蓝网");
        List<Website> list = query.getResultList();
        Workbook workbook = ExcelUtil.mapPOJOToExcel(filename,list);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition","attachment;filename=" + filename);
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }

}
