import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { SettingsComponent } from './settings.component';
import { PropertiesComponent } from './properties.component';
import { HomeComponent } from './home.component';

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
    path: 'properties',
    component: PropertiesComponent
  },
  { path: '**',
    redirectTo: 'home'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
