import React, {ReactNode, useState} from "react";
import {
  Button,
  Image,
  Popover,
  PopoverProps,
  Upload
} from "@arco-design/web-react";
import uploadStyles from './style/upload.module.less';
import styles from './style/image.upload.module.less';
import {
  IconDelete,
  IconEdit,
   IconUpload
} from "@arco-design/web-react/icon";
import {UploadItem} from "@arco-design/web-react/lib/Upload";
import cs from "classnames";

interface ImageUploadPopoverProps extends PopoverProps{
  uploadUrl: string
  imgUrl?: string;
  onImgChange?: () => void;
  onImgRemove?: () => void;
  children?: ReactNode;
}
const ImageUploadPopover = (props: ImageUploadPopoverProps) => {
  const { uploadUrl, imgUrl, onImgChange, onImgRemove, children, ...otherProps} = props;
  const defaultImgFile = { uid: '999', url: '/' };

  const [popVisible, setPopVisible] = useState<boolean>(false);
  const [imgKey, setImgKey] = useState<number>(0);

  async function onUploadChange(fileList: UploadItem[]) {
    if (!fileList || fileList.length === 0) {
      return;
    }
    for (const file of fileList) {
      if (file.status === 'done' && file.uid !== defaultImgFile.uid) {
        await onImgChange();
        setImgKey(imgKey + 1);
        return;
      }
    }
  }

  return (
    <Popover {...otherProps} className={`${uploadStyles['popover-outer']}`} popupVisible={popVisible} onVisibleChange={visible => {
      setPopVisible(visible);
    }} content={
      <div className={`${styles['image-upload-popover']}`}>
        <Upload
          className={cs(uploadStyles['upload-outer'], {[uploadStyles['hidden']]: imgUrl})}
          action={uploadUrl} accept="image/jpeg, image/jpg, image/png, image/gif"
          multiple={false}
          onChange={onUploadChange}
          renderUploadList={() => (
            <div className={cs({[uploadStyles['hidden']]: !imgUrl})}>
              <Image key={imgKey} className={styles['upload-image']} width={240} src={imgUrl} actions={[
                // <Button key={1} type={"text"} size={"mini"} className={styles['upload-image-button']}
                //   onClick={() => {
                //     console.log('asdasd');
                //   }}
                // >
                //   <IconDownload />
                // </Button>,
                <Button key={2} type={"text"} size={"mini"} className={styles['upload-image-button']}
                  onClick={() => {
                    const fileInputs = document.getElementsByTagName('input');
                    for (let i = 0; i < fileInputs.length; i++) {
                      if (fileInputs[i].type === 'file') {
                        fileInputs[i].click(); break;
                      }
                    }
                  }}
                >
                  <IconEdit />
                </Button>,
                <Button key={3} type={"text"} size={"mini"} className={styles['upload-image-button']}
                  onClick={() => {
                    setPopVisible(false);
                    onImgRemove();
                  }}
                >
                  <IconDelete />
                </Button>,
              ]}/>
            </div>
          )}
        >
          <div className={`${uploadStyles['upload-block-outer']} ${styles['upload-image-block-outer']}`}>
            <div className={`${uploadStyles['upload-block']}`}>
              <IconUpload />
            </div>
          </div>
        </Upload>
      </div>
    }>
      {children}
    </Popover>
  );
}

export default ImageUploadPopover;