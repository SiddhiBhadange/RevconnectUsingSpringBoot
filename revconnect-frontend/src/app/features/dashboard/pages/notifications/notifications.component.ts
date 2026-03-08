import { HttpClient } from '@angular/common/http';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Router } from '@angular/router';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  notifications: any[] = [];

  constructor(private http: HttpClient,
              private notificationService: NotificationService,
              private router: Router,
              private cdr: ChangeDetectorRef
  )
   {changeDetection: ChangeDetectionStrategy.OnPush}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications() {
    this.http.get<any[]>('http://localhost:8080/api/notifications').subscribe(data => {
      this.notifications = data;
      this.cdr.detectChanges(); // Trigger change detection
    });
  }


 markAsRead(id: number) {
  this.http.put(
    `http://localhost:8080/api/notifications/${id}/read`,
    {},
    { responseType: 'text' }   
  ).subscribe(() => {

    this.notifications = this.notifications.map(n =>
      n.id === id ? { ...n, read: true } : n
    );

    this.notificationService.refreshUnreadCount();
  });
}
deleteNotification(id: number) {

  this.http.delete(
    `http://localhost:8080/api/notifications/${id}`,
       { responseType: 'text' }
  ).subscribe(() => {

    this.notifications =
      this.notifications.filter(n => n.id !== id);
      this.cdr.detectChanges(); // Trigger change detection

  });
}
}
