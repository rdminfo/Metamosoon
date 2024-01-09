import React, { ReactNode } from 'react';
import styles from "./style/file.select.module.less";
import {Button, Divider, Popover, PopoverProps, Space, Typography} from "@arco-design/web-react";
import IconFolderCustom from "@/pages/music/assets/folder.svg";
import IconMusicCustom from "@/pages/music/assets/music.svg";
import {IconDelete} from "@arco-design/web-react/icon";
import {FileSelectList} from "@/pages/music/types/FileSelectList";
import {FolderInfo} from "@/pages/music/types/FolderInfo";

interface FileSelectPopoverProps extends PopoverProps{
  selectFileList: FileSelectList;
  onToFolderClick?(folderInfo: FolderInfo) : void;
  onFileRemove?(fileIds: string[]): void
  children?: ReactNode;
}
const FileSelectPopover = (pops: FileSelectPopoverProps) => {
  const { selectFileList, onToFolderClick, onFileRemove, children, ...otherProps } = pops;

  return (
    <Popover
      trigger={"click"} position={"bottom"}
      className={`${styles['popover-outer']}`}
      { ...otherProps }
      content={
        <div>
          <div className={`${styles['content-nav-select-popover-top']}`}>
            <Typography.Text style={{ fontSize: '12px', cursor: "pointer" }} underline
                             onClick={() => onToFolderClick?.({ id: selectFileList.folderId, name: selectFileList.folderName })}>
              {selectFileList.folderName}
            </Typography.Text>
          </div>
          <Divider type="horizontal" style={{ margin: '4px 0' }} />
          {
            selectFileList.items.map((item, index) => (
              <div key={index} className={`${styles['select-list-item-outer']}`}>
                <Space className={`${styles['select-list-item']}`} size={"mini"}>
                  {item.type === 'folder' ? (
                    <IconFolderCustom />
                  ) : (
                    <IconMusicCustom />
                  )}
                  <Typography.Text style={{ fontSize: '12px' }}>{item.name}</Typography.Text>
                </Space>
                <Button type={"text"} size={"small"} icon={<IconDelete />} onClick={() => onFileRemove?.([item.fileId])} />
              </div>
            ))
          }
        </div>
      }>
      {children}
    </Popover>
  )
}

export default FileSelectPopover;
