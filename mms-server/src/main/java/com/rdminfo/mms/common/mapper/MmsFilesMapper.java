package com.rdminfo.mms.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rdminfo.mms.common.entity.MmsFiles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-04
 */
public interface MmsFilesMapper extends BaseMapper<MmsFiles> {

    /**
     * 根据id获取所有的父级文件夹
     * @param id 文件id列表
     * @param includeSelf 返回的列表中是否包含自身
     *
     * @return 文件夹列表
     */
    List<MmsFiles> selectParentFilesByIds(@Param("ids") List<Long> id, @Param("idsContainRootId") boolean idsContainRootId, @Param("includeSelf") boolean includeSelf);

    /**
     * 根据id获取所有的子级文件
     * @param id 文件id列表
     * @param includeSelf 返回的列表中是否包含自身
     *
     * @return 文件列表
     */
    List<MmsFiles> selectChildrenFilesByIds(@Param("ids") List<Long> id, @Param("idsContainRootId") boolean idsContainRootId, @Param("includeSelf") boolean includeSelf);
}
