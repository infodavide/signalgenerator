import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { EMPTY, Observable, of } from 'rxjs';
import { map, exhaustMap, catchError, take } from 'rxjs/operators';
import {
  APPLY_USER_DATA_ACTION,
  APPLY_GENERIC_SETTINGS_ACTION,
  APPLY_SIGNAL_SETTINGS_ACTION,
  RESET_ACTION,
  GENERATION_REQUEST_ACTION,
  GENERATION_REQUEST_SUCCESS_ACTION,
  GENERATION_REQUEST_FAILURE_ACTION
} from './main.actions';
import { MainState, propertiesSelector, settingsSelector, userSelector } from './main.store';
import { SignalService } from './service/signal.service';
import { State, Store, props } from '@ngrx/store';

const LOCAL_STORAGE_KEY = "mainState";

@Injectable()
export class MainEffects {

  constructor(
    private _store: Store<MainState>,
    private _actions$: Actions,
    private _service: SignalService
  ) { }
}