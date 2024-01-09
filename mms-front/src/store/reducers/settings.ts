import defaultSettings from '@/settings.json';
import { UPDATE_SETTINGS } from '@/store/constants';
import SettingsState from '@/store/types/SettingsState';

const initialState: SettingsState = {
  data: defaultSettings,
};

export default function settings(state = initialState, action) {
  switch (action.type) {
    case UPDATE_SETTINGS: {
      const { data = defaultSettings } = action.payload;
      return {
        ...state,
        data,
      };
    }
    default:
      return state;
  }
}
