package com.damon.main.excelUtil;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 这个类的作用：
 * 是Excel工具类，
 * 例如：
 * 利用IO流，在本地创建excel；
 * 读取本地excel，并根据xls还是xlsx生成workbook实例；
 * 对本地excel文件写入内容等；
 * 读取上传的excel，映射为实体list；
 * 在内存生成excel，并回写到本地；
 * @author Damon update on January 30th,2018
 * 把ExcelEntity换为内部类
 */
@Slf4j
@EnableScheduling
@Component
public class ExcelUtil {

    private static final String XLSX = "xlsx";
    private static final String XLS = "xls";

    public static boolean isItNullOrEmpty(String obj) {
        if (obj == null || obj.length() == 0) return true;
        else return false;
    }

    @Data
    @ToString
    private static class ExcelEntity{
        private Map<Integer,String> map;
        private Workbook workbook;
    }

    /**
     * 创建一个不带内容的excel
     * @param filePath
     */
    public static void createExcelToLocal(String filePath) throws Exception {

        Workbook workbook = null;
        if (filePath.endsWith(XLS))
            workbook = new HSSFWorkbook();
        else if (filePath.endsWith(XLSX))
            workbook = new XSSFWorkbook();
        else
            throw new Exception("pls specify the correct format of excel,xls or xlsx?");
        workbook.createSheet("sheet1");
        outputStreamThenClose(workbook,filePath);
    }

    /**
     * 用于在本地生成一个带内容的excel
     * @param workbook
     * @param filePath
     */
    public static void createExcelWithContentToLocal(Workbook workbook,String filePath) throws Exception {
        if (workbook == null)
            throw new NullPointerException("workbook null");
        //如果文件名既不是xls也不是xlsx，那就抛出异常
        if (!(filePath.endsWith(XLSX) || filePath.endsWith(XLS)))
            throw new Exception("pls specify correct format of excel,xls or xlsx");
        //如果workbook类型和filePath的结尾文件格式不一致，就要抛出异常
        String workbookType = workbook.getClass().getName();
        if (workbookType.endsWith("HSSFWorkbook") && !filePath.endsWith(XLS)){
            throw new Exception("The format of excel should be the same with workbook type," +
                    "HSSFWorkbook fits xls,XSSFWorkbook fits xlsx");
        }
        if (workbookType.endsWith("XSSFWorkbook") && !filePath.endsWith(XLSX)){
            throw new Exception("The format of excel should be the same with workbook type," +
                    "HSSFWorkbook fits xls,XSSFWorkbook fits xlsx");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            workbook.write(fos);
            fos.flush();
        } catch (Exception e) {
            log.error(String.format("FileOutputStream stream error,msg:[%s]",e));
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(String.format("close FileOutputStream error,msg:[%s]",e));
                }
            }
            if (null != workbook){
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error(String.format("close workbook stream error,msg:[%s]",e));
                }
            }
        }
    }

    /**
     * 读取本地excel并返回workbook对象
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Workbook readLocalExcel(String filePath) throws Exception {
        InputStream inputStream = new FileInputStream(new File(filePath));
        //xls，返回HSSFWorkbook
        //xlsx，返回XSSFWorkbook
        Workbook wb = getWorkbook(inputStream,filePath);
        inputStream.close();
        return wb;

    }

    /**
     * 回写一行内容到本地excel中
     * @param rowNum 这是第几行的参数，参数要求 >= 1
     * @param totalColNum 一行需要用到的列数，参数要求 >= 1
     * @param contents 这是string[]，每一列对应数组的每一个元素，第1个元素写到第1列，第2个元素写到第2列，以此类推
     * @param filePath
     * @throws Exception
     */
    public static void writeOneRowToExcel(int rowNum,int totalColNum,String[] contents,String filePath)
            throws Exception {
        if (rowNum < 1) throw new Exception("rowNum must be >= 1");
        if (totalColNum < 1) throw new Exception("totalNum must be >= 1");
        //如果列数和数组长度不一致，也要抛出异常
        if (contents == null) throw new NullPointerException("contents null");
        if (totalColNum != contents.length) throw new Exception("column amount is not the same with length of String[]");
        //传入的totalColNum是类的成员变量的数量,例如5个成员变量，那么下标就是0-4
        //传入的rowNum是平时说的第几行，例如第3行，那么下标就需要-1=2
        Workbook workbook = readLocalExcel(filePath);
        Sheet sheet = workbook.getSheetAt(0);
        Row row =sheet.createRow(rowNum - 1);
        for (int i = 1; i <= totalColNum; i++) {
            //row和列都会-1，因此传入的参数从1开始，要减去1
           Cell cell = row.createCell(i - 1);
           cell.setCellValue(contents[i - 1]);
        }
        outputStreamThenClose(workbook,filePath);
    }

    /**
     * 这是回写到excel需要用到的IO流
     * 这是一个公用方法，凡是需要回写excel，势必需要IO流
     * @param workbook
     * @param filePath
     * @throws Exception
     */
    public static void outputStreamThenClose(Workbook workbook,String filePath) throws Exception {
        if (workbook == null)
            throw new Exception("workbook is null");
        if (!(filePath.endsWith(XLS) || filePath.endsWith(XLSX)))
            throw new Exception("format of excel file is uncorrect,pls specify xls or xlsx");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            workbook.write(fos);
            fos.flush();
        } catch (Exception e) {
            log.error(String.format("FileOutputStream stream error,msg:[%s]",e));
        } finally {
            if (null != workbook){
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error(String.format("close workbook stream error,msg:[%s]",e));
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(String.format("close FileOutputStream error,msg:[%s]",e));
                }
            }

        }
    }

    /**
     * 该方法作用：
     * 对本地excel写入内容
     * @param rowNum 行数，这个从1开始数
     * @param columnNum 列数，这个从1开始数
     * @param content
     * @param file_path 本地文件绝对路径
     * @throws IOException
     */
    public static void writeContentToOneCellOfExcel(int rowNum,int columnNum, String content,String file_path)
            throws Exception {
        Workbook wb = getWorkbook(new FileInputStream(new File(file_path)),file_path);
        Sheet firstSheet = wb.getSheetAt(0);
        Row row = firstSheet.getRow(rowNum - 1);//row实际下标从0开始
        Cell cell = row.createCell(columnNum - 1);//列实际下标从0开始
        cell.setCellValue(content);
        outputStreamThenClose(wb,file_path);
        //现在IO流单独写了个方法，下面的代码就用不到了
       /* FileOutputStream fos = new FileOutputStream(new File(file_path));
        wb.write(fos);
        wb.close();
        fos.close();*/
    }

    /**
     * 该方法适用于上传excel文件后返回一个workbook实例
     * 根据传入的文件格式xls还是xlsx，返回对应的workbook实例
     * @param inputStream
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String filePath) throws Exception {
        //xls，返回HSSFWorkbook
        //xlsx，返回XSSFWorkbook
        Workbook workbook = null;
        if (filePath.endsWith(XLS))
            workbook =  new HSSFWorkbook(inputStream);
        else if (filePath.endsWith(XLSX))
            workbook = new XSSFWorkbook(inputStream);
        else
            throw new Exception("the format of local excel is uncorrect,the correct format should be xls or xlsx");
        inputStream.close();
        return workbook;
    }

    /**
     * 用于在内存中生成一个Excel对象
     * 并且默认创建sheet1
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Workbook createWorkbook(String filePath) throws Exception {
        Workbook workbook = null;
        if (filePath.endsWith(XLS))
            workbook =  new HSSFWorkbook();
        else if (filePath.endsWith(XLSX))
            workbook = new XSSFWorkbook();
        else
            throw new Exception("the format of local excel is uncorrect,the correct format should be xls or xlsx");
        workbook.createSheet("sheet1");
        return workbook;
    }

    /**
     * 读取本地Excel文件
     * 获取excel的首行，
     * 然后把列数和单元格的值放入map中，
     * key=0,value=姓名
     * key=1,value=年龄
     * 意思就是第1列是姓名，下标是从0开始的
     * 第2列是年龄
     * map的size就是一共有N列
     * @param filePath
     * @param elementType
     * @return
     * @throws Exception
     */
    public static ExcelEntity getExcelHeader(String filePath, Class elementType) throws Exception {
        Workbook workbook = readLocalExcel(filePath);
        if (workbook == null) throw new NullPointerException("read local excel error,sometime the excel that you create" +
                "can not be read,pls recreate your excel.");
        ExcelEntity entity = new ExcelEntity();
        entity.setWorkbook(workbook);
        Field[] fields = elementType.getDeclaredFields();
        if (fields == null || fields.length == 0)
            return null;
        AccessibleObject.setAccessible(fields,true);
        Sheet sheet = workbook.getSheetAt(0);
        Row firstRow = sheet.getRow(0);
        //获取有效单元格数量
        int solidCellNum = firstRow.getPhysicalNumberOfCells();
        Map<Integer,String> colOrderAnnoValueMap = new HashMap<>();
        for (int i = 0; i < solidCellNum; i++) {
            //cell下标从0开始
            Cell eachCell = firstRow.getCell(i);
            String cellValue = eachCell.getStringCellValue();
            for (int y = 0; y < fields.length; y++) {
                Field eachField = fields[y];
                eachField.setAccessible(true);
                ExcelColumnAnnotation excelAnnotation = eachField.getAnnotation(ExcelColumnAnnotation.class);
                if (excelAnnotation == null)
                    continue;
                String annotationName = excelAnnotation.name();
                if (cellValue == null)
                    continue;
                if (cellValue.equals(annotationName))
                    colOrderAnnoValueMap.put(i,cellValue);
            }
        }
        entity.setMap(colOrderAnnoValueMap);
        return entity;
    }

    /**
     * 通过调用getExcelHeader读取本地Excel，
     * 获取ExcelEntity有成员变量Workbook和Map，
     * 通过workbook可以获取到一共有N行数据
     * 首先遍历N次，一行数据就是一个对象
     * 然后获取map的size,x
     * 遍历x次，这是列数，那就是说可以获取每一行的每个单元格的值
     * 只要所在列对应的首行的值和对象的成员变量的注解值一致，那就可以对这个成员变量进行赋值
     * 例如，第3个的第1个单元格的数据，第1个单元格，就是第1列，对应的首行数据是"姓名",
     * 然后有个成员变量是@注解("姓名") String name;
     * 首行数据值和注解值一致，那么就可以把第3行第1个单元格的值赋值给name变量。
     * @param filePath
     * @param elementType
     * @param <T>
     * @return
     * @throws Exception
     * @author Damon update on January 10th,2018
     */
    public static <T> List<T> mapExcelToPOJO(String filePath,Class<T> elementType) throws Exception {
        ExcelEntity excelEntity = getExcelHeader(filePath,elementType);
        Map<Integer,String> map = excelEntity.getMap();
        Workbook workbook = excelEntity.getWorkbook();
        if (workbook == null)
            return null;
        Sheet sheet = workbook.getSheetAt(0);
        //获取所有行数
        int rowNum = sheet.getPhysicalNumberOfRows();
        List<T> list = new ArrayList<>();
        int mapSize = map.size();
        //数据行从第二行开始，因此下标从1开始
        for (int i = 1; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            //每一行数据就是一个对象
            T pojo = elementType.newInstance();
            Field[] fields = pojo.getClass().getDeclaredFields();
            //mapSize就是列数，列下标从0开始
            for (int x = 0; x < mapSize; x++) {
                Cell cell = row.getCell(x);
                if (cell == null) continue;//如果是xlsx文件，这一步就不能少，否则cell.getValue就会异常
                String annotationValue = map.get(x);
                for (int y = 0; y < fields.length; y++) {
                    Field eachField = fields[y];
                    eachField.setAccessible(true);
                    ExcelColumnAnnotation excelAnno = eachField.getAnnotation(ExcelColumnAnnotation.class);
                    if (excelAnno == null)
                        continue;
                    String fieldAnnoName = excelAnno.name();
                    //还要区分单元格值的类型是String还是interger
                    if (annotationValue.equals(fieldAnnoName)){
                        //这时候，还要判断成员变量的属性是String的还是数值型
                        //获取成员变量的类型的简称
                        String fieldType = eachField.getType().getSimpleName();
                        //下面开始给对象的成员变量赋值，如果获取单元格的值是null，那就跳过，否则抛null异常
                        if ("String".equals(fieldType)){
                            String cellValue = cell.getStringCellValue();
                            if (cellValue == null) continue;
                            eachField.set(pojo,cellValue);
                        } else if ("Integer".equals(fieldType) ||
                                "int".equals(fieldType)){
                            Double cellValue = cell.getNumericCellValue();
                            if (cellValue == null) continue;
                            double val = cellValue;
                            int finalVal = (int) val;
                            eachField.set(pojo,finalVal);
                        } else if ("Double".equals(fieldType) ||
                                "double".equals(fieldType)){
                            Double cellValue = cell.getNumericCellValue();
                            if (cellValue == null) continue;
                            eachField.set(pojo,cellValue);
                        } else if ("Boolean".equals(fieldType) ||
                                "boolean".equals(fieldType)){
                            Boolean cellValue = cell.getBooleanCellValue();
                            if (cellValue == null) continue;
                            eachField.set(pojo,cellValue);
                        }
                    }
                }

            }
            list.add(pojo);

        }
        return list;
    }

    /**
     * 把java对象映射到Excel，但是没有回写到本地，因此需要另外调用输出流
     * @param filePath
     * @param list
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Workbook mapPOJOToExcel(String filePath,List<T> list)
            throws Exception {
        if (list == null || list.size() == 0)
            return null;
        Workbook workbook = createWorkbook(filePath);
        Sheet sheet = workbook.getSheetAt(0);
        Map<String,Integer> map = null;
        //list size就是行数Row的数量,row下标从0开始
        for (int i = 0; i <= list.size(); i++) {
            Row row = sheet.createRow(i);
            T t = null;
            //如果是第一行，那这是标题行
            if (i == 0){
                t = list.get(i);
                Field[] fields = t.getClass().getDeclaredFields();
                AccessibleObject.setAccessible(fields,true);
                map = traverseFields(fields);
                if (map == null || map.size() == 0)
                    throw new Exception("column amount is 0,you must add annotation to at least on field of entity");
                Set<Map.Entry<String, Integer>> keyVals = map.entrySet();
                for (Map.Entry<String,Integer> e:keyVals){
                    int colNum = e.getValue();
                    String annoValue = e.getKey();
                    Cell cell = row.createCell(colNum);
                    cell.setCellValue(annoValue);
                }
            } else {
                //从第2行开始，那就是数据行，需要注意的是，list.get()，传入的下标应该要-1
                //因为第2行才是数据行，那么我此时应该获取第一个元素，但是i从1开始，因此要-1
                t = list.get(i - 1);
                Field[] fields = t.getClass().getDeclaredFields();
                AccessibleObject.setAccessible(fields,true);
                for (int x = 0; x < fields.length; x++) {
                    Field eachField = fields[x];
                    ExcelColumnAnnotation excelAnnotation = eachField.getAnnotation(ExcelColumnAnnotation.class);
                    if (excelAnnotation == null)
                        continue;
                    boolean shouldDownload = excelAnnotation.shouldDownload();
                    boolean isDbField = excelAnnotation.isDbColumn();
                    if (!shouldDownload || !isDbField) continue;
                    String annoName = excelAnnotation.name();
                    if (isItNullOrEmpty(annoName))
                        continue;
                    //如果map中有这个注解值，那就获取value，也就是列数colNum
                    //把当前成员变量的值，写到colNum下标对应的列下的某个单元格
                    if (map.containsKey(annoName)){
                        Integer colNum = map.get(annoName);
                        Cell cell = row.createCell(colNum);
                        //还要考虑成员变量的类型
                        //这时候，还要判断成员变量的属性是String的还是数值型
                        if ("String".equals(eachField.getType().getSimpleName())){
                            String value = (String) eachField.get(t);
                            if (value == null) continue;
                            /**
                             * excel单元格支持内容长度是32767，超过的话就会抛异常，就算合并单元格也不行，
                             * 成品库实体只有正文的长度会超过这个限度，那么就把正文字段放在类体的最后位置，
                             * 因为映射字段到excel的时候，字段的顺序就是按照从上到下的顺序来的
                             * 既然合并单元格不能解决问题，那就只能多创建几个单元格，各自放一部分正文
                             */
                            int valLenght = value.length();
                            int cellMax = 32767;
                            if (valLenght <= cellMax){
                                //这是长度在限度之内
                                cell.setCellValue(value);
                            } else {
                                //这是长度超过了限度
                                double remainder = valLenght % cellMax;
                                int quotient = valLenght / cellMax;
                                int cellNums  = (remainder > 0 ? quotient + 1:quotient);
                                //已经创建的cell赋值0-32766
                                cell.setCellValue(value.substring(0,cellMax - 1));
                                //cellNums就是需要的单元格数量
                                //因为已经在上面创建了一个cell，因此下面for循环要少一次
                                int startNum = cellMax;
                                for (int y = 1; y <= cellNums - 1; y++) {
                                    //接下去创建的单元格的列数从已知的colNum +1 开始
                                    Cell moreCell = row.createCell(colNum + y);
                                    int end = startNum + (cellMax -1);
                                    if (end >= valLenght)
                                        moreCell.setCellValue(value.substring(startNum));
                                    else
                                        moreCell.setCellValue(value.substring(startNum,end));
                                    startNum = end + 1;
                                }
                            }
                        } else if ("Integer".equals(eachField.getType().getSimpleName()) ||
                                "int".equals(eachField.getType().getSimpleName())){
                            Integer value = (Integer) eachField.get(t);
                            if (value == null) continue;
                            cell.setCellValue(value);
                        } else if ("Double".equals(eachField.getType().getSimpleName()) ||
                                "double".equals(eachField.getType().getSimpleName())){
                            Double value = (Double) eachField.get(t);
                            if (value == null) continue;
                            cell.setCellValue(value);
                        } else if ("Boolean".equals(eachField.getType().getSimpleName()) ||
                                "boolean".equals(eachField.getType().getSimpleName())){
                            Boolean value = (Boolean) eachField.get(t);
                            if (value == null) continue;
                            cell.setCellValue(value);
                        }
                    }

                }

            }

        }
        return workbook;
    }

    public static Map<String,Integer> traverseFields(Field[] fields){
        int num = 0;
        Map<String,Integer> map = new HashMap<>();
        for (int x = 0; x < fields.length; x++) {
            Field eachField = fields[x];
            ExcelColumnAnnotation excelAnnotation = eachField.getAnnotation(ExcelColumnAnnotation.class);
            if (excelAnnotation == null)
                continue;
            String annoName = excelAnnotation.name();
            boolean shouldDownload = excelAnnotation.shouldDownload();
            /**
             * 实体类中可能有几个属性的注解名称是一致的，例如数据圈，一个是针对excel中的数据圈，
             * 一个是针对数据库中的数据圈level
             * 下载的时候，应该是后者level才会有值，因此下面要进行判断该属性是否和数据库相对应
             */
            boolean isDbField = excelAnnotation.isDbColumn();
            if (!shouldDownload || !isDbField) continue;
            if (isItNullOrEmpty(annoName))
                continue;
            map.put(annoName,num);
            num++;
        }
        return map;
    }

    /**
     * 根据上传的Excel，返回一个Workbook
     * @param file
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbookFromUploadeExcel(MultipartFile file) throws Exception {
        if (file == null)
            throw new Exception("Fetch Excel file failed or file is null");
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = getWorkbook(inputStream,fileName);
        return workbook;

    }

    /**
     * 获取上传的文件的标题行
     * 并且创建一个Map，key是标题,value是列数
     * @param file
     * @param elementType
     * @return
     * @throws Exception
     */
    public static ExcelEntity getExcelHeaderFromWorkbook(MultipartFile file, Class elementType) throws Exception {
        Workbook workbook = getWorkbookFromUploadeExcel(file);
        ExcelEntity entity = new ExcelEntity();
        entity.setWorkbook(workbook);
        Field[] fields = elementType.getDeclaredFields();
        if (fields == null || fields.length == 0)
            return null;
        AccessibleObject.setAccessible(fields,true);
        Sheet sheet = workbook.getSheetAt(0);
        Row firstRow = sheet.getRow(0);
        //获取有效单元格数量
        int solidCellNum = firstRow.getPhysicalNumberOfCells();
        Map<Integer,String> colOrderAnnoValueMap = new HashMap<>();
        Map<String,Boolean> annotationIsRequiredMap = new HashMap<>();
        for (int i = 0; i < solidCellNum; i++) {
            //cell下标从0开始
            Cell eachCell = firstRow.getCell(i);
            String cellValue = eachCell.getStringCellValue();
            for (int y = 0; y < fields.length; y++) {
                Field eachField = fields[y];
                eachField.setAccessible(true);
                ExcelColumnAnnotation excelAnnotation = eachField.getAnnotation(ExcelColumnAnnotation.class);
                if (excelAnnotation == null)
                    continue;
                boolean isExcelCol = excelAnnotation.isExcelColumn();
                //不是excel中的列，那就跳过
                if (!isExcelCol) continue;
                String annotationName = excelAnnotation.name();
                boolean required = excelAnnotation.required();
                //不需要重复往map中put
                if (i == 0)
                    annotationIsRequiredMap.put(annotationName,required);
                if (cellValue == null)
                    continue;
                if (cellValue.equals(annotationName)){
                    colOrderAnnoValueMap.put(i,cellValue);
                    break;
                }
            }
        }
        compareAnnotationListExcelColumns(annotationIsRequiredMap,colOrderAnnoValueMap);
        colOrderAnnoValueMap = sortMapBykey(colOrderAnnoValueMap);
        entity.setMap(colOrderAnnoValueMap);
        return entity;
    }

    public static Map sortMapBykey(Map<Integer,String> theMap){
        Map<Integer,String> treeMap = new TreeMap<>(new MapKeyComparator());
        treeMap.putAll(theMap);
        theMap = treeMap;
        return theMap;
    }

    static class MapKeyComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer first, Integer second) {
            return first - second;
        }
    }

    //如果有Excel有列名和实体类属性不一致，那么就要抛出异常
    public static void compareAnnotationListExcelColumns(Map<String,Boolean> annotationMap,Map<Integer,String> excelMap) throws Exception {
        if (annotationMap == null || annotationMap.size() == 0) throw new Exception("实体类没有成员变量或者没有添加注解");
        if (excelMap == null || excelMap.size() == 0) throw new Exception("Excel第一行没有列名");
        String missingField = "";
        boolean isError = false;
        Set<String> annotationKeys = annotationMap.keySet();
        for (String key : annotationKeys) {
            boolean isRequired = annotationMap.get(key);
            if (isRequired) {
                if (!excelMap.containsValue(key)) {
                    isError = true;
                    missingField += key + ";";
                }
            }
        }
        if (isError)
            throw new Exception(String.format("Excel中有列名错误或者缺失，正确的是：%s", missingField));
    }

    /**
     * 把上传的excel映射为实体类
     * @param file
     * @param elementType
     * @param <T>
     * @return
     * @throws Exception
     */
public static <T> List<T> mapUploadedExcelToPOJO(MultipartFile file, Class<T> elementType) throws Exception {
        ExcelEntity excelEntity = getExcelHeaderFromWorkbook(file,elementType);
        Map<Integer,String> map = excelEntity.getMap();
        Workbook workbook = excelEntity.getWorkbook();
        if (workbook == null)
            return null;
        Sheet sheet = workbook.getSheetAt(0);
        //获取所有行数
        int rowNum = sheet.getPhysicalNumberOfRows();
        List<T> list = new ArrayList<>();
        Set<Integer> mapIndex = map.keySet();
        //数据行从第二行开始，因此下标从1开始
        for (int i = 1; i < (rowNum - 1); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            //每一行数据就是一个对象
            T pojo = elementType.newInstance();
            Field[] fields = pojo.getClass().getDeclaredFields();
            /**
             * 出现过这种情况，map中的key是0,1,2,3,4,5,6,7,8,9,12
             * 这是因为10,11这两列和实体类属性匹配不上，或者是空白列
             * 因此应该遍历map的key才行
             */
            int blankAmount = 0;
            for (Integer x : mapIndex) {
                /**
                 * 出现过这种情况，当整行都是空的时候，row.getCell获取的单元格反而不是null，当某行有数据，只是个别列是空的，
                 * 通过row.getCell获取这些单元格的时候得到的是null
                 */
                Cell cell = row.getCell(x);
                if (cell == null) {
                    blankAmount++;
                    continue;
                }
                //有这种情况，行与行之间有空白行，但是不做处理这整一行也会被转成一个对象，只不过所有字段都是null,因此要规避这种现象
                CellType cellType = cell.getCellTypeEnum();
                String cellTypeName = cellType.name();
                if ("BLANK".equals(cellTypeName)) {
                    blankAmount++;
                    continue;
                }
                String annotationValue = map.get(x);
                for (int y = 0; y < fields.length; y++) {
                    Field eachField = fields[y];
                    eachField.setAccessible(true);
                    ExcelColumnAnnotation excelAnno = eachField.getAnnotation(ExcelColumnAnnotation.class);
                    if (excelAnno == null)
                        continue;
                    boolean isExcelCol = excelAnno.isExcelColumn();
                    if (!isExcelCol) continue;
                    String fieldAnnoName = excelAnno.name();
                    //还要区分单元格值的类型是String还是interger
                    if (annotationValue.equals(fieldAnnoName)){
                        //这时候，还要判断成员变量的属性是String的还是数值型
                        //获取成员变量的类型的简称
                        String fieldType = eachField.getType().getSimpleName();
                        if ("String".equals(fieldType)){
                            String cellValue;
                            if ("NUMERIC".equals(cellTypeName))
                                cellValue = String.valueOf((int) cell.getNumericCellValue());
                            else cellValue = cell.getStringCellValue();
                            if (cellValue == null) continue;
                            cellValue = cellValue.replaceAll("\n","").trim();
                            eachField.set(pojo,cellValue);
                        } else if ("Integer".equals(fieldType) ||
                                "int".equals(fieldType)){
                            Double cellValue;
                            if ("STRING".equals(cellTypeName))
                                cellValue = Double.parseDouble(cell.getStringCellValue());
                            else cellValue = cell.getNumericCellValue();
                            if (cellValue == null) continue;
                            double val = cellValue;
                            int finalVal = (int) val;
                            eachField.set(pojo,finalVal);
                        } else if ("Double".equals(fieldType) ||
                                "double".equals(fieldType)){
                            Double cellValue;
                            if ("STRING".equals(cellTypeName))
                                cellValue = Double.parseDouble(cell.getStringCellValue());
                            else cellValue = cell.getNumericCellValue();
                            if (cellValue == null) continue;
                            eachField.set(pojo,cellValue);
                        } else if ("Boolean".equals(fieldType) ||
                                "boolean".equals(fieldType)){
                            Boolean cellValue = cell.getBooleanCellValue();
                            if (cellValue == null) continue;
                            eachField.set(pojo,cellValue);
                        }
                        //equals了，那么就不用再和实体类其它属性比较了
                        break;
                    }
                }
            }
            //如果空字段的数量和map中key的数量一致，那说明是空的对象
            if (blankAmount < mapIndex.size()) list.add(pojo);
        }
        return list;
    }
}
