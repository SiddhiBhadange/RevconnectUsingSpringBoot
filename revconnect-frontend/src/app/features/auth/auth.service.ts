import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private API_URL = 'http://localhost:8080/api/auth';

  // 🔥 ADD THIS (missing in your code)
  private loggedIn = new BehaviorSubject<boolean>(false);
isLoggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  login(usernameOrEmail: string, password: string) {
    return this.http.post(
      `${this.API_URL}/login`,
      { usernameOrEmail, password },
      { withCredentials: true }
    ).pipe(
      tap(() => this.loggedIn.next(true))   // 🔥 updates navbar
    );
  }

  register(data: any) {
    return this.http.post(
      `${this.API_URL}/register`,
      data,
      { withCredentials: true }
    );
  }

  logout() {
    return this.http.post(
      `${this.API_URL}/logout`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(() => this.loggedIn.next(false))  // 🔥 update on logout
    );
  }
  getCurrentUser() {
     return this.http.get( 'http://localhost:8080/api/users/me',
       { withCredentials: true } ); }

       checkAuth() {
    return this.http.get(
      'http://localhost:8080/api/users/me',
      { withCredentials: true }
    ).pipe(
      tap(() => this.loggedIn.next(true)),
      catchError(() => {
        this.loggedIn.next(false);
        return of(null);
      })
    );
  }
}




