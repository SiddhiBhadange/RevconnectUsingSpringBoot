import { Component } from '@angular/core';
import { AuthService } from './features/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  constructor(private authService: AuthService) {}

ngOnInit() {
  const token = localStorage.getItem('token');

  if (token) {
    this.authService.getCurrentUser().subscribe();
  }
   this.authService.restoreSession();
}
}

