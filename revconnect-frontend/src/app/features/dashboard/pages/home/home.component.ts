import { Component, OnInit, OnDestroy } from '@angular/core';
import { PostService } from '../../../../core/services/post.service';
import { CommentService } from '../../../../core/services/comment.service';
import { AuthService } from '../../../../features/auth/auth.service';
import { Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {

  posts: any[] = [];
  newPost = '';
  newPrivacy = 'EVERYONE';
  private pollingSubscription?: Subscription;
  currentUserId: number | null = null;

  constructor(private postService: PostService,
    private commentService: CommentService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.getCurrentUser().subscribe((user: any) => {
      this.currentUserId = user.id;
    });
    this.startPollingPosts();
  }

  ngOnDestroy() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  startPollingPosts() {
    // Poll every 10 seconds for new posts
    this.pollingSubscription = timer(0, 10000).pipe(
      switchMap(() => this.postService.getAllPosts())
    ).subscribe((data: any) => {
      // Keep local state for open comments/likes when updating
      this.updatePostsPreservingState(data);
    });
  }

  updatePostsPreservingState(newPosts: any[]) {
    if (this.posts.length === 0) {
      this.posts = newPosts;
      return;
    }

    // Merge new posts while keeping the local UI state (showComments, newComment)
    this.posts = newPosts.map(newPost => {
      const existingPost = this.posts.find(p => p.id === newPost.id);
      if (existingPost) {
        newPost.showComments = existingPost.showComments;
        newPost.newComment = existingPost.newComment;
        if (existingPost.showComments && !newPost.comments) {
          newPost.comments = existingPost.comments;
        }
      }
      return newPost;
    });
  }


  createPost() {
    if (!this.newPost.trim()) return;

    this.postService.createPost(this.newPost, this.newPrivacy).subscribe(() => {
      this.newPost = '';
      this.newPrivacy = 'EVERYONE';
      this.loadPosts();
    });
  }

  deletePost(post: any) {
    if (confirm('Are you sure you want to delete this post?')) {
      this.postService.deletePost(post.id).subscribe({
        next: () => {
          this.posts = this.posts.filter(p => p.id !== post.id);
        },
        error: (err) => console.error('Failed to delete post:', err)
      });
    }
  }
  loadPosts() {
    this.postService.getAllPosts().subscribe((data: any) => {
      console.log("POSTS:", data);
      this.posts = data;
    });
  }
  toggleLike(post: any) {

    if (post.likedByCurrentUser) {

      // UNLIKE
      this.postService.unlikePost(post.id).subscribe(() => {
        post.likedByCurrentUser = false;
        post.likeCount--;
      });

    } else {

      // LIKE
      this.postService.likePost(post.id).subscribe(() => {
        post.likedByCurrentUser = true;
        post.likeCount++;
      });

    }
  }
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
          .subscribe(res => {
            post.comments = res;
            post.commentCount = res.length;
          });
      });
  }
}
