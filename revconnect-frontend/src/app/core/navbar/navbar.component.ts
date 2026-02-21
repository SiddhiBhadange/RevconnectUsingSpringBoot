import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../features/auth/auth.service';
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  isLoggedIn = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  
  checkLogin() {
    this.authService.getCurrentUser().subscribe({
      next: () => this.isLoggedIn = true,
      error: () => this.isLoggedIn = false
    });
  }
 ngOnInit() {
    // 🔥 subscribe to state
    this.authService.isLoggedIn$.subscribe(
      status => this.isLoggedIn = status
    );
  }


  logout() {
    this.authService.logout().subscribe(() => {
      this.isLoggedIn = false;
      this.router.navigate(['/auth/login']);
    });
  }
}
