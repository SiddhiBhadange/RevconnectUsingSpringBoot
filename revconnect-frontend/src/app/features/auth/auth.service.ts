import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private API_URL = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(usernameOrEmail: string, password: string) {
    return this.http.post(
      `${this.API_URL}/login`,
      { usernameOrEmail, password },
      { withCredentials: true } // 🔥 REQUIRED
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
    );
  }
}