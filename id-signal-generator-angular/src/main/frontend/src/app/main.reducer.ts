import { ActionReducer, INIT, MetaReducer, createReducer, on } from '@ngrx/store';
import {
  APPLY_USER_DATA_ACTION,
  APPLY_GENERIC_SETTINGS_ACTION,
  APPLY_SIGNAL_SETTINGS_ACTION,
  RESET_ACTION,
  GENERATION_REQUEST_ACTION,
  GENERATION_REQUEST_SUCCESS_ACTION,
  GENERATION_REQUEST_FAILURE_ACTION
} from './main.actions';
import { MainState } from './main.store';

export const initialState: MainState = {
  user: {
    username: null,
    password: null,
    email: null
  },
  settings: {
    start: new Date(),
    end: new Date(),
    frequency: 1.0,
    step: 500,
    signal: null,
    linePattern: null
  },
  signalSettings: null,
  status: {
    message: null
  }
};
const DEBUG_REDUCER_ENABLED: boolean = true;
const HYDRATATION_REDUCER_ENABLED: boolean = true;
const HYDRATATION_STORAGE_KEY: string = 'mainState';

export const MAIN_REDUCER = createReducer(
  initialState,
  on(APPLY_USER_DATA_ACTION, (state, { data }) => {
    return {
      ...state,
      user: data
    };
  }),
  on(APPLY_GENERIC_SETTINGS_ACTION, (state, { data }) => {
    return {
      ...state,
      settings: data
    };
  }),
  on(APPLY_SIGNAL_SETTINGS_ACTION, (state, { data }) => {
    console.log(data);
    return {
      ...state,
      signalSettings: data
    }
  }),
  on(RESET_ACTION, (state) => {
    return {
      user: null,
      settings:  null,
      signalSettings: null,
      status: null
    };
  }),
  on(GENERATION_REQUEST_ACTION, (state) => {
    return {
      ...state
    };
  }),
  on(GENERATION_REQUEST_SUCCESS_ACTION, (state) => {
    return {
      ...state,
      status: {
        message: ''
      }
    };
  }),
  on(GENERATION_REQUEST_FAILURE_ACTION, (state, { message }) => {
    return {
      ...state,
      status: {
        message: message
      }
    };
  })
);

export function hydratationMetaReducer(reducer: ActionReducer<MainState>): ActionReducer<MainState> {
  return (state, action) => {
    if (HYDRATATION_REDUCER_ENABLED) {
      if (action.type === INIT) {
        console.log('[HydratationMetaReducer] Reloading state from local storage with key:', HYDRATATION_STORAGE_KEY);
        const json = localStorage.getItem(HYDRATATION_STORAGE_KEY);

        if (json) {
          console.log('[HydratationMetaReducer] parsing state: ', json);
          
          try {
            return JSON.parse(json);
          } catch {
            localStorage.removeItem(HYDRATATION_STORAGE_KEY);
          }
        }
      }
      
      const nextState = reducer(state, action);
      let json = JSON.stringify(nextState);
      localStorage.setItem(HYDRATATION_STORAGE_KEY, json);
      console.log('[HydratationMetaReducer] Saving state: ', json, ' to local storage with key:', HYDRATATION_STORAGE_KEY);

      if (action.type === RESET_ACTION.type) {
        localStorage.removeItem(HYDRATATION_STORAGE_KEY);
      }

      return nextState;
    }

    return state;
  };
}
export function debugMetaReducer(reducer: ActionReducer<any>): ActionReducer<any> {
  return function (state, action) {
    if (DEBUG_REDUCER_ENABLED) {
      console.log('[DebugMetaReducer] state:', state);
      console.log('[DebugMetaReducer] action: ', action);
    }

    return reducer(state, action);
  };
}

export const META_REDUCERS: MetaReducer<any>[] = [debugMetaReducer, hydratationMetaReducer];