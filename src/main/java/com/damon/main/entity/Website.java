package com.damon.main.entity;

import com.damon.main.excelUtil.ExcelColumnAnnotation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/7/25.
 */
@Entity
@Data
@Getter
@Setter
@ToString
@Table(name = "website")//website_mty
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ExcelColumnAnnotation(name = "采集点属性")
    @Column(name = "is_provincial")
    private String is_provincial;

//    @ExcelColumnAnnotation(name = "RowNum")
    @Transient
    private String rowNum;

    @ExcelColumnAnnotation(name = "网站名")
    @Column(name = "website")
    private String website = "";

    @ExcelColumnAnnotation(name = "主网站属性")
    @Column(name = "isHostSite")
    private Integer isHostSite; // 1：主网站；0：地方频道(地方频道partChannelName不为空)

    @ExcelColumnAnnotation(name = "地方频道名称")
    @Column(name = "partChannel")
    private String partChannel;

    @ExcelColumnAnnotation(name = "栏目名")
    @Column(name = "channelPath")
    private String channelPath;/*全路径*/

    @ExcelColumnAnnotation(name = "栏目别名")
    @Column(name = "channel")
    private String channel = ""; // 栏目别名用于应用层简化显示和使用

    @ExcelColumnAnnotation(name = "链接地址")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address = "";

    @ExcelColumnAnnotation(name = "内容属性")
    @Transient
    private String excelContentType; // 0：新闻，1：信息（模板性文章）非数据库映射字段，待转换

    @ExcelColumnAnnotation(name = "归属性质")
    @Column(name = "belonging_m")
    @JsonIgnore
    private String belongingM; //词典码值 明文

    @ExcelColumnAnnotation(name = "网站性质")
    @Column(name = "sitenatureM")
    private String siteNatureM;

    @ExcelColumnAnnotation(name = "采集优先级")
    @Column(name = "gatherPriority")
    private String gatherPriority;

    @ExcelColumnAnnotation(name = "地域分类")
    @Column(name = "regionalstructure")
    private String regionalstructure;

    @ExcelColumnAnnotation(name = "内容分类")
    @Column(name = "erticalstructure_m")
    @JsonIgnore
    private String erticalstructure_m; /*垂直性质 明文*/

    @ExcelColumnAnnotation(name = "标签")
    @Column(name = "markList")
    private String markList;

    @ExcelColumnAnnotation(name = "数据圈")
    @Transient
    private String excelLevel; // (资源圈)0：资源:1：分析 非数据库映射字段，待转换

    @ExcelColumnAnnotation(name = "媒体单位")
    @Column(name = "mediaUnit")
    private String mediaUnit;

    @ExcelColumnAnnotation(name = "备注")
    @Column(name = "note")
    @JsonIgnore
    private String note;

    @Column(name = "contentType")
    private Integer contentType; // 0：新闻，1：信息（模板性文章）(业务层将excelContentType字段转为该字段，存储到数据库中)

    @Column(name = "sitenature")
    private String siteNature;  /** 网站性质 码值*/

    @Column(name = "level",columnDefinition="INT default 0")
    private int level;  /** (业务层将excelLevel字段转为该字段，存储到数据库中)*/

    @Column(name = "belonging")
    private String belonging = ""; /**  归属性质 */

    @Column(name = "product",columnDefinition="INT default 0")
    private Integer product; /** 是否集团信息(1:是,0否)*/

    @Column(name = "rank")
    private String rank = "";  /** 来源等级 */

    @Column(name = "rank_m")
    @JsonIgnore
    private String rank_m; /** 来源等级 明文 */

    @Column(name = "erticalstructure")
    private String erticalstructure = ""; /** 垂直性质 码值*/

    @Column(name = "definitionstructure")
    private String definitionstructure = ""; /** 自定义性质 */

    @Column(name = "definitionstructure_m")
    @JsonIgnore
    private String definitionstructure_m; /** 自定义性质 明文 */

    @Column(name="reliability")
    private String reliability = ""; /** 可靠性 */

    @Column(name = "source")
    private String source; /** 数据来源 */

    @Column(name = "excel")
    @JsonIgnore
    private String excel; /** excel表格名*/

    //新增字段 data_user，有mlf,ddgz,common
    @Column(name = "data_user")
    private String data_user;
}
