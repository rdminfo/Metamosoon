import React, {ReactNode} from 'react';
import contentStyle from "@/pages/music/style/content.module.less";
import { Grid } from '@arco-design/web-react';
import {ColProps} from "@arco-design/web-react/es/Grid/interface";
import cs from 'classnames'
interface ContentListItem extends ColProps{
  colSize: number;
  children: ReactNode;
  head?: boolean;
  content?: boolean;
}
const ContentListItem = (props: ContentListItem) => {
  const {colSize, children, head, content, style} = props;
  return (
    <Grid.Col
      className={cs(contentStyle['list-default-col'], {[contentStyle['list-default-head-col']]: head}, {[contentStyle['list-default-content-col']]: content})}
      xs={colSize} sm={colSize} md={colSize} lg={colSize} xl={colSize} xxl={colSize}
      style={style}
    >
      {children}
    </Grid.Col>
  )
}

export default ContentListItem;