package com.rdminfo.mms.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MmsFiles implements Serializable {


    /************** SQL字段常量生成 **************/
    public static final String ID = "id";
    public static final String PID = "pid";
    public static final String MD5 = "md5";
    public static final String CATEGORY = "category";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String ROOT_PATH = "root_path";
    public static final String ABSOLUTE_PATH = "absolute_path";
    public static final String TYPE = "type";
    public static final String TYPE_SORT = "type_sort";
    public static final String SIZE = "size";
    public static final String LEVEL = "level";
    public static final String LAST_MODIFIED = "last_modified";

    private Long id;

    private Long pid;

    private String md5;

    private String category;

    private String name;

    private String path;

    private String rootPath;

    private String absolutePath;

    private String type;

    private Integer typeSort;

    private Long size;

    private Integer level;

    private String lastModified;


}
