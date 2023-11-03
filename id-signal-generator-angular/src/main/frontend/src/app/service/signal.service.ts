import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, first, map, throwError } from 'rxjs';
import { UserData, SettingsData, SignalSettings } from '../main.store';

@Injectable({
  providedIn: 'root'
})
export class SignalService {

  private _url: string = 'api/request';

  constructor(private _client: HttpClient) {
  }

  fetchAvailableSignals(): Observable<SignalSettings[]> {
    return this._client.get(this._url).pipe(
      catchError(error => {
        let message: string;

        if (error.error instanceof ErrorEvent) {
          message = `Error: ${error.error.message}`;
        } else {
          message = this._getErrorMessage(error);
        }

        return throwError(() => Error(message));
      }),
      map(data => {
        let r: SignalSettings[] = [];
        console.log(data);
        type ObjectKey = keyof typeof data;
        let name = null;

        if (data.hasOwnProperty('name')) {
          name = data['name' as ObjectKey];
        }

        let settings = null;

        if (data.hasOwnProperty('settings')) {
          let obj = data['settings' as ObjectKey];
          settings = new Map(Object.entries(obj));
        }

        if (name != null && settings != null) {
          r.push({
            name: name.toString(),
            settings: settings
          } as SignalSettings);
        }

        return r;
      })
    );
  }

  post(user: Observable<UserData>, settings: Observable<SettingsData>, properties: Observable<Map<String, object>>): Observable<any> {
    let data = {
      user: {},
      settings: {},
      properties: {}
    };
    user.pipe(first()).subscribe(d => {
      data.user = d;
    });
    settings.pipe(first()).subscribe(d => {
      data.settings = d;
    });
    properties.pipe(first()).subscribe(d => {
      data.properties = (d: Map<String, object>) => Object.fromEntries(d.entries());;
    });
    return this._client.post(this._url, data).pipe(
      catchError(error => {
        let message: string;

        if (error.error instanceof ErrorEvent) {
          message = `Error: ${error.error.message}`;
        } else {
          message = this._getErrorMessage(error);
        }

        return throwError(() => Error(message));
      })
    );
  }

  private _getErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
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
