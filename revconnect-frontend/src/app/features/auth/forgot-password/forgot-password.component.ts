import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {

  success = '';
  error = '';

  constructor(private fb: FormBuilder, private http: HttpClient) {}

  forgotForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  onSubmit() {
    if (this.forgotForm.invalid) return;

    this.http.post(
      'http://localhost:8080/api/auth/forgot-password',
      this.forgotForm.value
    ).subscribe({
      next: () => {
        this.success = 'Reset link sent to your email.';
        this.error = '';
      },
      error: () => {
        this.error = 'Something went wrong. Try again.';
        this.success = '';
      }
    });
  }
}