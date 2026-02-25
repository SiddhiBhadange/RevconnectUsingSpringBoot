import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { FormsModule } from '@angular/forms';
import { CommentService } from '../../core/services/comment.service';
import { MatIconModule } from '@angular/material/icon';
import { TrendingComponent } from './pages/trending/trending.component';

@NgModule({
  declarations: [HomeComponent, TrendingComponent],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    FormsModule,
    CommonModule,
    MatIconModule
  ]
})

export class DashboardModule {
  constructor(private commentService: CommentService) {}

  toggleComments(post: any) {
  post.showComments = !post.showComments;

  if (post.showComments && !post.comments) {
    this.commentService.getComments(post.id).subscribe(res => {
      post.comments = res;
    });
  }
}

addComment(post: any) {
  if (!post.newComment) return;

  this.commentService.addComment(post.id, post.newComment)
    .subscribe(() => {
      post.newComment = '';
      this.commentService.getComments(post.id)
        .subscribe(res => post.comments = res);
    });
}

 }
export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  username: string;
  isOwner: boolean;
}

export interface Post {
  id: number;
  content: string;
  hashtags: string[];
  pinned: boolean;
  createdAt: string;

  userId: number;
  username: string;

  likeCount: number;
  commentCount: number;
  likedByCurrentUser: boolean;
  isOwner: boolean;   // ✅ ADD THIS

  imageUrl?: string;

  showComments?: boolean;
  comments?: Comment[];
  newComment?: string;
}