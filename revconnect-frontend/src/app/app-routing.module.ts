import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotificationsComponent } from './features/dashboard/pages/notifications/notifications.component';
import { ConnectionsComponent } from './features/connections/connections.component';
import { SearchUserComponent } from './features/dashboard/pages/search-user/search-user.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ChatComponent } from './features/dashboard/pages/chat/chat.component';
import { AnalyticsComponent } from './features/analytics/analytics.component';
import { AuthGuard } from './core/auth.guard';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  {
  path: 'notifications',
  component: NotificationsComponent,
  canActivate: [AuthGuard] 
},{
  path: 'connections',
  component: ConnectionsComponent,
  canActivate: [AuthGuard] 
},{
  path: 'search',
  component: SearchUserComponent,
  canActivate: [AuthGuard] 
},
{
  path: 'profile/:id',
  component: ProfileComponent,
  canActivate: [AuthGuard]
},{ path: 'chat/:id', component: ChatComponent,
  canActivate: [AuthGuard] 
 },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'analytics', component: AnalyticsComponent,
    canActivate: [AuthGuard] 
   }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
