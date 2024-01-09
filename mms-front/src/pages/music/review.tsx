import React, {useRef} from "react";
import {
  AutoComplete,
  Button,
  Card, Cascader,
  DatePicker,
  Form,
  Input,
  InputNumber, Message, Select,
  Space,
  TreeSelect,
  Typography, Upload,
  Image
} from "@arco-design/web-react";
import {IconArrowLeft, IconDelete, IconDownload, IconEye, IconRefresh, IconRight} from "@arco-design/web-react/icon";
import cardStyles from './style/card.module.less';
import styles from "@/pages/music/style/review.module.less";
import useLocale from "@/utils/useLocale";
import locale from "@/pages/music/locale";
import localeGlobal from "@/locale";
import {getReviewItemsConfig, getReviewLayoutConfig} from "@/pages/music/config/review";
import ResizableCard from "@/components/Common/ResizableCard";
import {CARD_PADDING} from "@/config/dom";
import navigationStyles from "@/pages/music/style/navigation.module.less";

const Review = (props) => {
  const {height} = props
  const t = useLocale(locale);
  const tg = useLocale(localeGlobal);
  const formRef = useRef();
  const formItemLayout = getReviewLayoutConfig();
  const formItems = getReviewItemsConfig(t, tg);
  const [coverPreviewVisible, setCoverPreviewVisible] = React.useState(false);
  const [coverPreviewWidth, setCoverPreviewWidth] = React.useState(0);
  function handleWidthChange (width) {
    setCoverPreviewWidth(width - (CARD_PADDING * 2) - 54)
  }

  return (
    <ResizableCard className={styles['review']} style={{height: height}} onWidthChange={handleWidthChange}>
      <Space className={`${cardStyles['nav-bar']} ${styles['review-nav']}`} size={"medium"}>
        <div></div>
        <Button.Group>
          <Button className={`${cardStyles['nav-bar-button']} ${styles['review-nav-btn']}`} type='text' size={"mini"} onClick={() => console.log('')} />
          <Button className={`${cardStyles['nav-bar-button']} ${styles['review-nav-btn']}`} type='text' size={"mini"} onClick={() => console.log('')}>
            {tg['ope.clear']}
          </Button>
          <Button className={`${cardStyles['nav-bar-button']} ${styles['review-nav-btn']}`} type='primary' size={"mini"} onClick={() => console.log('')}>
            {tg['ope.apply']}
          </Button>
        </Button.Group>
      </Space>
      <div className={styles['form-outer']} style={{height: height - 40 - 24}}>
        <Form
          ref={formRef}
          className={styles['form']}
          {...formItemLayout}
          autoComplete='off'
          scrollToFirstError
          size={"mini"}
        >
          {
            formItems.map((item, index) => {
              return (
                <Form.Item className={styles['form-item']} label={item.name} key={index}>
                  {item.type === 'input' ? <Input placeholder={item.name} /> :
                    item.type === 'data' ? <Input placeholder={item.name} /> :
                    item.type === 'textArea' ? <Input.TextArea style={{ minHeight: '300px' }} /> :
                    item.type === 'image' ?
                      <Image src={'//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/a8c8cdb109cb051163646151a4a5083b.png~tplv-uwbnlip3yd-webp.webp'}
                             className={styles['form-item-image']} width={coverPreviewWidth}
                             onClick={() => {setCoverPreviewVisible(true);}}
                             actions={[
                               <button key='1' className={styles['form-item-image-btn']} onClick={() => { console.log('download'); }}><IconDownload /></button>,
                               <button key='2' className={styles['form-item-image-btn']} onClick={() => { console.log('download'); }}><IconDelete /></button>,
                             ]}
                             previewProps={{
                               visible: coverPreviewVisible,
                               onVisibleChange: (e) => { setCoverPreviewVisible(false); },
                             }}
                    /> : ''}
                </Form.Item>
              )
            })
          }
        </Form>
      </div>
    </ResizableCard>
  );
};

export default Review;
