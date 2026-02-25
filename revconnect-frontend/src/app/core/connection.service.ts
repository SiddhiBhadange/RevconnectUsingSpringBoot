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
      { withCredentials: true }  
    );
  }

  // Accept / Reject request
  respond(requestId: number, status: string): Observable<any> {
    return this.http.post(
      `${this.API}/respond/${requestId}?status=${status}`,
      {},
      { withCredentials: true }   
    );
  }

  // Get pending requests
  getPending(): Observable<any> {
    return this.http.get(
      `${this.API}/pending`,
      { withCredentials: true }  
    );
  }

  // Get connection status with specific user
  getStatus(userId: number): Observable<any> {
    return this.http.get(
      `${this.API}/status/${userId}`,
      { withCredentials: true }   
    );
  }

  // Get all connections
  getConnections(): Observable<any> {
    return this.http.get(
      `${this.API}`,
      { withCredentials: true }  
    );
  }
  removeConnection(userId: number) {
  return this.http.delete(
    `${this.API}/remove/${userId}`,
    { withCredentials: true }
  );
}
}
