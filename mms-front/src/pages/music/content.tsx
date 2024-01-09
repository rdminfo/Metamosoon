import React, {useEffect, useMemo, useRef, useState} from 'react';
import {
  Button,
  Card,
  Checkbox,
  Divider,
  Grid,
  Input,
  Message,
  Modal,
  Space,
  Typography
} from '@arco-design/web-react';
import cardStyles from '@/pages/music/style/card.module.less';
import contentStyle from '@/pages/music/style/content.module.less';
import {
  IconCheck,
  IconDelete,
  IconDownload,
  IconDragArrow,
  IconEdit,
  IconEye,
  IconFilter,
  IconFolderAdd,
  IconMenu,
  IconMinusCircle, IconPlus,
  IconRight,
  IconSearch,
  IconSort,
  IconUpload,
} from '@arco-design/web-react/icon';
import useLocale from '@/utils/useLocale';
import locale from '@/pages/music/locale';
import globalLocale from '@/locale';
import IconFolderCustom from './assets/folder.svg';
import IconMusicCustom from './assets/music.svg';
import {getSortTypeConfig, getViewTypeConfig,} from '@/pages/music/config/content.list.select';
import {MusicInfo} from '@/pages/music/types/MusicInfo';
import useSWRMutation from 'swr/mutation';
import {useSelector} from 'react-redux';
import {RootState} from '@/store/types/RootState';
import {
  deleteFile, delMusicCover,
  fetchMusicDetail,
  fetchMusicList,
  moveFile,
  musicSave,
  saveFolder,
  updFolder, updMusicLyrics,
  zipFile
} from '@/service/music';
import {MUSIC_ITEM_HEIGHT} from "@/config/dom";
import cs from 'classnames';
import store from "@/store";
import {UPDATE_CURRENT_FOLDER} from "@/store/constants";
import {MusicListApiParam} from "@/pages/music/types/MusicListApiParam";
import {FileSelectList} from "@/pages/music/types/FileSelectList";
import {mutate} from "swr";
import {deleteConfirm} from "@/utils/modal";
import MultipleUploadPopover from "@/components/Popover/MultipleUploadPopover";
import {createUrl} from "@/service/api";
import FileSelectPopover from "@/components/Popover/FileSelectPopover";
import {arraysEqual} from "@/utils/array";
import ContentListItem from "@/pages/music/components/content-list-item";
import {MusicSaveApiParam} from "@/pages/music/types/MusicSaveApiParam";
import {formatFileSize, isMusicFileType} from "@/utils/file";
import GroupSelect from "@/components/Select/GroupSelect";
import {MusicListCondition} from "@/pages/music/types/MusicListCondition";
import {FileType, FilterCategory, Sort, TagRange, ViewType} from "@/pages/music/enums/MusicListCondition";
import {GroupSelectData} from "@/components/Select/type/GroupSelectData";
import SearchSelect from "@/components/Select/SearchSelect";
import {SelectItemData} from "@/components/Select/type/SelectItemData";
import {MusicListFilter} from "@/pages/music/types/MusicListFilter";
import ImageUploadPopover from "@/components/Popover/ImageUploadPopover";
import ContentLyricEdit from "@/pages/music/components/content-lyric-edit";

const Content = (props) => {
  const { height } = props;
  const t = useLocale(locale);
  const tg = useLocale(globalLocale);
  const sortType = getSortTypeConfig(t, tg);
  const viewTypes = getViewTypeConfig(t);
  const listOuterRef = useRef(null);

  const { currenFolder, aggregateCondition } = useSelector((state: RootState) => ({
    currenFolder: state.currentFolder.data,
    aggregateCondition: state.aggregateCondition
  }));

  const { data, trigger, isMutating } = useSWRMutation('fetchMusicList', fetchMusicList);

  const musicListOriginData = useMemo<MusicInfo[]>(() => data?.data?.items, [data]);

  const [musicListData, setMusicListData] = useState<MusicInfo[]>([]);
  const [currenPageFolder, setCurrenPageFolder] = useState<string>('');
  const [currenTagRange, setCurrenTagRange] = useState<string>('');
  const [listExceedOuter, setListExceedOuter] = useState<boolean>(false);
  const [fileSelected, setFileSelected] = useState<FileSelectList>({ folderId: '', folderName: '', items: [] });
  const [moveMode, setMoveMode] = useState<boolean>(false);
  const [moving, setMoving] = useState<boolean>(false);
  const [deleting, setDeleting] = useState<boolean>(false);
  const [downloading, setDownloading] = useState<boolean>(false);
  const [saving, setSaving] = useState<boolean>(false);
  const [dataChange, setDataChange] = useState<boolean>(false);
  const [listCondition, setListCondition] = useState<MusicListCondition>({});
  const [listFilter, setListFilter] = useState<MusicListFilter>({});
  const [resetSingle, setResetSingle] = useState<number>(0);
  const [filterResetSingle, setFilterResetSingle] = useState<number>(0);
  const [artistFilterData, setArtistFilterData] = useState<string[]>([]);
  const [albumFilterData, setAlbumFilterData] = useState<string[]>([]);
  const [yearFilterData, setYearFilterData] = useState<string[]>([]);
  const [lyricsEditVisible, setLyricsEditVisible] = useState<boolean>(false);
  const [lyricsEditInfo, setLyricsEditInfo] = useState<{ musicInfo?: MusicInfo, lyrics?: string }>({});

  const { selected, selectAll, setSelected, unSelectAll, isAllSelected, isPartialSelected } =
    Checkbox.useCheckbox(musicListData.map((item) => item.fileId));

  const selectedModeDisable = useMemo<boolean>(() =>
      fileSelected && fileSelected.items && fileSelected.items.length > 0 && fileSelected.folderId !== currenFolder.id,
    [fileSelected, currenFolder]);

  // 列表内容获取
  useEffect(() => {
    console.log('useEffect currenFolder, aggregateCondition, listCondition');
    setMusicListData([]);
    let filterParam = { ...listFilter };
    if (currenFolder.id != currenPageFolder) {
      filterParam = {};
      setCurrenPageFolder(currenFolder.id);
      // 每次文件夹切换都重置筛选按钮
      setFilterResetSingle(filterResetSingle + 1);
      setListFilter({});
    }
    const params: MusicListApiParam = {
      folderId: currenFolder.id,
      organized: aggregateCondition.organized.value,
      hasAlbum: aggregateCondition.album.value,
      hasCover: aggregateCondition.cover.value,
      hasLyric: aggregateCondition.lyric.value,
      hasYear: aggregateCondition.year.value,
      ...listCondition, ...filterParam
    }
    trigger(params).then((res) => {
      setMusicListData(res.data.items);
      // 切换文件夹或者切换tag范围时Filter数据
      if (currenFolder.id != currenPageFolder || currenTagRange !== listCondition.tagRange) {
        setArtistFilterData(res.data.artists || []);
        setAlbumFilterData(res.data.albums || []);
        setYearFilterData(res.data.years || []);
      }
      if (currenTagRange !== listCondition.tagRange) {
        setCurrenTagRange(listCondition.tagRange)
      }
    })
    if (fileSelected && fileSelected.selectCount > 0 && fileSelected.folderId !== currenFolder.id) {
      setMoveMode(true)
    }
  }, [currenFolder, aggregateCondition, listCondition]);

  useEffect(() => {
    console.log('useEffect musicListData');
    if (listOuterRef.current) {
      // 动态宽度设置，目的是解决内容超出div滚动条扰乱布局的问题
      const listOuterHeight = listOuterRef.current.getBoundingClientRect().height;
      if (musicListData) {
        const listHeight = musicListData.length * MUSIC_ITEM_HEIGHT + 10
        setListExceedOuter(listHeight >= listOuterHeight)
      }
    }
    const change = musicListData && musicListData.length > 0 && !arraysEqual(musicListData, musicListOriginData, ['updateColumns', 'coverInfo', 'lyrics', 'lyricsLength']);
    setDataChange(change);
  }, [musicListData]);

  // 检测选择
  useEffect(() => {
    console.log('useEffect fileSelected.selectCount');
    if (fileSelected.selectCount === 0) { setMoveMode(false) }
  }, [fileSelected.selectCount]);

  function handleEdit(fileId: string, column: string, value: string) {
    console.log('handleEdit')
    const editIndex = musicListData.findIndex(item => item.fileId === fileId);
    const originIndex = musicListOriginData.findIndex(item => item.fileId === fileId);
    setMusicListData(prevState => {
      const setState = [...prevState];
      const oldVal = musicListOriginData[originIndex]?.[column] || 'O_o';
      if (editIndex !== -1) {
        const setData = { ...setState[editIndex], [column]: value };
        const updColumns = setData.updateColumns || [];
        const updColumnIndex = updColumns.indexOf(column);
        if (value === oldVal && updColumnIndex !== -1) {
          updColumns.splice(updColumnIndex, 1);
        } else if (value !== oldVal && updColumnIndex === -1) {
          updColumns.push(column);
        }
        setData.updateColumns = updColumns;
        setState[editIndex] = setData;
      }
      return setState;
    });
  }

  // 从服务器更新本地的某个音乐的某个列信息
  async function onListItemUpdate(fileId: string, columns: (keyof MusicInfo)[]) {
    console.log('onListItemUpdate');
    const res = await fetchMusicDetail(fileId);
    if (res && res.data) {
      const musicInfo = res.data;
      setMusicListData(prevState => {
        const newState = [ ...prevState ];
        const updMusicIndex = newState.findIndex(item => item.fileId === fileId);

        const newData = { ...newState[updMusicIndex] } as MusicInfo;
        columns.forEach(item => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          newData[item] = musicInfo[item];
          console.log(item + ' reset, value = ' + musicInfo[item]);
        });
        newState[updMusicIndex] = newData;
        return newState;
      });
    }
  }

  // 文件夹切换
  function folderLoad(folderId: string, folderName?: string) {
    console.log('folderLoad')
    store.dispatch({
      type: UPDATE_CURRENT_FOLDER,
      payload: { data: { id: folderId, name: folderName || '' } },
    });
  }

  // 文件全选
  function onSelectAll(checked) {
    console.log('onSelectAll')
    if (checked) { selectAll(); } else { unSelectAll(); }
    let folderCount = 0, fileCount = 0;
    const fileSelectedItems = checked ? musicListData.map((item) => {
      if (item.type === 'folder') { folderCount ++; } else { fileCount ++; }
      return ({ fileId: item.fileId, type: item.type, name: item.fileName })
    }) : [];
    setFileSelected({
      folderId: currenFolder.id,
      folderName: currenFolder.name,
      items: fileSelectedItems,
      selectFolderCount: folderCount,
      selectFileCount: fileCount,
      selectCount: folderCount + fileCount,
    });
  }

  // 文件选择
  function onSelect(value) {
    console.log('onSelect')
    setSelected(value)
    let folderCount = 0; let fileCount = 0;
    const fileSelectedItems = value.length > 0 ? value.map((fileId) => {
      const fileItem = musicListData.find((t) => t.fileId === fileId);
      if (fileItem.type == 'folder') { folderCount ++; } else { fileCount ++ }
      return ({ fileId: fileItem.fileId, type: fileItem.type, name: fileItem.fileName })
    }) : [];
    setFileSelected({
      folderId: currenFolder.id,
      folderName: currenFolder.name,
      items: fileSelectedItems,
      selectFolderCount: folderCount,
      selectFileCount: fileCount,
      selectCount: folderCount + fileCount,
    });
  }

  // 文件取消选择
  function onRemoveSelect(value: string[]) {
    console.log('onRemoveSelect')
    if (value && value.length === 0 || (fileSelected && fileSelected.selectCount && fileSelected.selectCount == 0)) { return; }
    const selectCount = fileSelected.selectCount;
    const allRemoved = selectCount === value.length;
    const updateItems = [], selectFileIds = [];let folderCount = 0; let fileCount = 0;
    fileSelected.items.forEach(item => {
      if (!value.includes(item.fileId)) {
        selectFileIds.push(item.fileId)
        if (item.type == 'folder') { folderCount ++; } else { fileCount ++ }
        updateItems.push({ fileId: item.fileId, type: item.type, name: item.name })
      }
    })
    setSelected(selectFileIds);
    setFileSelected({
      folderId: currenFolder.id,
      folderName: currenFolder.name,
      items: allRemoved ? [] : updateItems,
      selectFolderCount: allRemoved ? 0 : folderCount,
      selectFileCount: allRemoved ? 0 : fileCount,
      selectCount: allRemoved ? 0 : folderCount + fileCount,
    });
  }

  // 文件夹空选检测
  function selectEmptyCheck () {
    console.log('selectEmptyCheck')
    if (!fileSelected.selectCount || fileSelected.selectCount === 0) {
      Message.warning(t['music.ope.notification.noFileSelected'])
      return false;
    }
    return true;
  }

  // 文件操作后动清空选择
  async function fileOpeFinished() {
    console.log('fileOpeFinished')
    unSelectAll();
    setFileSelected({
      folderId: currenFolder.id,
      folderName: currenFolder.name,
      items: [],
      selectFolderCount: 0,
      selectFileCount: 0,
      selectCount: 0,
    });
    folderLoad(currenFolder.id, currenFolder.name);
    await mutate('fetchFolderTreeList');
  }

  // 文件移动
  async function onMoveFile() {
    console.log('onMoveFile')
    if (!selectEmptyCheck()) { return;  }
    if (!moveMode) { setMoveMode(true); return; }
    setMoving(true);
    await moveFile({
      toFolderId: currenFolder.id,
      fileIds: fileSelected.items.map(item => item.fileId)
    });
    setMoveMode(false);setMoving(false);
    await fileOpeFinished();
  }

  // 删除文件
  async function onRemoveFile() {
    console.log('onRemoveFile')
    if (!selectEmptyCheck()) { return;  }
    deleteConfirm({
      tg, onOk:async () => {
        setDeleting(true)
        const fileIds = fileSelected.items.map(item => item.fileId).join(',');
        await deleteFile(fileIds);
        await fileOpeFinished();
        setDeleting(false);
      }
    });
  }

  // 文件下载
  async function onDownloadFile() {
    console.log('onDownloadFile')
    if (!selectEmptyCheck()) { return;  }
    setDownloading(true);
    const fileIds = fileSelected.items.map(item => item.fileId).join(',');
    const response = await zipFile(fileIds);
    const fileMd5 = response && response.data ? response.data : '';
    if (fileMd5) {
      const downloadUrl = createUrl(`/files/download/tmp/${fileMd5}`);
      const link = document.createElement('a');
      link.href = downloadUrl;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
    setDownloading(false);
  }

  // 新增文件夹
  async function onNewFolder() {
    console.log('onNewFolder')
    const folderData: MusicInfo = {
      type: 'folder',
      fileName: '',
      fileId: `${new Date().getTime()}${Math.random() * 10000000000000000}`,
      newItem: true,
    }
    const newData = [folderData, ...musicListData];
    setMusicListData(newData)
  }

  // 保存
  async function onSave() {
    console.log('onSave')
    if (!dataChange) {
      setDataChange(false);
      return;
    }
    const folderSaveData: string[] = [];
    const folderIdUpdData: string[] = [];
    const folderNameUpdData: string[] = [];
    const musicSaveData: MusicSaveApiParam = { items: [] };
    for (const item of musicListData) {
      if (!item.fileName) {
        Message.warning(tg['notification.fileName.notEmpty'])
        return;
      }
      if (item.type === 'folder' && item.newItem) {
        folderSaveData.push(item.fileName);
      } else if (item.type === 'folder') {
        folderIdUpdData.push(item.fileId);
        folderNameUpdData.push(item.fileName);
      } else if (isMusicFileType(item.type) && item && item.updateColumns && item.updateColumns.length > 0) {
        musicSaveData.items.push({
          fileId: item.fileId, fileName: item.fileName, title: item.title, artist: item.artist,
          album: item.album, year: item.year, trackNumber: item.trackNumber, updateColumns: item.updateColumns || []
        });
      }
    }
    if(folderSaveData.length > 0) {
      await saveFolder(currenFolder.id, folderSaveData.join(','));
    }
    if(folderIdUpdData.length > 0) {
      await updFolder(folderIdUpdData.join(','), folderNameUpdData.join(','));
    }
    if (musicSaveData.items.length > 0) {
      await musicSave(musicSaveData);
    }
    setSaving(false);
    await fileOpeFinished();
  }

  // 删除本地数据
  async function onRemoveLocal(fileId: string) {
    console.log('onRemoveLocal')
    const newData = musicListData.filter(item => !item.newItem && item.fileId !== fileId);
    setMusicListData(newData)
  }

  // 列表数据查找条件
  function onSelectConditionChange(selectData: GroupSelectData[]) {
    console.log('onSelectConditionChange')
    const newCondition = {...listCondition};
    if (selectData && selectData.length > 0) {
      selectData.forEach(item => {
        if (item.groupInfo.code === ViewType.TAG_RANGE) {
          newCondition.tagRange = item.items && item.items.length > 0 ? item.items[0].value : TagRange.BASIC;
        } else if (item.groupInfo.code === ViewType.FILE_TYPE) {
          newCondition.fileType = item.items && item.items.length > 0 ? item.items[0].value : FileType.DEFAULT;
        } else if (item.groupInfo.code === Sort.CATEGORY) {
          newCondition.sortCategory = item.items && item.items.length > 0 ? item.items[0].value : undefined;
        } else if (item.groupInfo.code === Sort.ORDER) {
          newCondition.sortOrder = item.items && item.items.length > 0 ? item.items[0].value : undefined;
        }
      })
      setListCondition(newCondition)
    }
  }

  // 列表数据过滤条件
  function onFilterChanged(data: SelectItemData[] | string[], filterCategory: FilterCategory) {
    const newListFilter = {...listFilter};
    console.log('onFilterChanged');
    if (data && data.length > 0) {
      const dataVal = data[0];
      const value = typeof dataVal === 'string' ? dataVal : dataVal.value;
      if (filterCategory === FilterCategory.ARTIST) {
        newListFilter.artist = value
      } else if (filterCategory === FilterCategory.ALBUM) {
        newListFilter.album = value
      } else if (filterCategory === FilterCategory.YEAR) {
        newListFilter.year = value
      }
      folderLoad(currenFolder.id, currenFolder.name);
    }
    setListFilter(newListFilter);
  }

  return (
    <Card className={contentStyle['content']} style={{ height: height }}>
      {/* 列表顶部操作栏和排序方式等 */}
      <Space className={`${cardStyles['nav-bar']} ${contentStyle['content-nav']}`}>
        <Space size={'small'}>
          <Button.Group>
            <Button
              icon={moveMode ? <IconCheck style={{ color: 'rgb(var(--arcoblue-5))' }} /> : <IconDragArrow />}
              loading={moving} onClick={() => onMoveFile()}
            />
            <Button icon={<IconDelete />} loading={deleting} onClick={() => onRemoveFile()} />
            <MultipleUploadPopover folderInfo={{ id: currenFolder.id, name: currenFolder.name }}
                                   folderClick={(folderInfo) => folderLoad(folderInfo.id, folderInfo.name)}
                                   action={createUrl('/files/upload/music')}
                                   trigger={"click"} position={"rt"}>
              <Button icon={<IconUpload />} />
            </MultipleUploadPopover>
            <Button icon={<IconDownload />} loading={downloading} onClick={() => onDownloadFile()} />
            <Button icon={<IconFolderAdd />} onClick={() => onNewFolder()} />
          </Button.Group>
          <Divider type="vertical" style={{ margin: '0 8px', marginRight: '6px' }}
          />
          <GroupSelect options={viewTypes} onChange={(selectData) => onSelectConditionChange(selectData)} resetSingle={resetSingle}>
            <Button className={contentStyle['top-select-button']} icon={<IconMenu />} size={'small'}>
              {t['music.content.top.viewType']}
            </Button>
          </GroupSelect>
          <GroupSelect options={sortType}  onChange={(selectData) => onSelectConditionChange(selectData)} resetSingle={resetSingle}>
            <Button className={contentStyle['top-select-button']} icon={<IconSort />} size={'small'}>
              {tg['sort']}
            </Button>
          </GroupSelect>
          <Button className={contentStyle['top-select-button']} type={'text'} size={'mini'}
                  onClick={() => {
                    setListCondition({});
                    setListFilter({});
                    setResetSingle(resetSingle + 1);
                    setFilterResetSingle(filterResetSingle + 1);
                  }}>
            {tg['ope.reset']}
          </Button>
          <Space size={"mini"}>
            { fileSelected && (fileSelected.selectFolderCount > 0 || fileSelected.selectFileCount > 0) ?
              <div className={`${contentStyle['content-nav-select-info']}`}>
                <FileSelectPopover selectFileList={fileSelected}
                                   onToFolderClick={folderInfo => folderLoad(folderInfo.id, folderInfo.name)}
                                   onFileRemove={fileIds => onRemoveSelect(fileIds)}>
                  <div className={`${contentStyle['content-nav-select-info-text']}`}>
                    { fileSelected && (fileSelected.selectFolderCount > 0 || fileSelected.selectFileCount > 0) ? <Typography.Text underline>{t['music.content.top.selected']}</Typography.Text> : '' }
                    { fileSelected && fileSelected.selectFolderCount > 0 ? <Typography.Text underline>{fileSelected.selectFolderCount}{t['music.content.top.selected.folders']}</Typography.Text> : '' }
                    { fileSelected && (fileSelected.selectFolderCount > 0 && fileSelected.selectFileCount > 0) ? <Typography.Text underline>, </Typography.Text> : '' }
                    { fileSelected && fileSelected.selectFileCount > 0 ? <Typography.Text underline>{fileSelected.selectFileCount}{t['music.content.top.selected.files']}</Typography.Text> : '' }
                  </div>
                </FileSelectPopover>
                <Button
                  type={'text'} size={'mini'} style={{ padding: '0 2px' }}
                  onClick={() => {
                    onRemoveSelect(fileSelected.items.map(item => item.fileId))
                  }}
                >
                  {tg['ope.cancel']}
                </Button>
              </div>
              : '' }
          </Space>
        </Space>
        <Space className={`${contentStyle['content-nav-right']}`} size={"mini"}>
          {
            dataChange ? (
              <Button className={contentStyle['top-select-button']} type={'text'} size={'mini'} onClick={onSave} loading={saving} >
                {tg['ope.save']}
              </Button>
            ) : undefined
          }
        </Space>
      </Space>
      {/* 列表内容 */}
      <div className={contentStyle['list-outer']} ref={listOuterRef}>
        {/* 列表标头 */}
        <div className={contentStyle['list-header-outer']}>
          <Grid.Row
            className={`${contentStyle['list-default-head-row']} ${contentStyle['list-default-row']}`}
          >
            <ContentListItem colSize={1} head style={{paddingLeft: '0px'}}>
              <Checkbox checked={isAllSelected() && musicListData && musicListData.length > 0}
                        indeterminate={isPartialSelected()}
                        onChange={onSelectAll}
                        disabled={selectedModeDisable}>
              </Checkbox>
            </ContentListItem>
            <ContentListItem colSize={5} head>
              <Typography.Text>{tg['music.fileName']}</Typography.Text>
            </ContentListItem>
            {
              !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                <ContentListItem colSize={4} head>
                  <Typography.Text>{tg['music.songName']}</Typography.Text>
                </ContentListItem>
              ) : (
                <ContentListItem colSize={6} head>
                  <Typography.Text>{tg['music.cover']}</Typography.Text>
                </ContentListItem>
              )
            }
            {
              !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                <ContentListItem colSize={3} head>
                  <Typography.Text>{tg['music.artist']}</Typography.Text>
                  <SearchSelect options={artistFilterData} onChange={(data) => onFilterChanged(data, FilterCategory.ARTIST)} resetSingle={filterResetSingle}>
                    <IconFilter
                      className={cs(contentStyle['list-default-head-filter'], { [contentStyle['list-default-head-filter-active']]: !!listFilter.artist })}
                    />
                  </SearchSelect>
                </ContentListItem>
              ) : (
                <ContentListItem colSize={2} head>
                  <Typography.Text>{tg['music.lyric']}</Typography.Text>
                </ContentListItem>
              )
            }
            {
              !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                <ContentListItem colSize={5} head>
                  <Typography.Text>{tg['music.album']}</Typography.Text>
                  <SearchSelect options={albumFilterData} onChange={(data) => onFilterChanged(data, FilterCategory.ALBUM)} resetSingle={filterResetSingle}>
                    <IconFilter
                      className={cs(contentStyle['list-default-head-filter'], { [contentStyle['list-default-head-filter-active']]: !!listFilter.album })}
                    />
                  </SearchSelect>
                </ContentListItem>
              ) : (
                <ContentListItem colSize={2} head>
                  <Typography.Text>{tg['music.trackLength']}</Typography.Text>
                </ContentListItem>
              )
            }
            {
              !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                <ContentListItem colSize={2} head>
                  <Typography.Text>{tg['music.trackNumber']}</Typography.Text>
                </ContentListItem>
              ) : (
                <ContentListItem colSize={3} head>
                  <Typography.Text>{tg['music.size']}</Typography.Text>
                </ContentListItem>
              )
            }
            {
              !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                <ContentListItem colSize={3} head>
                  <Typography.Text>{tg['music.year']}</Typography.Text>
                  <SearchSelect options={yearFilterData} onChange={(data) => onFilterChanged(data, FilterCategory.YEAR)} resetSingle={filterResetSingle}>
                    <IconFilter
                      className={cs(contentStyle['list-default-head-filter'], { [contentStyle['list-default-head-filter-active']]: !!listFilter.year })}
                    />
                  </SearchSelect>
                </ContentListItem>
              ) : (
                <ContentListItem colSize={2} head>
                  <Typography.Text>{tg['music.format']}</Typography.Text>
                </ContentListItem>
              )
            }
            {
              !listCondition.tagRange || listCondition.tagRange !== TagRange.BASIC && (
                <ContentListItem colSize={2} head>
                  <Typography.Text>{tg['music.bitrate']}</Typography.Text>
                </ContentListItem>
              )
            }
            <ContentListItem colSize={1} style={{ paddingLeft: '3px' }} head>
              <Button icon={<IconSearch />} type={'text'} size={'mini'} />
            </ContentListItem>
          </Grid.Row>
        </div>
        {/* 列表内容 */}
        <div className={contentStyle['list-content-outer']}>
          {isMutating ? (
            ''
          ) : (
            <Checkbox.Group
              className={contentStyle['list-default-content']}
              value={selected}
              onChange={onSelect}
            >
              {musicListData.map((item, index) => (
                <Grid.Row
                  className={cs(contentStyle['list-default-content-row'], contentStyle['list-default-row'], {
                    [contentStyle['list-default-row-exceed']]: listExceedOuter,
                  })}
                  key={item.fileId}
                >
                  <ContentListItem colSize={1} content>
                    <Checkbox key={item.fileId} value={item.fileId} disabled={item.newItem || selectedModeDisable}></Checkbox>
                  </ContentListItem>
                  <ContentListItem colSize={5} content>
                    <Space size={'mini'} className={contentStyle['list-default-content-space']}>
                      {item.type === 'folder' ? (
                        <IconFolderCustom onClick={() => {
                          if (!item.newItem) {
                            folderLoad(item.fileId, item.fileName);
                          }
                        }} />
                      ) : (
                        <IconMusicCustom />
                      )}
                      <Input
                        className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.fileName}
                        onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                          handleEdit(item.fileId, 'fileName', e.target.value || '')
                        }
                      />
                    </Space>
                  </ContentListItem>
                  {
                    !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                      <ContentListItem colSize={4} content>
                        {item.type !== 'folder' && (
                          <Input
                            className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.title}
                            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                              handleEdit(item.fileId, 'title', e.target.value || '')
                            }
                          />
                        )}
                      </ContentListItem>
                    ) : (
                      <ContentListItem colSize={6} style={{ paddingLeft: '8px' }} content>
                        {item.type !== 'folder' && (
                          <Space size={"mini"}>
                            <Typography.Text ellipsis style={{ margin: 0 }}>{item.coverInfo}</Typography.Text>
                            {
                              isMusicFileType(item.type) && (
                                <ImageUploadPopover trigger={"click"} position={"right"}
                                  uploadUrl={createUrl(`/music/cover/${item.fileId}`)}
                                  imgUrl={item.coverInfo ? createUrl(`/music/cover/${item.fileId}`) : ''}
                                  onImgChange={async () => {
                                    await onListItemUpdate(item.fileId, ['coverInfo'])
                                  }}
                                  onImgRemove={async () => {
                                    await delMusicCover(item.fileId);
                                    await onListItemUpdate(item.fileId, ['coverInfo'])
                                  }}
                                >
                                  <Button
                                    className={contentStyle['list-default-content-col-btn']}
                                    icon={!!item.coverInfo ? <IconEye /> : <IconPlus />}
                                    type={'text'}
                                    size={'mini'}
                                  />
                                </ImageUploadPopover>
                              )
                            }
                          </Space>
                        )}
                      </ContentListItem>
                    )
                  }
                  {
                    !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                      <ContentListItem colSize={3} content>
                        {item.type !== 'folder' && (
                          <Input
                            className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.artist}
                            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                              handleEdit(item.fileId, 'artist', e.target.value || '')
                            }
                          />
                        )}
                      </ContentListItem>
                    ) : (
                      <ContentListItem colSize={2} style={{ paddingLeft: '10px' }} content>
                        {item.type !== 'folder' && (
                          <Button
                            className={contentStyle['list-default-content-col-btn']}
                            icon={item.lyricsLength && item.lyricsLength > 0 ? <IconEdit /> : <IconPlus />}
                            type={'text'}
                            size={'mini'}
                            onClick={() => {
                              setLyricsEditInfo({musicInfo: item, lyrics: item.lyrics});
                              setLyricsEditVisible(true);
                            }}
                          />
                        )}
                      </ContentListItem>
                    )
                  }
                  {
                    !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                      <ContentListItem colSize={5} content>
                        {item.type !== 'folder' && (
                          <Input
                            className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.album}
                            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                              handleEdit(item.fileId, 'album', e.target.value || '')
                            }
                          />
                        )}
                      </ContentListItem>
                    ) : (
                      <ContentListItem colSize={2} style={{ paddingLeft: '8px' }} content>
                        {item.type !== 'folder' && (
                          <Typography.Text>{item.trackLength}</Typography.Text>
                        )}
                      </ContentListItem>
                    )
                  }
                  {
                    !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                      <ContentListItem colSize={2} content>
                        {item.type !== 'folder' && (
                          <Input
                            className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.trackNumber}
                            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                              handleEdit(item.fileId, 'trackNumber', e.target.value || '')
                            }
                          />
                        )}
                      </ContentListItem>
                    ) : (
                      <ContentListItem colSize={3} style={{ paddingLeft: '8px' }} content>
                        {item.type !== 'folder' && (
                          <Typography.Text>{formatFileSize(item.size)}</Typography.Text>
                        )}
                      </ContentListItem>
                    )
                  }
                  {
                    !listCondition.tagRange || listCondition.tagRange === TagRange.BASIC ? (
                      <ContentListItem colSize={3} content>
                        {item.type !== 'folder' && (
                          <Input
                            className={`${contentStyle['list-default-content-input']}`} size={'mini'} defaultValue={item.year}
                            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
                              handleEdit(item.fileId, 'year', e.target.value || '')
                            }
                          />
                        )}
                      </ContentListItem>
                    ) : (
                      <ContentListItem colSize={2} style={{ paddingLeft: '8px' }} content>
                        {item.type !== 'folder' && (
                          <Typography.Text>{item.format}</Typography.Text>
                        )}
                      </ContentListItem>
                    )
                  }
                  {
                    !listCondition.tagRange || listCondition.tagRange !== TagRange.BASIC && (
                      <ContentListItem colSize={2} style={{ paddingLeft: '8px' }} content>
                        {item.type !== 'folder' && (
                          <Typography.Text>{item.bitrate}</Typography.Text>
                        )}
                      </ContentListItem>
                    )
                  }
                  <ContentListItem colSize={1} style={{ paddingLeft: '3px' }} content>
                    {item.newItem ? (
                      <Button
                        icon={<IconMinusCircle />}
                        type={'text'}
                        size={'mini'}
                        style={{ color: 'rgb(var(--danger-7))' }}
                        onClick={() => onRemoveLocal(item.fileId)}
                      />
                    ) : item.type === 'folder' ? (
                      <Button
                        icon={<IconRight />}
                        type={'text'}
                        size={'mini'}
                        onClick={() => folderLoad(item.fileId, item.fileName)}
                      />
                    ) : (
                      <Button
                        className={contentStyle['list-default-content-col-btn']}
                        icon={<IconSearch />}
                        type={'text'}
                        size={'mini'}
                      />
                    )}
                  </ContentListItem>
                </Grid.Row>
              ))}
            </Checkbox.Group>
          )}
        </div>
      </div>
      <ContentLyricEdit
        musicInfo={lyricsEditInfo.musicInfo}
        visible={lyricsEditVisible}
        onClose={() => setLyricsEditVisible(false)}
        content={lyricsEditInfo.lyrics}
        onConfirm={async (fileId, lyrics) => {
          await updMusicLyrics(fileId, lyrics);
          await onListItemUpdate(fileId, ['lyrics', 'lyricsLength']);
        }}
      />
    </Card>
  );
};
export default Content;
