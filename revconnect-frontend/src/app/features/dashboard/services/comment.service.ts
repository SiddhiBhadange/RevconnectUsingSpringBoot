import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CommentService {

  private API = 'http://localhost:8080/api/comments';

  constructor(private http: HttpClient) {}

  getComments(postId: number) {
    return this.http.get<any[]>(`${this.API}/${postId}`, {
      withCredentials: true
    });
  }

  addComment(postId: number, content: string) {
    return this.http.post(
      `${this.API}/${postId}`,
      { content },
      { withCredentials: true }
    );
  }
}