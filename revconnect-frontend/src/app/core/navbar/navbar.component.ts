import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../features/auth/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  unreadCount: number = 0;
  isLoggedIn = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    public router: Router
  ) {}

  ngOnInit(): void {

    // Listen to login state
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;

      if (status) {
        this.notificationService.refreshUnreadCount();
      } else {
        this.notificationService.reset();
      }
    });

    // Refresh notifications on every route change
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        if (this.isLoggedIn) {
          this.notificationService.refreshUnreadCount();
        }
      });

    // Subscribe to unread count changes
    this.notificationService.unreadCount$
      .subscribe((count: number) => {
        this.unreadCount = count;
      });
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/auth/login']);
    });
  }
}