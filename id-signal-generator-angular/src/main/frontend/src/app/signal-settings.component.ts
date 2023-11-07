import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subscription, catchError, first, last, of } from 'rxjs';
import { MainState, SettingsData, UserData, signalSettingsSelector, settingsSelector, userSelector } from './main.store';
import { APPLY_SIGNAL_SETTINGS_ACTION, RESET_ACTION } from './main.actions';
import { SignalService } from './service/signal.service';
import { InputBase } from './dynamic-form';
import { DynamicFormService } from './service/dynamic-form.service';
import { AbstractFormComponent } from './abstract-form-component';

@Component({
  selector: 'app-signal-settings',
  templateUrl: './signal-settings.component.html',
  styleUrls: ['./signal-settings.component.scss']
})
export class SignalSettingsComponent extends AbstractFormComponent {
  inputs!: InputBase[];
  user!: string;
  signal!: string;

  constructor(private _router: Router, private _store: Store<{ main: MainState }>, private _dynamicFormService: DynamicFormService, private _service: SignalService) {
    super();
  }

  override ngOnInit(): void {
    this._subscriptions.push(this._store.select(signalSettingsSelector).subscribe(d => {
      if (d) {
        this.inputs = d;
      } else {
        this.inputs = [];
      }

      console.log('Inputs in the dynamic form: ', this.inputs);
      this._dynamicFormService.appendToFormGroup(this.form, this.inputs);
      this.formValid = this.form.valid;
    }));
    this._subscriptions.push(this._store.select(userSelector).subscribe((d: UserData | null) => {
      if (d) {
        this.user = d.username + ' (' + d.email + ')';
      } else {
        this.user = '';
      }
    }));
    this._subscriptions.push(this._store.select(settingsSelector).subscribe((d: SettingsData | null) => {
      if (d) {
        this.signal = d.signal + ' from ' + d.start + ' to ' + d.end + ' at frequency ' + d.frequency + ' and step ' + d.step;
      } else {
        this.signal = '';
      }
    }));
    super.ngOnInit();
  }

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  back(): void {
    this.validate();
    this._router.navigate(['/settings']);
  }

  submit(): void {
    if (this.validate()) {
      this._router.navigate(['/preview']);
    }
  }

  private validate(): boolean {
    if (this.form.valid) {
      let data: InputBase[] = [];
      this.inputs.forEach((input) => {
        data.push({
          description: input.description,
          label: input.label,
          max: input.max,
          min: input.min,
          name: input.name,
          options: input.options,
          order: input.order,
          required: input.required,
          type: input.type,
          value: this.form.controls[input.name].value
        });
      });
      this._store.dispatch(APPLY_SIGNAL_SETTINGS_ACTION({ 
        data: this.inputs
      }));
      
      return true;
    }

    return false;
  }
}
