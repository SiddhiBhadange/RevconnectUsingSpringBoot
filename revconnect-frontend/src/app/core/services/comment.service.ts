import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../../features/dashboard/dashboard.module';

@Injectable({ providedIn: 'root' })
export class CommentService {

  private API_URL = 'http://localhost:8080/api/comments';

  constructor(private http: HttpClient) {}

  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(
      `${this.API_URL}/${postId}`,
      { withCredentials: true }
    );
  }

  addComment(postId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(
      `${this.API_URL}/${postId}`,
      { content },
      { withCredentials: true }
    );
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${commentId}`,
      { withCredentials: true }
    );
  }
}
