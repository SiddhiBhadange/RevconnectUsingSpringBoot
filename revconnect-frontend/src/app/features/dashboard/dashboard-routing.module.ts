import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from 'src/app/pages/profile/profile.component';
import { AuthGuard } from 'src/app/core/auth.guard';


const routes: Routes = [
 { path: 'profile', component: ProfileComponent,
  canActivate: [AuthGuard] 
  },       // My profile
  { path: 'profile/:id', component: ProfileComponent,
    canActivate: [AuthGuard] 
   },  
  { path: '', component: HomeComponent ,
    canActivate: [AuthGuard] 
  } // /dashboard
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
