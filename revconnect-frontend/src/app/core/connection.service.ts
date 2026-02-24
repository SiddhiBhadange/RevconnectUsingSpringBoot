import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {

  private API = 'http://localhost:8080/api/connections';

  constructor(private http: HttpClient) {}

  // Send connection request
  sendRequest(userId: number): Observable<any> {
    return this.http.post(
      `${this.API}/request/${userId}`,
      {},
      { withCredentials: true }   // ✅ IMPORTANT
    );
  }

  // Accept / Reject request
  respond(requestId: number, status: string): Observable<any> {
    return this.http.post(
      `${this.API}/respond/${requestId}?status=${status}`,
      {},
      { withCredentials: true }   // ✅ IMPORTANT
    );
  }

  // Get pending requests
  getPending(): Observable<any> {
    return this.http.get(
      `${this.API}/pending`,
      { withCredentials: true }   // ✅ IMPORTANT
    );
  }

  // Get connection status with specific user
  getStatus(userId: number): Observable<any> {
    return this.http.get(
      `${this.API}/status/${userId}`,
      { withCredentials: true }   // ✅ IMPORTANT
    );
  }

  // Get all connections
  getConnections(): Observable<any> {
    return this.http.get(
      `${this.API}`,
      { withCredentials: true }   // ✅ IMPORTANT
    );
  }
  removeConnection(userId: number) {
  return this.http.delete(
    `${this.API}/remove/${userId}`,
    { withCredentials: true }
  );
}
}