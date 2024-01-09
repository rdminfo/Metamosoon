package com.rdminfo.mms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import com.rdminfo.mms.common.pojo.po.MusicListQueryPO;
import com.rdminfo.mms.common.pojo.vo.MusicAsDetailVO;
import com.rdminfo.mms.common.pojo.vo.MusicAsListVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-05
 */
public interface IMmsMusicService extends IService<MmsMusic> {

    /**
     * 获取音乐列表
     *
     * @param folderId 这里指文件夹ID，即获取某个文件夹下的所有音乐
     * @param queryParam 统计筛选的参数，参数内容见对象属性注解
     * @return 音乐列表数据
     */
    MusicAsListVO getList(Long folderId, MusicListQueryPO queryParam);

    /**
     * 将文件信息标签更新，及更新完文件然后再更新数据库里的数据
     * @param updateFileList 要更新的文件，数据库源数据
     * @param updateMusicInfoList 需要更新的信息
     * @param updateColumns 需要更新的列
     */
    void updateInfo(List<MmsFiles> updateFileList, List<MusicInfoBO> updateMusicInfoList, List<Set<String>> updateColumns);

    /**
     * 将文件信息标签更新，及更新完文件然后再更新数据库里的数据
     * @param fileId 文件ID
     *
     * @return 音乐信息
     */
    MusicAsDetailVO getDetail(long fileId);

    /**
     * 获取音乐的封面
     * @param fileId 文件ID
     *
     * @return 音乐封面byte数组
     */
    byte[] getCover(long fileId);
}
