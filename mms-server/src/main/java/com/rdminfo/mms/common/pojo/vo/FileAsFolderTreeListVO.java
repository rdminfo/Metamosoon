package com.rdminfo.mms.common.pojo.vo;

import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.pojo.bo.FolderInfoBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * 文件夹树状列表返回视图对象
 *
 * @author rdminfo 2023/12/07 9:43
 */
@Data
@Accessors(chain = true)
public class FileAsFolderTreeListVO {

    /**
     * 所有的ID列表
     */
    private List<String> folderIds;

    /**
     * 树状数据
     */
    private List<TreeItem> treeData;

    public static FileAsFolderTreeListVO convert(List<MmsFiles> mmsFilesList) {
        FileAsFolderTreeListVO resVO = new FileAsFolderTreeListVO();
        Set<String> fileIds = new HashSet<>();

        List<TreeItem> result = new ArrayList<>();
        Map<Long, TreeItem> folderIdMap = new HashMap<>();

        for (MmsFiles mmsFile : mmsFilesList) {
            fileIds.add(String.valueOf(mmsFile.getId()));
            TreeItem folderNode = TreeItem.convert(mmsFile);
            folderIdMap.put(mmsFile.getId(), folderNode);

            if (mmsFile.getLevel() == 1) {
                result.add(folderNode);
            } else {
                TreeItem parentNode = folderIdMap.get(mmsFile.getPid());
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(folderNode);
                }
            }
        }
        resVO.setFolderIds(new ArrayList<>(fileIds)).setTreeData(result);
        return resVO;
    }

    public static FileAsFolderTreeListVO empty() {
        return new FileAsFolderTreeListVO().setFolderIds(new ArrayList<>()).setTreeData(new ArrayList<>());
    }

    /**
     * 树状数据项
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    static class TreeItem extends FolderInfoBO {
        /**
         * 树状数据的children
         */
        private List<TreeItem> children;

        public static TreeItem convert(MmsFiles mmsFiles) {
            TreeItem treeItem = new TreeItem();
            treeItem.setFolderId(String.valueOf(mmsFiles.getId())).setFolderName(mmsFiles.getName());
            treeItem.setChildren(new ArrayList<>());
            return treeItem;
        }
    }

}
