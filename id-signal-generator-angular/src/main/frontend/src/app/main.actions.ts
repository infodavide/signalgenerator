import { createAction, props } from '@ngrx/store';
import { SettingsData, UserData } from './main.store';
import { InputBase } from './dynamic-form';

export const APPLY_USER_DATA_ACTION = createAction('Apply user settings',
    props<{ data: UserData }>()
);
export const APPLY_GENERIC_SETTINGS_ACTION = createAction('Apply generic settings',
    props<{ data: SettingsData }>());
export const APPLY_SIGNAL_SETTINGS_ACTION = createAction('Apply signal settings',
    props<{ data: InputBase[] }>());
export const RESET_ACTION = createAction('Reset settings');
export const GENERATION_REQUEST_SUCCESS_ACTION = createAction('Generation request success');
export const GENERATION_REQUEST_FAILURE_ACTION = createAction('Generation request failure',
    props<{ message: string }>()
);
export const GENERATION_REQUEST_ACTION = createAction('Post generation request',
    props<{ user: UserData, settings: SettingsData, signalSettings: InputBase[] }>()
);