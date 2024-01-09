package com.rdminfo.mms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.pojo.bo.FileSourceBO;
import com.rdminfo.mms.common.pojo.bo.FileZipBO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderListVO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderTreeListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-04
 */
public interface IMmsFilesService extends IService<MmsFiles> {

    /**
     * 获取文件夹列表
     *
     * @param fileCategory 需要获取的文件分区类型，例如music为查询音乐的文件夹
     * @return 返回数据
     */
    FileAsFolderTreeListVO getFoldersAsTreeList(String fileCategory);

    /**
     * 获取某个文件夹的所有父级文件夹列表
     *
     * @param fileId 文件夹ID，指type为folder的fileId
     * @return 父级文件夹列表，包括自己，且按照层级有小到大排序
     */
    List<FileAsFolderListVO> getParentFolders(Long fileId);

    /**
     * 文件移动
     *
     * @param toFolderId 移动的目标文件夹ID
     * @param fileIds 移动的文件ID列表
     */
    void moveFiles(Long toFolderId, List<Long> fileIds);

    /**
     * 文件删除
     *
     * @param fileIdList 要删除的文件ID列表
     */
    void deleteFiles(List<Long> fileIdList);

    /**
     * 保存单个文件至指定目录
     *
     * @param toFolderId 目标文件夹ID
     * @param file 保存的文件
     * @param fileCategory 保存的文件类型，目前有音乐和影视
     *
     * @return 保存后返回数据
     */
    FileSourceBO saveFile(Long toFolderId, MultipartFile file, String fileCategory);

    /**
     * 根据文件ID查找出文件，然后打包成zip文件
     *   如果要下载的文件已经在缓存目录，则直接返回该文件
     *   缓存目录的文件用md5文件名
     *
     * @param fileIdList 需要打包的文件ID列表
     *
     * @return 需要下载的信息Obj
     */
    FileZipBO toZipFile(List<Long> fileIdList);

    /**
     * 将文件夹保存至对应的文件夹里
     * @param parentFileId 要保存的父级文件夹ID
     * @param folderNameList 需要打包的文件ID列表
     */
    void addFolder(Long parentFileId, List<String> folderNameList);

    /**
     * 将文件重命名
     *      要求两个参数的大小一致，否则不进行业务处理
     * @param fileIdList 要更新的文件ID列表
     * @param fileNameList 要更新的文件夹名称列表
     */
    void updFileName(List<Long> fileIdList, List<String> fileNameList);
}
