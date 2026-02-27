import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { NavbarComponent } from './core/navbar/navbar.component';
import { HttpClientModule } from '@angular/common/http';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { NotificationsComponent } from './features/dashboard/pages/notifications/notifications.component';
import { ConnectionsComponent } from './features/connections/connections.component';
import { SearchUserComponent } from './features/dashboard/pages/search-user/search-user.component';
import { ChatComponent } from './features/dashboard/pages/chat/chat.component';
import { AnalyticsComponent } from './features/analytics/analytics.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './core/auth.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    ProfileComponent,
    NotificationsComponent,
    ConnectionsComponent,
    SearchUserComponent,
    ChatComponent,
    AnalyticsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
     MatToolbarModule,   // <-- required for <mat-toolbar>
    MatButtonModule ,   // <-- required for <button mat-raised-button>
    HttpClientModule,
    FormsModule,
    MatIconModule,
    FormsModule
  ],
  bootstrap: [AppComponent],
  providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }
]
})
export class AppModule { }
