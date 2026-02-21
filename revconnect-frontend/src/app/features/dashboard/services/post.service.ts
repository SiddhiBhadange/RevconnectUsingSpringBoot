import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PostService {

  private API_URL = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) {}

  getAllPosts() {
    return this.http.get(this.API_URL, { withCredentials: true });
  }

  createPost(content: string) {
    return this.http.post(
      this.API_URL,
      { content },
      { withCredentials: true }
    );
  }
}
