import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { SettingsComponent } from './settings.component';
import { SignalSettingsComponent } from './signal-settings.component';
import { HomeComponent } from './home.component';
import { PreviewComponent } from './preview.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },{
    path: 'user',
    component: UserComponent
  },{
    path: 'settings',
    component: SettingsComponent
  },{
    path: 'signal-settings',
    component: SignalSettingsComponent
  },{
    path: 'preview',
    component: PreviewComponent
  },
  { path: '**',
    redirectTo: 'home'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes,{
    scrollPositionRestoration: 'enabled'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
