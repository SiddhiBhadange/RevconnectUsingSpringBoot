import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
@Injectable({ providedIn: 'root' })
export class AuthService {

  private API_URL = 'http://localhost:8080/api/auth';

  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  login(usernameOrEmail: string, password: string) {
    return this.http.post<any>(
      `${this.API_URL}/login`,
      { usernameOrEmail, password }
    ).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        this.loggedIn.next(true);
      })
    );
  }

  register(data: any) {
    return this.http.post(
      `${this.API_URL}/register`,
      data
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.loggedIn.next(false);
    return this.http.post(`${this.API_URL}/logout`, {});
     this.router.navigate(['/login']);

  }

  getCurrentUser() {
    return this.http.get('http://localhost:8080/api/users/me');
  }

  restoreSession() {
    if (this.hasToken()) {
      this.loggedIn.next(true);
    }
  }
}