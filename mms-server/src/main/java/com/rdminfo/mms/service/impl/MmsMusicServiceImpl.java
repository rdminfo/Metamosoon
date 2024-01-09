package com.rdminfo.mms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rdminfo.mms.common.constants.MmsMusicConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.mapper.MmsMusicMapper;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import com.rdminfo.mms.common.pojo.po.MusicListQueryPO;
import com.rdminfo.mms.common.pojo.vo.MusicAsDetailVO;
import com.rdminfo.mms.common.pojo.vo.MusicAsListItemVO;
import com.rdminfo.mms.common.pojo.vo.MusicAsListVO;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.result.exception.CommonException;
import com.rdminfo.mms.common.utils.MusicFileUtil;
import com.rdminfo.mms.service.IMmsMusicService;
import com.rdminfo.mms.support.FileSupport;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-05
 */
@Service
public class MmsMusicServiceImpl extends ServiceImpl<MmsMusicMapper, MmsMusic> implements IMmsMusicService {

    @Resource
    private MmsMusicMapper musicMapper;

    @Resource
    private FileSupport fileSupport;

    @Override
    public MusicAsListVO getList(Long folderId, MusicListQueryPO queryParam) {
        List<MusicAsListItemVO> listRes = musicMapper.selectUnderFolderList(folderId, queryParam);
        MusicAsListVO resVo = new MusicAsListVO();
        for (MusicAsListItemVO itemTmp : listRes) {
            if (!MusicFileUtil.isMusicFile(itemTmp.getType())) {
                continue;
            }
            resVo.addArtist(itemTmp.getArtist());
            resVo.addAlbum(itemTmp.getAlbum());
            resVo.addYear(itemTmp.getYear());
        }
        if (listRes.size() > 0) {
            resVo.addItems(listRes);
        }
        return resVo;
    }

    @Override
    public void updateInfo(List<MmsFiles> updateFileList, List<MusicInfoBO> updateMusicInfoList, List<Set<String>> updateColumns) {
        if (null == updateFileList || null == updateMusicInfoList || null == updateColumns ||
                updateFileList.size() != updateMusicInfoList.size() || updateMusicInfoList.size() != updateColumns.size()) {
            return;
        }
        MmsFiles updFileTmp; String filePath; File updFile; Set<String> updateColumnsTmp; MusicInfoBO musicInfoTmp;
        for (int i = 0; i < updateFileList.size(); i++) {
            updFileTmp = updateFileList.get(i); filePath = updFileTmp.getAbsolutePath();
            if (!FileUtil.exist(filePath)) {
                continue;
            }
            updFile = FileUtil.file(filePath); musicInfoTmp = updateMusicInfoList.get(i); updateColumnsTmp = updateColumns.get(i);
            try {
                MusicFileUtil.setMusicInfo(musicInfoTmp, updFile, updateColumnsTmp);
            } catch (Exception e) {
                throw new CommonException(CommonResultCode.MUSIC_INFO_UPDATE_ERROR);
            }
            UpdateWrapper<MmsMusic> update = new UpdateWrapper<>();
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.TITLE), MmsMusic.TITLE, musicInfoTmp.getTitle());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.ARTIST), MmsMusic.ARTIST, musicInfoTmp.getArtist());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.ALBUM), MmsMusic.ALBUM, musicInfoTmp.getAlbum());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.YEAR), MmsMusic.YEAR, musicInfoTmp.getYear());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.LANGUAGE), MmsMusic.LANGUAGE, musicInfoTmp.getLanguage());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.LYRICS), MmsMusic.LYRICS, musicInfoTmp.getLyrics());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.TRACK_NUMBER), MmsMusic.TRACK_NUMBER, musicInfoTmp.getTrackNumber());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.COVER), MmsMusic.COVER, musicInfoTmp.getCover());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.COVER), MmsMusic.COVER_INFO, musicInfoTmp.getCoverInfo());
            update.set(updateColumnsTmp.contains(MmsMusicConstant.Tags.COVER_ART), MmsMusic.COVER_ART, musicInfoTmp.getCoverArt());
            update.eq(MmsMusic.FILE_ID, updFileTmp.getId());
            this.update(update);
        }
    }

    @Override
    public MusicAsDetailVO getDetail(long fileId) {
        MusicAsDetailVO resVo = new MusicAsDetailVO();
        MmsFiles musicFile = fileSupport.getById(fileId, MmsFiles.ID);
        if (null == musicFile) {
            return resVo;
        }

        QueryWrapper<MmsMusic> musicQuery = new QueryWrapper<>();
        musicQuery.select(MmsMusic.ID, MmsMusic.FILE_ID, MmsMusic.TITLE, MmsMusic.ARTIST, MmsMusic.ALBUM, MmsMusic.YEAR, MmsMusic.LANGUAGE,
                MmsMusic.LYRICS, MmsMusic.LYRICS_LENGTH, MmsMusic.TRACK_NUMBER, MmsMusic.COVER, MmsMusic.COVER_INFO, MmsMusic.FORMAT, MmsMusic.BITRATE,
                MmsMusic.TRACK_LENGTH, MmsMusic.ORGANIZED);
        musicQuery.eq(MmsMusic.FILE_ID, fileId).last(" limit 1");
        MmsMusic musicInfo = musicMapper.selectOne(musicQuery);
        if (null == musicInfo) {
            return new MusicAsDetailVO();
        }
        MusicInfoBO musicInfoBo = MusicInfoBO.convert(musicInfo);
        BeanUtil.copyProperties(musicInfoBo, resVo);
        resVo.setId(musicInfo.getId()).setFileId(musicInfo.getFileId());
        return resVo;
    }

    @Override
    public byte[] getCover(long fileId) {
        MmsFiles file = fileSupport.getById(fileId, MmsFiles.ID);
        if (null == file) {
            return new byte[]{};
        }

        QueryWrapper<MmsMusic> musicQuery = new QueryWrapper<>();
        musicQuery.select(MmsMusic.ID, MmsMusic.COVER);
        musicQuery.eq(MmsMusic.FILE_ID, fileId).last(" limit 1");
        MmsMusic musicInfo = musicMapper.selectOne(musicQuery);
        if (null == musicInfo) {
            return new byte[]{};
        }

        return null == musicInfo.getCover() ? new byte[]{} : musicInfo.getCover();
    }
}
