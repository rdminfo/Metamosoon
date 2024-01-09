import React from 'react';
import aggregateStyles from '../style/aggregate.module.less';
import {Badge, Radio, Space, Typography} from "@arco-design/web-react";
import Button from "@arco-design/web-react/lib/Button";
import {AggregateConditionList} from "@/pages/music/types/AggregateConditionList";
import {useSelector} from "react-redux";
import {RootState} from "@/store/types/RootState";
import store from "@/store";
import {UPDATE_AGGREGATE_CONDITION} from "@/store/constants";

function AggregateCondition(props: { data: AggregateConditionList }) {
  const {data} = props;
  const { radioValue, badgeCount } = useSelector((state: RootState) => ({
    radioValue: state.aggregateCondition[data.name].value,
    badgeCount: state.aggregateCondition[data.name].badgeCount
  }));

  function conditionChoose(value: string) {
    store.dispatch({
      type: UPDATE_AGGREGATE_CONDITION,
      payload: { attr: data.name, value },
    });
  }

  return (
    <Space>
      <Typography.Text type='secondary' style={{ fontSize: '13px' }}>
        {data.title}:
      </Typography.Text>
      <Radio.Group name='button-radio-group' value={radioValue}>
        {data.data.map((item, index) => {
          const badgeValue = badgeCount[index] !== undefined ? badgeCount[index] : 0;
          return (
            <Radio key={item.value} value={item.value} className={aggregateStyles['grid-radio']}>
              {({ checked }) => {
                return (
                  <Badge count={badgeValue} dot={true}>
                    <Button tabIndex={-1} key={item.value} shape='round' type={checked ? 'primary' : 'default'} size={"mini"}
                            onClick={() => { conditionChoose(item.value) }}>
                      {item.name}
                    </Button>
                  </Badge>
                );
              }}
            </Radio>
          );
        })}
      </Radio.Group>
    </Space>
  );
}

export default AggregateCondition;
