import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatCommonModule } from './mat-common/mat-common.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { MAIN_REDUCER, META_REDUCERS } from './main.reducer';
import { EffectsModule } from '@ngrx/effects';
import { MainEffects } from './main.effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { UserComponent } from './user.component';
import { SettingsComponent } from './settings.component';
import { PropertiesComponent } from './properties.component';
import { HomeComponent } from './home.component';

@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    SettingsComponent,
    PropertiesComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatCommonModule,
    FormsModule,
    ReactiveFormsModule,
    StoreModule.forRoot({ main: MAIN_REDUCER } , { metaReducers: META_REDUCERS }),
    EffectsModule.forRoot([MainEffects]),
    StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !isDevMode() })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
