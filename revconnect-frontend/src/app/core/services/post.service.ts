import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PostService {

  private API_URL = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) { }

  getAllPosts() {
    return this.http.get(this.API_URL);
  }

  createPost(content: string, privacy: string = 'EVERYONE') {
    return this.http.post(
      this.API_URL,
      { content, privacy }
    );
  }

  deletePost(postId: number) {
    return this.http.delete(
      `${this.API_URL}/${postId}`,
      { responseType: 'text' }
    );
  }
  likePost(postId: number) {
    return this.http.post(
      `http://localhost:8080/api/likes/${postId}`,
      {}
    );
  }

  unlikePost(postId: number) {
    return this.http.delete(
      `http://localhost:8080/api/likes/${postId}`
    );
  }
}
