/**
 * 音乐列表查看类型
 */
export enum ViewType {
  /**
   * 音乐列表查看数据范围
   */
  TAG_RANGE = 'tagRange',

  /**
   * 音乐列表文件筛选类型
   */
  FILE_TYPE = 'fileType'
}

/**
 * 音乐列表查看数据范围
 */
export enum TagRange {
  /**
   * 基础标签数据
   */
  BASIC = 'basic',

  /**
   * 其他标签数据，封面歌词，文件大小，时常等
   */
  OTHER = 'other'
}

/**
 * 音乐列表的文件范围边界
 */
export enum FileType {
  /**
   * 默认的，包含文件和音乐文件
   */
  DEFAULT = 'default',
  /**
   * 仅文件夹
   */
  ONLY_FOLDER = 'onlyFolder',
  /**
   * 仅音乐文件
   */
  ONLY_MUSIC = 'onlyMusic',
  /**
   * 所有文件类型
   */
  ALL = 'all'
}

/**
 * 音乐列表排序
 */
export enum Sort {
  /**
   * 排序类别
   */
  CATEGORY = 'sortCategory',
  /**
   * 排序的方式
   */
  ORDER = 'sortOrder'
}

export enum SortCategory {
  /**
   * 按文件名称
   */
  FILE_NAME = 'fileName',
  /**
   * 按歌曲名称
   */
  SONG_NAME = 'songName',
  /**
   * 按歌手
   */
  ARTIST = 'artist',
  /**
   * 按专辑
   */
  ALBUM = 'album',

  /**
   * 按编号
   */
  TRACK_NUMBER = 'trackNumber',

  /**
   * 按年份
   */
  YEAR = 'year',

  /**
   * 按文件大小
   */
  SIZE = 'size',

  /**
   * 按歌曲时长
   */
  TRACK_LENGTH = 'trackLength',
}
/**
 * 排序方式
 */
export enum SortOrder {
  /**
   * 升序
   */
  ASC = 'asc',
  /**
   * 降序
   */
  DESC = 'desc',
}
/**
 * 列表筛选
 */
export enum FilterCategory {
  /**
   * 歌手筛选
   */
  ARTIST = 'artist',
  /**
   * 专辑筛选
   */
  ALBUM = 'album',

  /**
   * 年份筛选
   */
  YEAR = 'year',
}
