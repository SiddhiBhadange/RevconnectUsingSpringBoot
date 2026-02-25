import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  messages: any[] = [];
  newMessage: string = '';
  receiverId!: number;
  currentUser: any;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

  this.http.get<any>('http://localhost:8080/api/users/me',
    { withCredentials: true })
    .subscribe(user => {
      this.currentUser = user;
    });

  this.receiverId = Number(this.route.snapshot.paramMap.get('id'));

  this.loadMessages();

  // 🔥 ADD THIS (Polling every 2 seconds)
  setInterval(() => {
    this.loadMessages();
  }, 2000);
}

  loadMessages() {
    this.http.get<any[]>(
      `http://localhost:8080/api/chat/${this.receiverId}`,
      { withCredentials: true }
    ).subscribe(data => {
      this.messages = data;
    });
  }

  sendMessage() {

    if (!this.newMessage.trim()) return;

    this.http.post(
      `http://localhost:8080/api/chat/${this.receiverId}`,
      this.newMessage,
      { withCredentials: true }
    ).subscribe(() => {

      this.newMessage = '';
      this.loadMessages();
    });
  }
}