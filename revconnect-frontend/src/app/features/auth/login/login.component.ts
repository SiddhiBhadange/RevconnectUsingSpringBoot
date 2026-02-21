import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  loginForm: FormGroup;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
  usernameOrEmail: ['', Validators.required],
  password: ['', Validators.required]
});
  }

  onSubmit(): void {
  if (this.loginForm.invalid) return;

  this.error = '';
  this.success = '';
  const { usernameOrEmail, password } = this.loginForm.value;

  this.authService.login(usernameOrEmail, password).subscribe({
    next: () => {
        this.success = 'Login successful!';
      this.router.navigate(['/dashboard']);
    },
  error: (err) => {
  console.error('Login error:', err);

  if (err.status === 401) {
    this.error = 'Invalid username or password';
  } else if (err.status === 403) {
    this.error = 'Access denied';
  } else if (err.error?.message) {
    this.error = err.error.message;
  } else {
    this.error = 'Login failed. Please try again.';
  }
}
  });
}
  }