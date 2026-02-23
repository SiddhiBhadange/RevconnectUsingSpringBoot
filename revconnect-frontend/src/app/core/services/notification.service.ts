import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  refreshUnreadCount() {
    this.http.get<number>(
      'http://localhost:8080/api/notifications/unread-count',
      { withCredentials: true }
    ).subscribe(count => {
      this.unreadCountSubject.next(count);
    });
  }

  decrease() {
    this.unreadCountSubject.next(
      Math.max(0, this.unreadCountSubject.value - 1)
    );
  }

  increase() {
    this.unreadCountSubject.next(
      this.unreadCountSubject.value + 1
    );
  }
   reset() {
  this.unreadCountSubject.next(0);
}
}