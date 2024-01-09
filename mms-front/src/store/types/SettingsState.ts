import defaultSettings from '@/settings.json';

export default interface SettingsState {
  data?: typeof defaultSettings;
}
