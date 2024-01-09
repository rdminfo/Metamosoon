package com.rdminfo.mms.common.constants;

/**
 * 正则校验常量
 *
 * @author rdminfo 2023/12/04 9:44
 */
public class RegexConstant {

    /** 正则：yyyy-MM-dd (HH:mm:ss)可省略 日期校验平闰年 */
    public static final String DATE_OMISSIBLE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)(\\s(20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])?$";
    /**  正则：yyyy-MM-dd (HH:mm:ss)不可省略 日期校验平闰年 */
    public static final String DATE_PERFECT = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s(20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9]$";
    /** 正则：HH:mm:ss 时间校验 */
    public static final String TIME = "^(?:20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9]$";
    /** 正则：Integer数字 */
    public static final String INTEGER = "^-?[0-9]\\d*$";
    /** 正则：Double */
    public static final String DOUBLE = "^-?[0-9]\\d*\\.*\\d*$";
    /** 正则：url */
    public static final String URL = "^(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$";
    /** 正则：图片url,http://***.jpg */
    public static final String IMG_URL = "^(http|https)?://.+\\.(jpg|jpeg|svg|png|bmp|gif)$";
    /** 正则：手机号 */
    public static final String TELEPHONE = "^0?(13|14|15|17|18|19)[0-9]{9}$";
    /** 正则：邮箱 */
    public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /** 正则：密码，取值范围为6-20 位，字母、数字、字符 */
    public static final String PASSWD = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-_]).{8,20}$";
    /** 正则：用户名，取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位 */
    public static final String USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
    /** 正则：文件夹或者文件命名规范 */
    public static final String FILE_NAME = "(?!((^(con)$)|^(con)/..*|(^(prn)$)|^(prn)/..*|(^(aux)$)|^(aux)/..*|(^(nul)$)|^(nul)/..*|(^(com)[1-9]$)|^(com)[1-9]/..*|(^(lpt)[1-9]$)|^(lpt)[1-9]/..*)|^/s+|.*/s$)(^[^/:/*/?/\"/</>/|]{1,255}$)";

    /**   文章点赞参数 */
    public static final String PARAM_ARTICLE_THUMB_UP_LIKED = "^(?:like|cancelLike)$";

}
