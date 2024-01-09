import UserInfoState from '@/store/types/UserInfoState';
import SettingsState from '@/store/types/SettingsState';
import CurrentFolderState from '@/store/types/CurrentFolderState';
import AggregateConditionState from "@/store/types/AggregateConditionState";

export interface RootState {
  userInfo: UserInfoState;
  settings: SettingsState;
  currentFolder: CurrentFolderState;
  aggregateCondition: AggregateConditionState;
}
