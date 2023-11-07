import { Component, HostListener } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subscription, tap } from 'rxjs';
import { MainState, SignalSettings, UserData, settingsSelector, userSelector } from './main.store';
import { SignalService } from './service/signal.service';
import { APPLY_GENERIC_SETTINGS_ACTION, APPLY_SIGNAL_SETTINGS_ACTION, APPLY_USER_DATA_ACTION, RESET_ACTION } from './main.actions';
import { InputBase } from './dynamic-form';
import { AbstractFormComponent } from './abstract-form-component';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent extends AbstractFormComponent {
  maxDate: Date;
  minDate: Date;
  range: FormGroup;
  availableSignals$!: Observable<SignalSettings[]>;
  availableSignals: SignalSettings[] = [];
  user!: string;
  linePatternHelpVisibility: boolean = false;

  constructor(private _router: Router, private _store: Store<{ main: MainState }>, private _service: SignalService) {
    super();
    let now = new Date();
    this.maxDate = new Date(now.getTime() + 8.64e+7);
    this.maxDate.setHours(0, 0, 0, 0);
    this.minDate = new Date(now.getTime() - 8.64e+7);
    this.minDate.setHours(0, 0, 0, 0);
    let startControl: FormControl = new FormControl<Date>(this.minDate, [Validators.required, this.dateValidatorFn]);
    let endControl: FormControl = new FormControl<Date>(this.maxDate, [Validators.required, this.dateValidatorFn]);
    this.form.addControl('start', startControl);
    this.form.addControl('end', endControl);
    this.form.addControl('frequency', new FormControl<number>(1.0, [Validators.required, Validators.min(0.1), Validators.max(10.0)]));
    this.form.addControl('step', new FormControl<number>(500.0, [Validators.required, Validators.min(200), Validators.max(600000)]));
    this.form.addControl('signal', new FormControl<string | null>(null, [Validators.required]));
    this.form.addControl('linePattern', new FormControl<string>('', [Validators.required, Validators.maxLength(1024), this.linePatternValidatorFn()]));
    this.range = new FormGroup({
      start: startControl,
      end: endControl,
    });
  }

  override ngOnInit(): void {
    this._subscriptions.push(this._store.select(settingsSelector).subscribe(d => {
      if (d) {
        this.form.patchValue({
          start: d.start || this.minDate,
          end: d.end || this.maxDate,
          frequency: d.frequency || 1.0,
          step: d.step || 500,
          signal: d.signal,
          linePattern: d.linePattern
        });
        this.formValid = this.form.valid;
      }
    }));
    this.availableSignals$ = this._service.fetchAvailableSignals().pipe(
      tap(d => {
        console.log('Available signals: ', d);
        this.availableSignals = d;
      })
    );
    this._subscriptions.push(this._store.select(userSelector).subscribe((d: UserData | null) => {
      if (d) {
        this.user = d.username + ' (' + d.email + ')';
      } else {
        this.user = '';
      }
    }));
    super.ngOnInit();
  }

  public dateValidatorFn(): ValidatorFn {
    return (control) => {
      if (control.value instanceof Date) {
        let time = (control.value as Date).getTime();
        let max = this.maxDate.getTime();
        let min = this.minDate.getTime();

        if (time < min) {
          console.log('Date is lower than: ' + min);
          return {
            "dateValidator": {
              reason: 'Date is lower than: ' + min,
              value: control.value
            }
          };
        }

        if (time > max) {
          console.log('Date is greater than: ' + max);
          return {
            "dateValidator": {
              reason: 'Date is greater than: ' + max,
              value: control.value
            }
          };
        }
      }

      return null;
    }
  }

  public linePatternValidatorFn(): ValidatorFn {
    return (control) => {
      //TODO
      return null;
    };
  }

  toggleLinePatternHelpVisibility() {
    this.linePatternHelpVisibility = !this.linePatternHelpVisibility;
  }

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  back(): void {
    this.validate();
    this._router.navigate(['/user']);
  }

  submit(): void {
    if (this.validate()) {
      this._router.navigate(['/signal-settings']);
    }
  }

  private validate(): boolean {
    if (this.form.valid) {
      this._store.dispatch(APPLY_GENERIC_SETTINGS_ACTION({
        data: {
          start: this.form.controls['start'].value,
          end: this.form.controls['end'].value,
          frequency: this.form.controls['frequency'].value,
          step: this.form.controls['step'].value,
          signal: this.form.controls['signal'].value,
          linePattern: this.form.controls['linePattern'].value,
        }
      }));
      let inputs: InputBase[] = [];
      Object.values(this.availableSignals).forEach((item: SignalSettings) => {
        console.log('Inputs associated to the signal: ', item.name, inputs);
        if (item.name === this.form.controls['signal'].value) {
          console.log('Inputs: ', item.inputs);
          if (item.inputs) {
            inputs = item.inputs;
          }
        }
      });
      console.log('Inputs associated to the choosen signal: ', inputs);
      this._store.dispatch(APPLY_SIGNAL_SETTINGS_ACTION({
        data: inputs
      }));
      return true;
    }

    return false;
  }
}
