package com.rdminfo.mms.common.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.rdminfo.mms.common.constants.MmsMusicConstant;
import com.rdminfo.mms.common.constants.RegexConstant;
import com.rdminfo.mms.common.enums.FileType;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

/**
 * 音乐文件工具类
 *
 * @author admin 2023/12/06 11:31
 */
public class MusicFileUtil {

    public static boolean isMusicFile(String type) {
        if (null == type || type.length() == 0) { return false; }
        return FileType.MP3.getCode().equals(type) || FileType.FLAC.getCode().equals(type) || FileType.WAV.getCode().equals(type);
    }

    public static MusicInfoBO getMusicInfo(File file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException, SQLException {
        MusicInfoBO musicInfo = new MusicInfoBO();
        AudioFile audioFile = AudioFileIO.read(file); FileType musicType = FileType.find(audioFile.getExt());
        switch (musicType) {
            case MP3:
                MP3File mp3FileTmp = (MP3File) audioFile;
                getMusicHeadInfo(musicInfo, mp3FileTmp);
                if (mp3FileTmp.hasID3v2Tag()) {
                    getMusicInfoByV2Tag(musicInfo, mp3FileTmp);
                } else if (mp3FileTmp.hasID3v1Tag()) {
                    getMusicInfoByV1Tag(musicInfo, mp3FileTmp);
                }
                break;
            case FLAC:
                AudioHeader audioHeader = audioFile.getAudioHeader();
                getMusicHeadInfo(musicInfo, audioHeader);
                getMusicInfo(musicInfo, (FlacTag) audioFile.getTag());
                break;
            default:
                return null;
        }
        musicOrganizedCheck(musicInfo);
        musicLyricsLengthCheck(musicInfo);
        musicCoverInfoCheck(musicInfo);
        return musicInfo;
    }

    public static void setMusicInfo(MusicInfoBO musicInfo, File file, Set<String> tags) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException, CannotWriteException {
        AudioFile audioFile = AudioFileIO.read(file); Tag tag;
        FileType musicType = FileType.find(audioFile.getExt());
        if (musicType.equals(FileType.MP3) && ((MP3File) audioFile).hasID3v1Tag()) {
            tag = v1TagToV24Tag(audioFile);
        } else {
            tag = audioFile.getTag();
        }
        for (String tagStr : tags) {
            switch (tagStr) {
                case MmsMusicConstant.Tags.TITLE:
                    tag.setField(FieldKey.TITLE, musicInfo.getTitle());
                    break;
                case MmsMusicConstant.Tags.ARTIST:
                    tag.setField(FieldKey.ARTIST, musicInfo.getArtist());
                    break;
                case MmsMusicConstant.Tags.ALBUM:
                    tag.setField(FieldKey.ALBUM, musicInfo.getAlbum());
                    break;
                case MmsMusicConstant.Tags.YEAR:
                    tag.setField(FieldKey.YEAR, musicInfo.getYear());
                    break;
                case MmsMusicConstant.Tags.LANGUAGE:
                    tag.setField(FieldKey.LANGUAGE, musicInfo.getLanguage());
                    break;
                case MmsMusicConstant.Tags.LYRICS:
                    tag.setField(FieldKey.LYRICIST, musicInfo.getLyrics());
                    break;
                case MmsMusicConstant.Tags.TRACK_NUMBER:
                    tag.setField(FieldKey.TRACK, String.valueOf(musicInfo.getTrackNumber()));
                    break;
                default:
                    break;
            }
        }
        AudioFileIO.write(audioFile);
        musicOrganizedCheck(musicInfo);
        musicLyricsLengthCheck(musicInfo);
        musicCoverInfoCheck(musicInfo);
    }


    private static void musicOrganizedCheck(MusicInfoBO musicInfo) {
        if (StrUtil.isNotEmpty(musicInfo.getTitle()) && StrUtil.isNotEmpty(musicInfo.getAlbum()) && StrUtil.isNotEmpty(musicInfo.getArtist())) {
            musicInfo.setOrganized(MmsMusicConstant.Organized.YES);
        } else {
            musicInfo.setOrganized(MmsMusicConstant.Organized.NO);
        }
    }

    private static void musicLyricsLengthCheck(MusicInfoBO musicInfo) {
        if (StrUtil.isNotEmpty(musicInfo.getLyrics())) {
            musicInfo.setLyricsLength(musicInfo.getLyrics().length());
        }
    }

    private static void musicCoverInfoCheck(MusicInfoBO musicInfo) {
        if (null != musicInfo.getCover() && musicInfo.getCover().length > 0) {
            String coverInfo;
            try {
                InputStream inputStream = new ByteArrayInputStream(musicInfo.getCover());
                BufferedImage image = ImageIO.read(inputStream);
                if (image != null) {
                    String format = FileUtil.detectImageFormat(musicInfo.getCover()).toUpperCase(Locale.ROOT);
                    int width = image.getWidth();
                    int height = image.getHeight();
                    String imgSize = FileUtil.formatFileSize(musicInfo.getCover().length);
                    coverInfo = format + "|" + width + "*" + height + "," + imgSize;
                } else {
                    coverInfo = "Empty Cover";
                }
                IoUtil.close(inputStream);
            } catch (Exception e) {
                coverInfo = "Error Cover";
                e.printStackTrace();
            }
            musicInfo.setCoverInfo(coverInfo);
        } else {
            musicInfo.setCoverInfo("");
        }
    }



    private static void getMusicHeadInfo(MusicInfoBO musicInfo, MP3File mp3File) {
        MP3AudioHeader audioHeaderTmp = mp3File.getMP3AudioHeader();
        musicInfo.setFormat(audioHeaderTmp.getFormat()).setBitrate(audioHeaderTmp.getBitRate())
                .setTrackLength(audioHeaderTmp.getTrackLengthAsString());
    }

    private static void getMusicHeadInfo(MusicInfoBO musicInfo, AudioHeader audioHeader) {
        musicInfo.setFormat(audioHeader.getFormat()).setBitrate(audioHeader.getBitRate())
                .setTrackLength(String.format("%02d:%02d", audioHeader.getTrackLength() / 60, audioHeader.getTrackLength() % 60));
    }

    private static void getMusicInfoByV1Tag(MusicInfoBO musicInfo, MP3File mp3File) {
        getMusicInfo(musicInfo, mp3File.getID3v1Tag());
    }

    private static void getMusicInfoByV2Tag(MusicInfoBO musicInfo, MP3File mp3File) {
        getMusicInfo(musicInfo, mp3File.getID3v2TagAsv24());
    }

    private static void getMusicInfo(MusicInfoBO musicInfo, FlacTag tag) {
        musicInfo.setTitle(tag.getFirst(FieldKey.TITLE)).setArtist(tag.getFirst(FieldKey.ARTIST))
                .setAlbum(tag.getFirst(FieldKey.ALBUM)).setYear(tag.getFirst(FieldKey.YEAR))
                .setLanguage(tag.getFirst(FieldKey.LANGUAGE)).setLyrics(tag.getFirst(FieldKey.LYRICS))
                .setTrackNumber(tag.getFirst(FieldKey.TRACK).matches(RegexConstant.INTEGER) ? Integer.parseInt(tag.getFirst(FieldKey.TRACK)) : null)
                .setCover(tag.getFirstArtwork().getBinaryData());
    }

    private static void getMusicInfo(MusicInfoBO musicInfo, ID3v1Tag v1Tag) {
        musicInfo.setTitle(v1Tag.getFirst(FieldKey.TITLE)).setArtist(v1Tag.getFirst(FieldKey.ARTIST))
                .setAlbum(v1Tag.getFirst(FieldKey.ALBUM)).setYear(v1Tag.getFirst(FieldKey.YEAR));
    }

    private static void getMusicInfo(MusicInfoBO musicInfo, ID3v24Tag v2Tag) {
        musicInfo.setTitle(v2Tag.getFirst(ID3v24FieldKey.TITLE)).setArtist(v2Tag.getFirst(ID3v24FieldKey.ARTIST))
                .setAlbum(v2Tag.getFirst(ID3v24FieldKey.ALBUM)).setYear(v2Tag.getFirst(ID3v24FieldKey.YEAR))
                .setLanguage(v2Tag.getFirst(ID3v24FieldKey.LANGUAGE)).setLyrics(v2Tag.getFirst(ID3v24FieldKey.LYRICS))
                .setTrackNumber(v2Tag.getFirst(ID3v24FieldKey.TRACK).matches(RegexConstant.INTEGER) ? Integer.parseInt(v2Tag.getFirst(FieldKey.TRACK)) : null)
                .setCover(v2Tag.getFirstArtwork().getBinaryData());
    }

    private static ID3v24Tag v1TagToV24Tag(AudioFile audioFile) throws FieldDataInvalidException, IOException {
        MP3File mp3File = (MP3File) audioFile;
        if (!mp3File.hasID3v1Tag()) {
            return mp3File.getID3v2TagAsv24();
        }
        ID3v1Tag v1Tag = mp3File.getID3v1Tag();
        ID3v24Tag v2Tag = new ID3v24Tag();
        v2Tag.setField(FieldKey.TITLE, v1Tag.getFirst(FieldKey.TITLE)); v2Tag.setField(FieldKey.ARTIST, v1Tag.getFirst(FieldKey.ARTIST));
        v2Tag.setField(FieldKey.ALBUM, v1Tag.getFirst(FieldKey.ALBUM)); v2Tag.setField(FieldKey.YEAR, v1Tag.getFirst(FieldKey.YEAR));
        mp3File.delete(v1Tag); mp3File.setID3v2Tag(v2Tag); mp3File.setTag(v2Tag);
        return v2Tag;
    }

}
