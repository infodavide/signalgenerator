import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { MainState, SignalSettings, settingsSelector } from './main.store';
import { SignalService } from './service/signal.service';
import { APPLY_GENERIC_SETTINGS_ACTION, APPLY_USER_DATA_ACTION, RESET_ACTION } from './main.actions';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {
  private _subscriptions: Subscription[] = [];
  form: FormGroup;
  range: FormGroup;
  formValid: boolean = false;
  availableSignals$!: Observable<SignalSettings[]>;

  constructor(private _router: Router, private _store: Store<{ main: MainState }>, private _service: SignalService) {
    let startControl: FormControl = new FormControl<Date | null>(null, [Validators.required]);
    let endControl: FormControl = new FormControl<Date | null>(null, [Validators.required]);
    this.form = new FormGroup({
      start: startControl,
      end: endControl,
      frequency: new FormControl(null, [Validators.required]),
      step: new FormControl(null, [Validators.required]),
      signal: new FormControl(null, [Validators.required]),
      linePattern: new FormControl(null, [Validators.required])
    });
    this.range = new FormGroup({
      start: startControl,
      end: endControl,
    });
  }

  ngOnInit(): void {
    this._subscriptions.push(this._store.select(settingsSelector).subscribe(d => {
      if (d) {
        this.form.patchValue({
          start: d.start, 
          end: d.end,
          frequency: d.frequency,
          step: d.step,
          signal: d.signal,
          linePattern: d.linePattern
        });
        this.formValid = this. form.valid;
      }
    }));
    this.availableSignals$ = this._service.fetchAvailableSignals();
    this._subscriptions.push(this. form.valueChanges .subscribe((changed: any) => {
      this.formValid = this. form.valid;
    }));
  }

  ngOnDestroy(): void {
    this._subscriptions.forEach(s => s.unsubscribe());
  }

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  back(): void {
    this.validate();
    this._router.navigate(['/user']);
  }

  next(): void {
    if (this.validate()) {
      this._router.navigate(['/properties']);
    }
  }

  private validate(): boolean {
    if (this.form.valid) {
      this._store.dispatch(APPLY_GENERIC_SETTINGS_ACTION({ 
        start: this.form.controls['start'].value,
        end: this.form.controls['end'].value,
        frequency: this.form.controls['frequency'].value,
        step: this.form.controls['step'].value,
        signal: this.form.controls['signal'].value,
        linePattern: this.form.controls['linePattern'].value,
      }));
      return true;
    }

    return false;
  }
}
