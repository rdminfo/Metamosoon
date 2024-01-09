import React from 'react';
import {Modal} from "@arco-design/web-react";
import {ModalProps} from "@arco-design/web-react/es/Modal/interface";

export interface InnerModelProps extends ModalProps{
  tg?: unknown
}
export function deleteConfirm(config: InnerModelProps) {
  const { onOk, tg } = config
  Modal.confirm({
    simple: true,
    title: tg['notification.warn'],
    okText: tg['ope.confirm'],
    cancelText: tg['ope.cancel'],
    content: tg['modal.text.delete'],
    style: { padding: '18px', maxWidth: '360px' },
    onOk
  });
}
