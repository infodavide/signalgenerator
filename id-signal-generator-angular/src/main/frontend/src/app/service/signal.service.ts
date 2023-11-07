import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subscription, catchError, first, last, map, of, throwError } from 'rxjs';
import { UserData, SettingsData, SignalSettings } from '../main.store';
import { environment } from '../../environments/environment';
import { InputBase } from '../dynamic-form';
import { DynamicFormService } from './dynamic-form.service';

@Injectable({
  providedIn: 'root'
})
export class SignalService {

  private _baseUrl!: string;

  constructor(private _client: HttpClient, private _dynamicFormService: DynamicFormService) {
    this._baseUrl = environment.apiBaseUrl + '/signal';
    console.log('Base URL: ', this._baseUrl);
  }

  fetchAvailableSignals(): Observable<SignalSettings[]> {
    let url = this._baseUrl + '/available';
    console.log('Fetching available singals from: ', url);

    return this._client.get(url).pipe(
      catchError(error => {
        let message: string;

        if (error.error instanceof ErrorEvent) {
          message = `Error: ${error.error.message}`;
        } else {
          message = this._getErrorMessage(error);
        }

        return throwError(() => Error(message));
      }),
      map(items => {
        let r: SignalSettings[] = [];
        Object.values(items).forEach((item: Object) => {
          console.log('Data: ', item);

          if (item.hasOwnProperty('name')) {
            r.push(item as SignalSettings);
          }
        });

        return r;
      })
    );
  }

  post(user: Observable<UserData | null>, settings: Observable<SettingsData | null>, signalSettings: Observable<InputBase[] | null>): Observable<any> {
    let data = {
      user: {},
      settings: {},
      signalSettings: [] as InputBase[]
    };
    user.pipe(first()).subscribe(d => {
      if (d) {
        data.user = d;
      }
    });
    settings.pipe(first()).subscribe(d => {
      if (d) {
        data.settings = d;
      }
    });
    signalSettings.pipe(first()).subscribe(d => {
      if (d) {
        data.signalSettings = d.map(o => Object.assign({}, o));
      }
    });

    console.log('Consolidated data: ', data);

    if (!data.user) {
      throw Error('User not specified');
    }

    if (!data.settings) {
      throw Error('Settings not specified');
    }

    if (!data.signalSettings) {
      throw Error('Signal settings not specified');
    }

    console.log('Posting data: ', data, 'to: ', this._baseUrl);

    return this._client.post(this._baseUrl, data).pipe(
      catchError(error => {
        console.log('HTTP error: ', error);
        let message: string;

        if (error.error instanceof ErrorEvent) {
          message = error.error.message;
        } else {
          message = this._getErrorMessage(error);
        }

        return throwError(() => Error(message));
      })
    );
  }

  preview(user: Observable<UserData | null>, settings: Observable<SettingsData | null>, signalSettings: Observable<InputBase[] | null>): Observable<any> {
    let url = this._baseUrl + '/preview';
    let data = {
      user: {},
      settings: {},
      signalSettings: [] as InputBase[]
    };
    user.pipe(first()).subscribe(d => {
      if (d) {
        data.user = d;
      }
    });
    settings.pipe(first()).subscribe(d => {
      if (d) {
        data.settings = d;
      }
    });
    signalSettings.pipe(first()).subscribe(d => {
      if (d) {
        data.signalSettings = d.map(o => Object.assign({}, o));
      }
    });

    console.log('Consolidated data: ', data);

    if (!data.user) {
      throw Error('User not specified');
    }

    if (!data.settings) {
      throw Error('Settings not specified');
    }

    if (!data.signalSettings) {
      throw Error('Signal settings not specified');
    }

    console.log('Posting data: ', data, 'to: ', url);
    const headers = new HttpHeaders({
      'Accept': 'image/png',
    });

    return this._client.post(url, data, { headers: headers, responseType: 'blob' }).pipe(
      catchError(error => {
        console.log('HTTP error: ', error);
        let message: string;

        if (error.error instanceof ErrorEvent) {
          message = error.error.message;
        } else {
          message = this._getErrorMessage(error);
        }

        return throwError(() => Error(message));
      }),
      map((d: object) => {
        console.log("Service received data of type: ", typeof d);

        if (d instanceof Blob) {
          return d as Blob;
        }
        else if (d instanceof Response) {
          return (d as Response).blob();
        } else {
          return d;
        }
      })
    );
  }

  private _getErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
      case 400: {
        return `Bad request: ${error.message}`;
      }
      case 404: {
        return `Not Found: ${error.message}`;
      }
      case 403: {
        return `Access Denied: ${error.message}`;
      }
      case 500: {
        return `Internal Server Error: ${error.message}`;
      }
      default: {
        return `Unknown Server Error: ${error.message}`;
      }
    }
  }
}
