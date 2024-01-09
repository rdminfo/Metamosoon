package com.rdminfo.mms.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 分页参数解析封装
 *
 * @author rdminfo 2023/12/04 11:50
 */
@Data
public class MPage {

    private long pageCurrent;
    private long pageSize;
    private long total;

    public MPage() {}

    public MPage(long pageCurrent, long pageSize) {
        this(pageCurrent, pageSize, 0);
    }

    public MPage(long pageCurrent, long pageSize, long total) {
        this.pageCurrent = pageCurrent;
        this.pageSize = pageSize;
        this.total = total;
    }

    public <T> Page<T> getPage () {
        return new Page<>(pageCurrent, pageSize, total);
    }

    public <O, T> Page<O> convert (Page<T> iPage) {
        return new Page<>(iPage.getCurrent(), iPage.getSize(), iPage.getTotal());
    }


}
