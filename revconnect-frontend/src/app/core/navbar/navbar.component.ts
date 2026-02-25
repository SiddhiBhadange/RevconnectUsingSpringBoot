import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../features/auth/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { UserService } from '../../core/services/user.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  unreadCount: number = 0;
  isLoggedIn = false;

  searchQuery = '';
  searchResults: any[] = [];
  showDropdown = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private userService: UserService,
    public router: Router
  ) { }

  ngOnInit(): void {

    // Listen to login state
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;

      if (status) {
        this.notificationService.startPolling();
      } else {
        this.notificationService.stopPolling();
        this.notificationService.reset();
      }
    });

    // Refresh notifications on every route change (polling handles this now, but kept for immediate feedback)
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

  ngOnDestroy() {
    this.notificationService.stopPolling();
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.notificationService.stopPolling();
      this.router.navigate(['/auth/login']);
    });
  }

  onSearch() {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      this.showDropdown = false;
      return;
    }

    this.userService.searchUsers(this.searchQuery).subscribe(results => {
      this.searchResults = results;
      this.showDropdown = true;
    });
  }

  goToProfile(userId: number) {
    this.showDropdown = false;
    this.searchQuery = '';
    this.router.navigate(['/dashboard/profile', userId]);
  }
}
