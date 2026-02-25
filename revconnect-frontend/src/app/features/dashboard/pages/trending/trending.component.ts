import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

interface Post {
  id: number;
  username: string;
  content: string;
  createdAt: string;
  likeCount?: number;
  commentCount?: number;
}

@Component({
  selector: 'app-trending',
  templateUrl: './trending.component.html',
  styleUrls: ['./trending.component.css']
})
export class TrendingComponent implements OnInit {

  posts: Post[] = [];
  tag: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';

  private readonly API_URL = 'http://localhost:8080/api/posts/search';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // Optional: load default trending tag
    // this.searchByTag('trending');
  }

  searchByTag(customTag?: string): void {

    const searchTag = customTag || this.tag;

    if (!searchTag || searchTag.trim() === '') {
      this.errorMessage = 'Please enter a hashtag';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.posts = [];

    this.http.get<Post[]>(
      `${this.API_URL}?tag=${encodeURIComponent(searchTag)}`,
      { withCredentials: true }   // 🔥 sends cookies (JSESSIONID)
    )
    .subscribe({
      next: (response: Post[]) => {
        this.posts = response;
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Trending fetch error:', error);

        if (error.status === 403) {
          this.errorMessage = 'Access denied. Please login first.';
        } else if (error.status === 401) {
          this.errorMessage = 'Unauthorized. Please login.';
        } else if (error.status === 0) {
          this.errorMessage = 'Backend server is not reachable.';
        } else {
          this.errorMessage = 'Failed to load posts.';
        }

        this.isLoading = false;
      }
    });
  }
}