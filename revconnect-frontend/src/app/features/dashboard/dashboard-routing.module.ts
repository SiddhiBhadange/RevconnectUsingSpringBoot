import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from 'src/app/pages/profile/profile.component';
import { TrendingComponent } from 'src/app/features/dashboard/pages/trending/trending.component';


const routes: Routes = [
 { path: 'profile', component: ProfileComponent },       // My profile
  { path: 'profile/:id', component: ProfileComponent },  
  { path: '', component: HomeComponent } ,
  // /dashboard
  {
    path: 'trending',
    component: TrendingComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
