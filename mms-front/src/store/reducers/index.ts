import { combineReducers } from 'redux';
import settings from '@/store/reducers/settings';
import userInfo from '@/store/reducers/userInfo';
import currentFolder from '@/store/reducers/currentFolder';
import aggregateCondition from "@/store/reducers/aggregateCondition";

export default combineReducers({
  settings,
  userInfo,
  currentFolder,
  aggregateCondition,
});
