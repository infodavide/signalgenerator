import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subscription, catchError, first, last, of } from 'rxjs';
import { MainState, propertiesSelector, settingsSelector, userSelector } from './main.store';
import { APPLY_SIGNAL_SETTINGS_ACTION, RESET_ACTION } from './main.actions';
import { SignalService } from './service/signal.service';

@Component({
  selector: 'app-properties',
  templateUrl: './properties.component.html',
  styleUrls: ['./properties.component.scss']
})
export class PropertiesComponent {
  private _subscriptions: Subscription[] = [];
  form: FormGroup;

  constructor(private _router: Router, private _store: Store<{ main: MainState }>, private _service: SignalService) {    
    this.form = new FormGroup({});
  }

  ngOnInit(): void {
    this._subscriptions.push(this._store.select(propertiesSelector).subscribe(d => {
      //TODO
    }));
    this._subscriptions.push(this.form.valueChanges.subscribe(values => console.log(values)));    
  }

  ngOnDestroy(): void {
    this._subscriptions.forEach(s => s.unsubscribe());
  }

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  previous(): void {
    this.validate();
    this._router.navigate(['/settings']);
  }

  preview() {
    //TODO
  }

  submit(): void {/*
    if (this.validate()) {
      this._service.post(
        this._store.select(userSelector),
        this._store.select(settingsSelector),
        this._store.select(propertiesSelector)
      ).pipe(
        catchError(error => {
          let message: string;
  
          if (error.error instanceof ErrorEvent) {
            message = `Error: ${error.error.message}`;
          } else {
            message = `Unknown error`;
          }
  
          //TODO
          return of();
        }),
        last(() => {
          this._router.navigate(['/home']);

          return true;
        })
      );
    }*/
  }

  private validate(): boolean {
    if (this.form.valid) {
      let values = new Map<string, object>();
      //TODO
      this._store.dispatch(APPLY_SIGNAL_SETTINGS_ACTION({ 
        properties: values
      }));
      return true;
    }

    return false;
  }
}
