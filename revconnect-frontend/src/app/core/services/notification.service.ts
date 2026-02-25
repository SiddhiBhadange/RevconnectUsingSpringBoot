import { Injectable } from '@angular/core';
import { BehaviorSubject, timer, Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();
  private pollingSubscription?: Subscription;

  constructor(private http: HttpClient) {}

  startPolling() {
    if (this.pollingSubscription && !this.pollingSubscription.closed) {
      return; 
    }
    // Poll every 10 seconds
    this.pollingSubscription = timer(0, 10000).pipe(
      switchMap(() => this.http.get<number>('http://localhost:8080/api/notifications/unread-count', { withCredentials: true })),
      tap(count => this.unreadCountSubject.next(count))
    ).subscribe();
  }

  stopPolling() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

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
