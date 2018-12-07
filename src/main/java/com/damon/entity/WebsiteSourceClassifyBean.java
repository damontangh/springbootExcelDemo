package com.damon.entity;

import lombok.Data;

@Data
public class WebsiteSourceClassifyBean {

	private Integer id;
	/**
	 * 网站名
	 */
	private String website;
	/**
	 * 栏目名称
	 */
	private String channel;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 来源等级
	 */
	private String rank;
	/**
	 * 归属性质
	 */
	private String belonging;
	/**
	 * 垂直性质
	 */
	private String erticalstructure;
	/**
	 * 自定义性质
	 */
	private String definitionstructure;
	/**
	 * 地域性质
	 */
	private String regionalstructure;
	/**
	 * 可靠性
	 */
	private String reliability;
	/**
	 * 数据等级(资源圈:0,分析圈1,其他:100)
	 */
	private String level;
	/**
	 * 是否集团信息(1:是,0否)
	 */
	private String product;
	/**
	 * 数据源
	 */
	private String source;

	/*新来源分类打标字段  @since  17.09.20*/
	private String channel_path;//channelPath; /*栏目全路径*/

	private String content_type;//contentType; /*内容属性 新闻1，信息2 ...*/

	private String sitenature;//siteNature; /*网站性质 码值多值*/

	private String is_host_site;//isHostSite; /*主网站属性；暂时不加；无应用场景*/

	private String part_channel;//partChannel; /*地方频道名称全路径*/

	private String media_unit; /*媒体单位*/

	private String mark_list; /*标签*/
	private String data_user; /*用户，mlf,ddgz,common,其中common的数据要保存到2个正式库*/
}
