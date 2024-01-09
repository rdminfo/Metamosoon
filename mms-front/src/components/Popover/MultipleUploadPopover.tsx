import React, {ReactNode, useState} from 'react';
import {Button, Divider, Popover, PopoverProps, Space, Typography, Upload} from "@arco-design/web-react";
import uploadStyles from './style/upload.module.less';
import styles from './style/multiple.upload.module.less';
import {UploadItem} from "@arco-design/web-react/lib/Upload";
import cs from 'classnames';
import IconMusicCustom from "@/pages/music/assets/music.svg";
import {IconCheck, IconDelete, IconExclamationCircleFill, IconSync} from "@arco-design/web-react/icon";
import useLocale from "@/utils/useLocale";
import globalLocale from "@/locale";
import locale from "./local";
import {UploadProps} from "@arco-design/web-react/es/Upload/interface";
import {FolderInfo} from "@/pages/music/types/FolderInfo";

interface UploadPopoverProps extends PopoverProps, UploadProps{
  folderInfo? : FolderInfo;
  folderClick(folderInfo: FolderInfo) : void;
  children?: ReactNode;
}
const MultipleUploadPopover = (props: UploadPopoverProps) => {
  const t = useLocale(locale)
  const tg = useLocale(globalLocale);
  const {folderInfo, folderClick, action, children, ...otherProps} = props;

  const [popVisible, setPopVisible] = useState<boolean>(false);
  const [uploadFolder, setUploadFolder] = useState<FolderInfo>(folderInfo);
  const [fileList, setFileList] = useState<UploadItem[]>([]);
  const [allDone, setAllDone] = useState<boolean>(false);
  const [allClear, setAllClear] = useState<boolean>(false);

  function onUploadChange (fileList) {
    let doneCount = 0;
    fileList.forEach(file => {
      if (file.status === 'done') {
        doneCount ++;
      }
    })
    if (doneCount === fileList.length && doneCount != 0) {
      setAllDone(true);
    } else {
      setAllDone(false);
    }
    setFileList(fileList);
  }

  return (
    <Popover {...otherProps} className={`${uploadStyles['popover-outer']}`} popupVisible={popVisible} onVisibleChange={visible => {
      if (allDone && allClear) {
        setFileList([]); setAllClear(false);
      }
      setPopVisible(visible);
    }} content={
      <div className={`${styles['multiple-upload-popover']}`}>
        <Upload className={cs(uploadStyles['upload-outer'], {[uploadStyles['hidden']]: fileList && fileList.length > 0})}
          fileList={fileList} {...otherProps}
          action={`${action}/${uploadFolder.id}`}
          onChange={onUploadChange}
          multiple
          renderUploadList={(fileList, props) => (
            <div className={`${styles['upload-list-outer']}`}>
              <div className={cs({[uploadStyles['hidden']]: !fileList || fileList.length === 0})}>
                <div className={`${styles['upload-list-top']}`}>
                  <div className={`${styles['upload-list-top-left']}`}>
                    <Typography.Text className={`${styles['upload-list-top-left-text']}`} onClick={() => folderClick(uploadFolder)} underline>{uploadFolder.name}</Typography.Text>
                  </div>
                  <div className={`${styles['upload-list-top-right']}`}>
                    {allDone ? <>
                        <Button type={"text"} size={"mini"} onClick={() => {
                          setFileList([]);
                        }}>
                          {tg['ope.clear']}
                        </Button>
                        <Button type={"text"} size={"mini"} onClick={() => {
                          setPopVisible(false); setAllClear(true);
                        }}>
                          {tg['ope.done']}
                        </Button> </>
                      : undefined }
                  </div>
                </div>
                <Divider type="horizontal" style={{ margin: '6px 0' }} />
              </div>
              <div className={`${styles['upload-list-item-outer']} ${cs({[uploadStyles['hidden']]: !fileList || fileList.length === 0})}`}>
                {fileList.map((file, index) => {
                  return (
                    <div key={index} className={`${styles['upload-list-item']}`}>
                      <Space className={styles['upload-list-item-left']} size={"mini"}>
                        <IconMusicCustom />
                        <Typography.Text className={styles['upload-list-item-left-text']}>{file.name}</Typography.Text>
                        {
                          file.status === 'uploading' ? <IconSync spin /> :
                            file.status === 'done' ? <IconCheck style={{ color: 'rgb(var(--primary-5))' }} /> :
                              file.status === 'error' ? <IconExclamationCircleFill style={{ color: 'rgb(var(--danger-4))', fontSize: '16px' }} /> : ''
                        }
                      </Space>
                      <Space className={styles['upload-list-item-right']} size={"mini"}>
                        {
                          file.status === 'error' ? <Button type={"text"} size={"mini"} onClick={() => props.onReupload(file)}>{tg['ope.retry']}</Button> : ''
                        }
                        <Button type={"text"} size={"mini"} icon={<IconDelete />} onClick={() => props.onRemove(file)} />
                      </Space>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
          beforeUpload={() => {
            setUploadFolder(folderInfo);
            return true;
          }}
        >
          <div className={`${uploadStyles['upload-block-outer']}`}>
            <div className={`${uploadStyles['upload-block']}`}>
              {t['upload.block.one']}<span>{t['upload.block.two']}</span>
            </div>
          </div>
        </Upload>
      </div>
    }>
      {children}
    </Popover>
  );
}

export default MultipleUploadPopover;
