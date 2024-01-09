import React, {useEffect, useState} from 'react';
import styles from './style/index.module.less';
import {Space} from '@arco-design/web-react';

import Navigation from "@/pages/music/navigation";
import Aggregate from "@/pages/music/aggregate";
import ContentNav from "@/pages/music/components/content-nav";
import Content from "@/pages/music/content";
import Review from "@/pages/music/review";
import {useWindowSize} from '@/utils/useWindowSize'
import {HEADER_HEIGHT, MODULE_PADDING} from "@/config/dom";

function Music() {
  const { width, height } = useWindowSize();
  const [aggregateHeight, setAggregateHeight] = useState(0);
  const [navigationHeight, setNavigationHeight] = useState(0);
  const [contentNavHeight, setContentNavHeight] = useState(0);
  const [contentHeight, setContentHeight] = useState(0);
  const [reviewHeight, setReviewHeight] = useState(0);

  useEffect(() => {
    setNavigationHeight(height - HEADER_HEIGHT - aggregateHeight - (MODULE_PADDING * 3))
    setContentHeight(height - HEADER_HEIGHT - contentNavHeight - (MODULE_PADDING * 3))
    setReviewHeight(height - HEADER_HEIGHT - (MODULE_PADDING * 2))
  }, [width, height, aggregateHeight, contentNavHeight]);

  return (
    <div className={styles.container}>
      <Space size={"medium"} direction="vertical" className={styles.left}>
        <Aggregate onHeightChange={(height) => {setAggregateHeight(height)}} />
        <Navigation height={navigationHeight} />
      </Space>
      <Space size={"medium"} direction="vertical" className={styles.content}>
        <ContentNav onHeightChange={(height) => {setContentNavHeight(height)}} />
        <Content height={contentHeight} />
      </Space>
      <Space size={"medium"} direction="vertical" className={styles.right}>
        <Review height={reviewHeight} />
      </Space>
    </div>
  );
}

export default Music;
