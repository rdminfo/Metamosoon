import {RESET_AGGREGATE_CONDITION, UPDATE_AGGREGATE_CONDITION} from "@/store/constants";
import AggregateConditionState from "@/store/types/AggregateConditionState";

/**
 * 统计筛选条件的值，其中value值见aggregate.condition.tsx的data.value的集值
 * badgeCount是控制统计筛选项显示角标的值，是个数组，数组的大小应该和aggregate.condition.tsx的data数组大小一致
 */
const initialState: AggregateConditionState = {
  organized: { value: '', badgeCount: [0, 0] },
  album: { value: '',  badgeCount: [0, 0] },
  cover: { value: '', badgeCount: [0, 0] },
  lyric: { value: '', badgeCount: [0, 0] },
  year: { value: '', badgeCount: [0, 1] },
};

export default function aggregateCondition(state = initialState, action) {
  switch (action.type) {
    case UPDATE_AGGREGATE_CONDITION: {
      const { attr, value } = action.payload;
      if (state.hasOwnProperty(attr)) {
        return {
          ...state,
          [attr]: {
            ...state[attr],
            value: value,
          },
        };
      } else {
        return state;
      }
    }
    case RESET_AGGREGATE_CONDITION: {
      return initialState
    }
    default:
      return state;
  }
}
