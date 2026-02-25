import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  notifications: any[] = [];

  constructor(private http: HttpClient,
              private notificationService: NotificationService,
              private router: Router
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications() {
    this.http.get<any[]>('http://localhost:8081/api/notifications', {
      withCredentials: true
    }).subscribe(data => {
      this.notifications = data;
    });
  }


  markAsRead(id: number) {
  this.http.put(
    `http://localhost:8081/api/notifications/${id}/read`,
    {},
    { withCredentials: true }
  ).subscribe(() => {

    const notification = this.notifications.find(n => n.id === id);
    if (notification) notification.read = true;

    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
  this.router.navigate([this.router.url]);
});
  });
}
deleteNotification(id: number) {

  this.http.delete(
    `http://localhost:8081/api/notifications/${id}`,
    { withCredentials: true }
  ).subscribe(() => {

    this.notifications =
      this.notifications.filter(n => n.id !== id);

  });
}
}
