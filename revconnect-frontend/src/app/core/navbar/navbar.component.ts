import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../features/auth/auth.service';
import { NotificationService } from '../../features/dashboard/services/notification.service';
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  unreadCount: number = 0;
  checkLogin() {
    this.authService.getCurrentUser().subscribe({
      next: () => this.isLoggedIn = true,
      error: () => this.isLoggedIn = false
    });
  }

  
 isLoggedIn = false;

  constructor(
    private authService: AuthService,
    public router: Router
  ) {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }
  ngOnInit(): void {
    this.checkLogin();
    this.loadUnreadCount();
   
  this.notificationService.unreadCount$
    .subscribe(count => {
      this.unreadCount = count;
    });

  this.notificationService.refreshUnreadCount();
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/auth/login']);
    });
  }
  loadUnreadCount() {
  this.authService.getCurrentUser().subscribe({
    next: () => {
      fetch('http://localhost:8080/api/notifications/unread-count', {
        credentials: 'include'
      })
      .then(res => res.json())
      .then(count => this.unreadCount = count);
    }
  });
}

  }
 


