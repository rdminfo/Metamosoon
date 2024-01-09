package com.rdminfo.mms.common.pojo.bo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.enums.FileType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * 文件加载BO
 *
 * @author rdminfo 2023/12/04 14:44
 */
@Data
@Accessors(chain = true)
public class FileSourceBO {

    /**
     * 保存在数据库的主键id
     */
    private Long dbId;

    /**
     * 保存在数据库的父级文件主键id
     */
    private Long dbParentId;

    /**
     * md5用于校验文件是否发生变化
     */
    private String md5;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件相对路径
     */
    private String path;

    /**
     * 文件所在根路径
     */
    private String rootPath;

    /**
     * 绝对路径
     */
    private String absolutePath;

    /**
     * 父级所在路径
     */
    private String parentPath;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件类型排序，文件夹为1，其他为2，固定的
     */
    private Integer typeSort;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 相对于根路径所在的层级
     */
    private Integer level;

    /**
     * 上次更新时间
     */
    private String lastModified;

    /**
     * 文件File类
     */
    private File file;

    public FileSourceBO() {}

    public FileSourceBO(String rootPath, File file, int level, long dbId, long dbParentId) {
        this(rootPath, file, level, dbId);
        this.dbParentId = dbParentId;
    }

    public FileSourceBO(String rootPath, File file, int level, long dbId) {
        this(rootPath, file, level);
        this.dbId = dbId;
    }



    public FileSourceBO(String rootPath, File file, int level) {
        String path = file.getPath().replace("\\", "/");
        String parentPath = file.getParent().replace("\\", "/");
        rootPath = rootPath.replace("\\", "/");
        this.name = file.getName();
        this.path = path.replace(rootPath, "");
        this.rootPath = rootPath;
        this.absolutePath = path;
        this.parentPath = parentPath.replace(rootPath, "");
        this.type = file.isDirectory() ? FileType.FOLDER.getCode() : FileTypeUtil.getType(file);
        this.typeSort = file.isDirectory() ? 1 : 2;
        this.size = file.length();
        this.level = level;
        this.lastModified = DateUtil.formatDateTime(DateUtil.date(file.lastModified()));
        this.file = file;
    }

    public static FileSourceBO convert(MmsFiles mmsFiles) {
        if (null == mmsFiles) { return new FileSourceBO(); }
        return new FileSourceBO().setDbId(mmsFiles.getId()).setAbsolutePath(mmsFiles.getAbsolutePath()).setMd5(mmsFiles.getMd5());
    }

    public static MmsFiles reverseConvert(FileSourceBO fileSourceBO) {
        if (null == fileSourceBO) { return new MmsFiles(); }
        return new MmsFiles().setId(fileSourceBO.getDbId()).setName(fileSourceBO.getName()).setPath(fileSourceBO.getPath())
                .setRootPath(fileSourceBO.getRootPath()).setAbsolutePath(fileSourceBO.getAbsolutePath()).setType(fileSourceBO.getType())
                .setSize(fileSourceBO.getSize()).setLevel(fileSourceBO.getLevel()).setLastModified(fileSourceBO.getLastModified())
                .setMd5(fileSourceBO.md5()).setPid(fileSourceBO.getDbParentId()).setTypeSort(fileSourceBO.getTypeSort());
    }

    public String md5() {
        return DigestUtil.md5Hex(this.name + this.absolutePath + this.type + this.typeSort + this.size + this.level + this.lastModified);
    }
}
