import { createSelector } from "@ngrx/store";
import { InputBase } from './dynamic-form';

export interface UserData {
  username: string | null,
  password: string | null, // Not used in this version
  email: string | null
};
export interface SettingsData {
  start: Date | null,
  end: Date | null,
  frequency: number | null,
  step: number | null,
  signal: string | null,
  linePattern: string | null
}
export interface StatusData {
  message: string | null
}
export interface MainState {
  user: UserData | null,
  settings: SettingsData | null,
  signalSettings: InputBase[] | null,
  status: StatusData | null
};

export interface SignalSettings {
  name?: string;
  inputs: InputBase[] | null;
}

export const selectUser = (state: { main: MainState }) => state.main.user;
export const userSelector = createSelector(
  selectUser, (d: UserData| null) => d
);
export const selectSettings = (state: { main: MainState }) => state.main.settings;
export const settingsSelector = createSelector(
  selectSettings,
  (d: SettingsData| null) => d
);
export const selectSignalSettings = (state: { main: MainState }) => state.main.signalSettings;
export const signalSettingsSelector = createSelector(
  selectSignalSettings,
  (d: InputBase[] | null) => d
);
export const selectStatus = (state: { main: MainState }) => state.main.status;
export const statusSelector = createSelector(
  selectStatus,
  (d: StatusData| null) => d
);
