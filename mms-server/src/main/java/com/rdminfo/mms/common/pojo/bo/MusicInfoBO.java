package com.rdminfo.mms.common.pojo.bo;

import cn.hutool.crypto.digest.DigestUtil;
import com.rdminfo.mms.common.entity.MmsMusic;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 音乐Meta信息加载BO
 *
 * @author rdminfo 2023/12/06 11:24
 */
@Data
@Accessors(chain = true)
public class MusicInfoBO {

    /**
     * 标题
     */
    private String title;

    /**
     * 艺术家
     */
    private String artist;

    /**
     * 相册
     */
    private String album;

    /**
     * 年份
     */
    private String year;

    /**
     * 语言
     */
    private String language;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 歌词的内容的长度
     */
    private Integer lyricsLength;

    /**
     * 轨道号码
     */
    private Integer trackNumber;

    /**
     * 封面
     */
    private byte[] cover;

    /**
     * 封面信息，文件格式、分辨率、大小等信息
     */
    private String coverInfo;

    /**
     * 艺术家封面
     */
    private byte[] coverArt;

    /**
     * 文件格式
     */
    private String format;

    /**
     * 文件比特率
     */
    private String bitrate;

    /**
     * 歌曲时长
     */
    private String trackLength;

    /**
     * 歌曲信息是否已整理，有歌手、专辑、标题就算是已整理
     */
    private Integer organized;

    public static MmsMusic reverseConvert(MusicInfoBO musicInfo) {
        MmsMusic mmsMusic = new MmsMusic();
        if (null == musicInfo) { return mmsMusic; }
        mmsMusic.setTitle(musicInfo.getTitle()).setArtist(musicInfo.getArtist()).setAlbum(musicInfo.getAlbum()).setYear(musicInfo.getYear())
                .setLanguage(musicInfo.getLanguage()).setLyrics(musicInfo.getLyrics()).setTrackNumber(musicInfo.getTrackNumber())
                .setCover(musicInfo.getCover()).setFormat(musicInfo.getFormat()).setBitrate(musicInfo.getBitrate()).setTrackLength(musicInfo.getTrackLength())
                .setOrganized(musicInfo.getOrganized()).setMd5(musicInfo.md5()).setLyricsLength(musicInfo.getLyricsLength()).setCoverInfo(musicInfo.getCoverInfo());
        return mmsMusic;
    }

    public static MusicInfoBO convert(MmsMusic mmsMusic) {
        MusicInfoBO musicInfo = new MusicInfoBO();
        if (null == mmsMusic) { return musicInfo; }
        musicInfo.setTitle(mmsMusic.getTitle()).setArtist(mmsMusic.getArtist()).setAlbum(mmsMusic.getAlbum()).setYear(mmsMusic.getYear())
                .setLanguage(mmsMusic.getLanguage()).setLyrics(mmsMusic.getLyrics()).setTrackNumber(mmsMusic.getTrackNumber())
                .setCover(mmsMusic.getCover()).setFormat(mmsMusic.getFormat()).setBitrate(mmsMusic.getBitrate()).setTrackLength(mmsMusic.getTrackLength())
                .setOrganized(mmsMusic.getOrganized()).setLyricsLength(mmsMusic.getLyricsLength()).setCoverInfo(mmsMusic.getCoverInfo());
        return musicInfo;
    }

    public String md5() {
        return DigestUtil.md5Hex(this.title + this.artist + this.album + this.year + this.language + this.lyrics + this.lyricsLength +
                this.trackNumber + DigestUtil.md5Hex(this.cover) + this.coverInfo + this.format + this.bitrate + this.trackLength + this.organized);
    }
}
