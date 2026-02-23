import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '../../features/dashboard/services/notification.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  notifications: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications() {
    this.http.get<any[]>('http://localhost:8080/api/notifications', {
      withCredentials: true
    }).subscribe(data => {
      this.notifications = data;
    });
  }

  markAsRead(id: number) {
    this.http.put(
      `http://localhost:8080/api/notifications/${id}/read`,
      {},
      { withCredentials: true }
    ).subscribe(() => {
      this.loadNotifications();
    });
  }
  markAsRead(id: number) {
  this.http.put(
    `http://localhost:8080/api/notifications/${id}/read`,
    {},
    { withCredentials: true }
  ).subscribe(() => {

    const notification = this.notifications.find(n => n.id === id);
    if (notification) notification.read = true;

    this.notificationService.decrease();
  });
}
}