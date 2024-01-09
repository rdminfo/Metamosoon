package com.rdminfo.mms.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 *
 * @author rdminfo 2023/12/04 11:32
 */
@Data
public class CommonPage<T> {
    private Long currentPage;
    private Long pageSize;
    private Long total;
    private Long totalPage;
    private List<T> list;

    /**
     * 将Mybatis-plus分页后的信息转为自定义的分页信息
     */
    public static <T> CommonPage<T> result(IPage<T> page) {
        CommonPage<T> result = new CommonPage<>();
        result.setCurrentPage(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setTotalPage(page.getPages());
        result.setList(page.getRecords());
        return result;
    }

    public static <T, V> CommonPage<T> result(IPage<V> page, List<T> records) {
        CommonPage<T> result = new CommonPage<>();
        result.setCurrentPage(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setTotalPage(page.getPages());
        result.setList(records);
        return result;
    }
}
