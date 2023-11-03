import { createAction, props } from '@ngrx/store';
import { SettingsData, UserData } from './main.store';

export const APPLY_USER_DATA_ACTION = createAction('Apply user settings',
    props<{ username: string; password: string, email: string }>()
);
export const APPLY_GENERIC_SETTINGS_ACTION = createAction('Apply generic settings',
    props<{ start: Date, end: Date, frequency: number, step: number, signal: string, linePattern: string }>());
export const APPLY_SIGNAL_SETTINGS_ACTION = createAction('Apply signal settings',
    props<{ properties: Map<string, Object> }>());
export const RESET_ACTION = createAction('Reset settings');
export const GENERATION_REQUEST_SUCCESS_ACTION = createAction('Generation request success');
export const GENERATION_REQUEST_FAILURE_ACTION = createAction('Generation request failure',
    props<{ message: string }>()
);
export const GENERATION_REQUEST_ACTION = createAction('Post generation request',
    props<{ user: UserData, settings: SettingsData, properties: Map<string, object> }>()
);