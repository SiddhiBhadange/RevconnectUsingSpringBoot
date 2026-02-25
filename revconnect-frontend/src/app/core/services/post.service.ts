import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../../features/dashboard/dashboard.module';

@Injectable({ providedIn: 'root' })
export class PostService {

  private API_URL = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(
      this.API_URL,
      { withCredentials: true }
    );
  }

  createPost(data: FormData): Observable<Post> {
    return this.http.post<Post>(
      this.API_URL,
      data,
      { withCredentials: true }
    );
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${postId}`,
      { withCredentials: true }
    );
  }

  likePost(postId: number): Observable<void> {
    return this.http.post<void>(
      `http://localhost:8080/api/likes/${postId}`,
      {},
      { withCredentials: true }
    );
  }

  unlikePost(postId: number): Observable<void> {
    return this.http.delete<void>(
      `http://localhost:8080/api/likes/${postId}`,
      { withCredentials: true }
    );
  }
}