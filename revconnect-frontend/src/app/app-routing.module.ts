import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotificationsComponent } from './features/dashboard/pages/notifications/notifications.component';
import { ConnectionsComponent } from './features/connections/connections.component';
import { SearchUserComponent } from './features/dashboard/pages/search-user/search-user.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ChatComponent } from './features/dashboard/pages/chat/chat.component';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    loadChildren: () =>
      import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  {
  path: 'notifications',
  component: NotificationsComponent
},{
  path: 'connections',
  component: ConnectionsComponent
},{
  path: 'search',
  component: SearchUserComponent
},
{
  path: 'profile/:id',
  component: ProfileComponent
},{ path: 'chat/:id', component: ChatComponent },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
