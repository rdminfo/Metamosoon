package com.rdminfo.mms.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.pojo.po.MusicListQueryPO;
import com.rdminfo.mms.common.pojo.vo.MusicAsListItemVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-05
 */
public interface MmsMusicMapper extends BaseMapper<MmsMusic> {

    /**
     * 查询某个文件夹下的音乐文件信息
     * @param folderId 文件夹id
     * @param queryParam 查询的参数
     *
     * @return 音乐文件列表
     */
    List<MusicAsListItemVO> selectUnderFolderList(Long folderId, MusicListQueryPO queryParam);
}
