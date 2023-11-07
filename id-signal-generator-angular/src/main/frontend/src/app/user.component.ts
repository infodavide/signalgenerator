import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { MainState, userSelector } from './main.store';
import { Store } from '@ngrx/store';
import { APPLY_USER_DATA_ACTION, RESET_ACTION } from './main.actions';
import { Router, RouterModule } from '@angular/router';
import { AbstractFormComponent } from './abstract-form-component';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends AbstractFormComponent {

  constructor(private _router: Router, private _store: Store<{ main: MainState }>) {    
    super();
    this.form.addControl('username', new FormControl<string>('', [Validators.required, Validators.maxLength(96)]));
    this.form.addControl('email', new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]));
  }

  override ngOnInit(): void {    
    this._subscriptions.push(this._store.select(userSelector).subscribe(d => {      
      if (d) {
        this.form.patchValue({
          username: d.username, 
          email: d.email
        });
        this.formValid = this. form.valid;
      }
    }));
    super.ngOnInit();
  }

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  submit(): void {
    if (this.form.valid) {
      this._store.dispatch(APPLY_USER_DATA_ACTION({ 
        data: {
          username: this.form.controls['username'].value,
          password: '', // Not used in this version
          email: this.form.controls['email'].value
        }
      }));
      this._router.navigate(['/settings']);
    }
  }
}
