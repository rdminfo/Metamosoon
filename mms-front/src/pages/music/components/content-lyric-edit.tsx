import React, {useEffect, useState} from "react";
import {Button, Input, Modal, Space, Typography} from "@arco-design/web-react";
import useLocale from "@/utils/useLocale";
import globalLocale from '@/locale';
import locale from "@/pages/music/locale";
import contentStyle from "@/pages/music/style/content.module.less";
import {MusicInfo} from "@/pages/music/types/MusicInfo";

interface ContentLyricEditProps{
  musicInfo: MusicInfo;
  visible?: boolean;
  onClose?: () => void;
  onConfirm?: (fileId: string, lyrics: string) => void;
  content?: string;
}
const ContentLyricEdit = (props: ContentLyricEditProps) => {
  const t = useLocale(locale);
  const tg = useLocale(globalLocale);
  const {musicInfo, content = '', visible, onClose, onConfirm} = props;

  const [contentInner, setContentInner] = useState<string>();
  const [updating, setUpdating] = useState<boolean>();

  const handleKeyPress = (event) => {
    if (event.key === 'Escape' || event.keyCode === 27) {
      onClose();
    }
  };

  useEffect(() => {
    if (visible) {
      setContentInner(content);
      document.addEventListener('keydown', handleKeyPress);
      return () => {
        document.removeEventListener('keydown', handleKeyPress);
      };
    }
  }, [visible]);

  return (
    <Modal
      title={t['music.ope.lyric.edit']}
      visible={visible}
      maskClosable={true}
      onCancel={() => onClose()}
      modalRender={() => (
        <div className={`arco-modal react-draggable zoomModal-appear-done zoomModal-enter-done ${contentStyle['content-lyric-edit-modal']}`}>
          <div className={contentStyle['content-lyric-edit-modal-top']}>
            <Typography.Text>{t['music.ope.lyric.edit']}</Typography.Text>
          </div>
          <div className={contentStyle['content-lyric-edit-modal-content']}>
            <Input.TextArea value={contentInner} onChange={(value) => setContentInner(value)} placeholder={'Lyric here'} />
          </div>
          <div className={contentStyle['content-lyric-edit-modal-footer']}>
            <Space>
              <Button size={"small"} color={'p'} onClick={() => {
                document.removeEventListener('keydown', handleKeyPress);
                onClose();
              }}>
                {tg['ope.cancel']}
              </Button>
              <Button size={"small"} type='primary' loading={updating}
                onClick={async () => {
                  setUpdating(true);
                  await onConfirm(musicInfo.fileId, contentInner);
                  setUpdating(false);
                  onClose();
                }}
              >
                {tg['ope.confirm']}
              </Button>
            </Space>
          </div>
        </div>
      )}
    />
  )
}

export default ContentLyricEdit;