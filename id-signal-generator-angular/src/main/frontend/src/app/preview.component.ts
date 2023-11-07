import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MainState, settingsSelector, signalSettingsSelector, userSelector } from './main.store';
import { SignalService } from './service/signal.service';
import { RESET_ACTION } from './main.actions';
import { AbstractComponent } from './abstract-component';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { catchError, last, of } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.scss']
})
export class PreviewComponent extends AbstractComponent implements OnInit {
  preview: any;

  constructor(private _router: Router, private _sanitizer: DomSanitizer, private _store: Store<{ main: MainState }>, private _service: SignalService, private _snackBar: MatSnackBar) {
    super();
  }

  ngOnInit(): void {
    this._subscriptions.push(
      this._service.preview(
        this._store.select(userSelector),
        this._store.select(settingsSelector),
        this._store.select(signalSettingsSelector)
      ).pipe(
        catchError(error => {
          console.log('Service error: ', error);
          let message: string;

          if (error.message) {
            message = `Error: ${error.message}`;
          } else {
            message = `Unknown error`;
          }
          let snackBarConfig = new MatSnackBarConfig();
          snackBarConfig.horizontalPosition = 'center';
          snackBarConfig.verticalPosition = 'bottom';
          snackBarConfig.panelClass = 'snackbar-error';
          //snackBarConfig.duration = 4000;
          this._snackBar.open('Cannot preview signal, reason: ' + message, 'Error', snackBarConfig);

          return of();
        }),
        last((d) => {
          console.log("Component received data of type: ", typeof d);
          var reader = new FileReader();
          reader.onloadend = () => {
            let element = document.querySelector('#preview_image');

            if (element) {
              this.preview = reader.result ;
            }
          }
          reader.readAsDataURL(d);

          return true;
        })
      ).subscribe());
  }  

  cancel(): void {
    this._store.dispatch(RESET_ACTION());
    this._router.navigate(['/home']);
  }

  back(): void {
    this._router.navigate(['/signal-settings']);
  }

  submit(): void {
    this._subscriptions.push(
      this._service.post(
        this._store.select(userSelector),
        this._store.select(settingsSelector),
        this._store.select(signalSettingsSelector)
      ).pipe(
        catchError(error => {
          console.log('Service error: ', error);
          let message: string;

          if (error.message) {
            message = `Error: ${error.message}`;
          } else {
            message = `Unknown error`;
          }
          let snackBarConfig = new MatSnackBarConfig();
          snackBarConfig.horizontalPosition = 'center';
          snackBarConfig.verticalPosition = 'bottom';
          snackBarConfig.panelClass = 'snackbar-error';
          //snackBarConfig.duration = 4000;
          this._snackBar.open('Cannot submit data, reason: ' + message, 'Error', snackBarConfig);

          return of();
        }),
        last(() => {
          let snackBarConfig = new MatSnackBarConfig();
          snackBarConfig.horizontalPosition = 'center';
          snackBarConfig.verticalPosition = 'bottom';
          snackBarConfig.panelClass = 'snackbar-info';
          snackBarConfig.duration = 2000;
          this._snackBar.open('Data submitted', 'Information', snackBarConfig);
          setTimeout(() => { this._router.navigate(['/home']) }, 2000);

          return true;
        })
      ).subscribe());
  }
}
