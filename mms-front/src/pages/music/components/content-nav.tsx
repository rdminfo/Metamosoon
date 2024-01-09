import React, {useEffect, useState} from "react";
import {Breadcrumb, Button, Divider, Input, Popover, Space} from "@arco-design/web-react";
import {IconArrowLeft, IconHome, IconRefresh, IconRight} from "@arco-design/web-react/icon";
import cardStyles from "@/pages/music/style/card.module.less";
import useLocale from "@/utils/useLocale";
import locale from "@/pages/music/locale";
import ResizableCard from "@/components/Common/ResizableCard";
import {useSelector} from "react-redux";
import {RootState} from "@/store/types/RootState";
import {FolderListItem} from "@/pages/music/types/FolderListItem";
import store from "@/store";
import {UPDATE_CURRENT_FOLDER} from "@/store/constants";
import {fetchFolderParentList } from "@/service/music";
import useSWR from "swr";

const ContentNav = (props) => {
  const {onHeightChange} = props
  const t = useLocale(locale);

  const { currenFolder } = useSelector((state: RootState) => ({
    currenFolder: state.currentFolder.data,
  }));

  const { data } = useSWR(
    ['fetchFolderParentList', currenFolder.id],
    ([ban, folderId]) => fetchFolderParentList(ban, folderId)
  );

  const [breadcrumbData, setBreadcrumbData] = useState<FolderListItem[]>([])
  const [folderHistory, setFolderHistory] = useState<FolderListItem[]>([])
  const [folderLoadByBack, setFolderLoadByBack] = useState<boolean>(false)

  useEffect(() => {
    setBreadcrumbData(data?.data)
  }, [data]);

  useEffect(() => {
    if (currenFolder && !folderLoadByBack) {
      folderHistoryAdd(currenFolder.id, currenFolder.name)
    }
    setFolderLoadByBack(false);
  }, [currenFolder, folderLoadByBack]);

  const handleHeightChange = (height) => {
    onHeightChange(height);
  };

  function folderHistoryAdd(folderId: string, folderName: string) {
    const existsInHistory = folderHistory.length > 0 && folderHistory[folderHistory.length - 1].folderId === folderId;
    if (!existsInHistory) {
      setFolderHistory(prevHistory => [
        ...prevHistory, { folderId, folderName }
      ]);
    }
  }

  function folderBack() {
    setFolderLoadByBack(true);
    if (folderHistory.length <= 1) { return; }
    const lastLoadFolder = folderHistory[folderHistory.length - 2];
    folderLoad(lastLoadFolder);
    const newFolderHistory = folderHistory.slice(0, folderHistory.length - 1);
    setFolderHistory(newFolderHistory);
  }

  function folderReload() {
    if (folderHistory.length == 0) { return; }
    const lastLoadFolder = folderHistory[folderHistory.length - 1];
    folderLoad(lastLoadFolder);
  }

  function folderLoad(folderData: FolderListItem) {
    store.dispatch({
      type: UPDATE_CURRENT_FOLDER,
      payload: { data: { id: folderData.folderId, name: folderData.folderName } },
    });
  }

  return (
    <ResizableCard onHeightChange={handleHeightChange}>
      <Space className={cardStyles['nav-bar']}>
        <Space size={"mini"}>
          <Popover content={<div>{t['music.content.nav.popover.back']}</div>}>
            <Button type='text' icon={<IconArrowLeft />} size={"small"} onClick={folderBack} />
          </Popover>
          <Popover content={
            <div>{t['music.content.nav.popover.refresh']}</div>}>
            <Button type='text' icon={<IconRefresh />} size={"small"} onClick={folderReload} />
          </Popover>
          <Divider type='vertical' style={{ margin: '0 8px' }} />
          <Breadcrumb maxCount={4} style={{ fontSize: 14 }} separator={<IconRight />}>
            <Breadcrumb.Item href='#' onClick={() => {
              const homeBreadcrumbData = breadcrumbData && breadcrumbData.length > 0 ? breadcrumbData[0] : { folderId: '0', folderName: '/' } ;
              folderLoad({ folderId: homeBreadcrumbData.folderId, folderName: homeBreadcrumbData.folderName })
            }}>
              <IconHome />
            </Breadcrumb.Item>
            {
              !breadcrumbData || breadcrumbData.length === 0 ? ('') : (
                breadcrumbData.map((item, index) => {
                  if (item.level > 0) {
                    return (
                      <Breadcrumb.Item href='#' key={index} onClick={() => folderLoad(item)}>
                        { item.folderName }
                      </Breadcrumb.Item>
                    )
                  } else {
                    return undefined;
                  }
                })
              )
            }
          </Breadcrumb>
        </Space>
        <Input.Search allowClear placeholder={t['music.content.nav.search.placeholder']} style={{ minWidth: 230 }} />
      </Space>
    </ResizableCard>
  );
};

export default ContentNav;
