import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerForm: FormGroup;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.error = '';
    this.success = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.success = 'Registration successful! Redirecting to login...';

        // small delay so user can see message
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 1500);
      },
      error: () => {
        this.error = 'Registration failed';
      }
    });
  }
}