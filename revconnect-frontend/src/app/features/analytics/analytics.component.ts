import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {

  analytics: any;

  constructor(private http: HttpClient
  ) {
  }
  ngOnInit(): void {
    this.http.get('http://localhost:8080/api/analytics/me')
      .subscribe(data => this.analytics = data);
  }
}