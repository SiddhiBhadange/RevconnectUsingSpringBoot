import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { FormsModule } from '@angular/forms';
import { CommentService } from './services/comment.service';


@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    FormsModule,
    CommonModule
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

