import React, {useState} from 'react';
import cardStyles from './style/card.module.less';
import aggregateStyles from './style/aggregate.module.less';
import locale from './locale';
import {Badge, Grid, Popover, Space, Typography} from "@arco-design/web-react";
import useLocale from "@/utils/useLocale";
import Button from "@arco-design/web-react/lib/Button";
import AggregateCondition from "@/pages/music/components/aggregate-condition";
import {IconMinusCircle, IconPlusCircle} from "@arco-design/web-react/icon";
import {getConditionsConfig} from "@/pages/music/config/aggregate.condition";
import ResizableCard from "@/components/Common/ResizableCard";
import store from "@/store";
import {RESET_AGGREGATE_CONDITION} from "@/store/constants";
import {useSelector} from "react-redux";
import {RootState} from "@/store/types/RootState";


const Aggregate = (props) => {
  const {onHeightChange} = props
  const t = useLocale(locale);
  const conditions = getConditionsConfig(t);

  const { moreBadgeCount } = useSelector((state: RootState) => {
    const total = conditions.popover.reduce((accumulator, item) => {
      const countArray = state.aggregateCondition[item.name]?.badgeCount || [];
      const sum = countArray.reduce((total, currentValue) => total + currentValue, 0);
      return accumulator + sum;
    }, 0);

    return { moreBadgeCount: total };
  });

  const [gridCollapsed, setGridCollapsed] = useState<boolean>(true);

  const handleHeightChange = (height) => {
    onHeightChange(height);
  };

  return (
    <ResizableCard onHeightChange={handleHeightChange}>
      <Space className={cardStyles['nav-bar']}>
        <Space>
          <Typography.Title
            className={cardStyles['nav-bar-title']}
            heading={6}
          >
            {t['music.aggregate.nav.title']}
          </Typography.Title>
          <Button className={cardStyles['nav-bar-button']} type='text' size={"mini"}
                  onClick={() => setGridCollapsed(!gridCollapsed)}
                  icon={gridCollapsed ? <IconPlusCircle /> : <IconMinusCircle />}
          />
        </Space>
        <Button.Group>
          <Button className={cardStyles['nav-bar-button']} type='text' size={"mini"}
                  onClick={() => {
                    store.dispatch({type: RESET_AGGREGATE_CONDITION});
                  }}>
            {t['music.aggregate.nav.clear']}
          </Button>
          <Popover
            trigger='click'
            position='right'
            content={
              <Space direction='vertical'>
                {
                  conditions.popover.map((item, index) => (
                    <AggregateCondition data={item} key={index} />
                  ))
                }
              </Space>
            }
          >
            <Badge count={moreBadgeCount} dot={true}>
              <Button className={cardStyles['nav-bar-button']} type='text' size={"mini"}>
                {t['music.aggregate.nav.more']}
              </Button>
            </Badge>
          </Popover>
        </Button.Group>
      </Space>
      <Grid cols={1} className={aggregateStyles['grid']} collapsed={gridCollapsed} collapsedRows={2}>
        {
          conditions.card.map((item, index) => (
            <Grid.GridItem className={aggregateStyles['grid-item']} key={index}>
              <AggregateCondition data={item} />
            </Grid.GridItem>
          ))
        }
      </Grid>
    </ResizableCard>
  );
};

export default Aggregate;
