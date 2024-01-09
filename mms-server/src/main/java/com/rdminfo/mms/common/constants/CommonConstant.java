package com.rdminfo.mms.common.constants;

/**
 * 通用常量
 *
 * @author rdminfo 2023/12/14 22:55
 */
public class CommonConstant {

    /**
     * 条件-是否
     */
    public static class YES_NO {
        /**
         * 是
         */
        public static final int YES = 1;
        public static final String YES_STR = "yes";

        /**
         * 否
         */
        public static final int NO = 0;
        public static final String NO_STR = "no";

    }

    /**
     * 条件-是否有
     */
    public static class HAS_NO {
        /**
         * 有
         */
        public static final String HAS = "has";
        /**
         * 没有
         */
        public static final String NO = "no";

    }

    /**
     * 排序方式
     */
    public static class SORT_ORDER {
        /**
         * 升序
         */
        public static final String ASC = "asc";
        /**
         * 降序
         */
        public static final String DESC = "desc";

    }

}
