import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { MainState, userSelector } from './main.store';
import { Store } from '@ngrx/store';
import { APPLY_USER_DATA_ACTION, RESET_ACTION } from './main.actions';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit, OnDestroy {
  private _subscriptions: Subscription[] = [];
  form: FormGroup;
  formValid: boolean = false;

  constructor(private _router: Router, private _store: Store<{ main: MainState }>) {    
    this.form = new FormGroup({
      username: new FormControl(null, [Validators.required]),
      email: new FormControl(null, [Validators.required, Validators.email]),
    });
  }

  ngOnInit(): void {
    this._subscriptions.push(this._store.select(userSelector).subscribe(d => {      
      if (d) {
        this.form.patchValue({
          username: d.username, 
          email: d.email
        });
        this.formValid = this. form.valid;
      }
    }));
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

  next(): void {
    if (this.form.valid) {
      this._store.dispatch(APPLY_USER_DATA_ACTION({ 
        username: this.form.controls['username'].value,
        password: '', // Not used in this version
        email: this.form.controls['email'].value
      }));
      this._router.navigate(['/settings']);
    }
  }
}
