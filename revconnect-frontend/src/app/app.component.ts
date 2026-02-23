import { Component } from '@angular/core';
import { AuthService } from './features/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  constructor(private authService: AuthService) {}

ngOnInit() {
  this.authService.checkAuth().subscribe();
}
}