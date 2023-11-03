import { createSelector } from "@ngrx/store";

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
  user: UserData,
  settings: SettingsData,
  properties: Map<string, object>,
  status: StatusData
};

export interface SignalSettings {
  name?: string;
  settings?: Map<string, object>;
}

export const selectUser = (state: { main: MainState }) => state.main.user;
export const userSelector = createSelector(
  selectUser, (d: UserData) => d
);
export const selectSettings = (state: { main: MainState }) => state.main.settings;
export const settingsSelector = createSelector(
  selectSettings,
  (d: SettingsData) => d
);
export const selectProperties = (state: { main: MainState }) => state.main.properties;
export const propertiesSelector = createSelector(
  selectProperties,
  (d: Map<string, object>) => d
);
export const selectStatus = (state: { main: MainState }) => state.main.status;
export const statusSelector = createSelector(
  selectStatus,
  (d: StatusData) => d
);
