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
  properties: new Map<string, object>,
  status: {
    message: null
  }
};
const DEBUG_REDUCER_ENABLED: boolean = true;
const HYDRATATION_REDUCER_ENABLED: boolean = true;
const HYDRATATION_STORAGE_KEY: string = 'mainState';

export const MAIN_REDUCER = createReducer(
  initialState,
  on(APPLY_USER_DATA_ACTION, (state, { username, password, email }) => {
    return {
      ...state,
      user: {
        username: username,
        password: password,
        email: email
      }
    };
  }),
  on(APPLY_GENERIC_SETTINGS_ACTION, (state, { start, end, frequency, step, signal, linePattern }) => {
    return {
      ...state,
      settings: {
        start: start,
        end: end,
        frequency: frequency,
        step: step,
        signal: signal,
        linePattern: linePattern
      }
    };
  }),
  on(APPLY_SIGNAL_SETTINGS_ACTION, (state, { properties }) => {
    return {
      ...state,
      properties: properties
    };
  }),
  on(RESET_ACTION, (state) => {
    return {
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
      properties: new Map<string, object>,
      status: {
        message: null
      }
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
        const value = localStorage.getItem(HYDRATATION_STORAGE_KEY);

        if (value) {
          try {
            return JSON.parse(value);
          } catch {
            localStorage.removeItem(HYDRATATION_STORAGE_KEY);
          }
        }
      }
      
      const nextState = reducer(state, action);
      localStorage.setItem(HYDRATATION_STORAGE_KEY, JSON.stringify(nextState));
      console.log('[HydratationMetaReducer]  Saving state: ', nextState, ' to local storage with key:', HYDRATATION_STORAGE_KEY);

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