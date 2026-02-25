import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../../core/services/post.service';
import { CommentService } from '../../../../core/services/comment.service';
import { Post, Comment } from '../../dashboard.module';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  posts: Post[] = [];
  newPost = '';
  selectedImage: File | null = null;

  constructor(
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  // =========================
  // IMAGE SELECT
  // =========================
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedImage = input.files?.[0] ?? null;
  }

  // =========================
  // CREATE POST
  // =========================
  createPost(): void {

    if (!this.newPost.trim()) return;

    const formData = new FormData();
    formData.append('content', this.newPost);

    if (this.selectedImage) {
      formData.append('image', this.selectedImage);
    }

    this.postService.createPost(formData)
      .subscribe({
        next: (created: Post) => {
          this.posts.unshift(created);
          this.newPost = '';
          this.selectedImage = null;
        },
        error: (err) => console.error('Create post failed', err)
      });
  }

  // =========================
  // LOAD POSTS
  // =========================
  loadPosts(): void {
    this.postService.getAllPosts()
      .subscribe({
        next: (data: Post[]) => {
          this.posts = data;
        },
        error: (err) => console.error('Load posts failed', err)
      });
  }

  // =========================
  // DELETE POST
  // =========================
  deletePost(post: Post): void {
    this.postService.deletePost(post.id)
      .subscribe(() => {
        this.posts = this.posts.filter(p => p.id !== post.id);
      });
  }

  // =========================
  // DELETE COMMENT
  // =========================
  deleteComment(post: Post, comment: Comment): void {

    this.commentService.deleteComment(comment.id)
      .subscribe(() => {

        post.comments = post.comments?.filter(c => c.id !== comment.id);
        post.commentCount = Math.max(0, post.commentCount - 1);

      });
  }

  // =========================
  // LIKE / UNLIKE
  // =========================
  toggleLike(post: Post): void {

    if (post.likedByCurrentUser) {

      this.postService.unlikePost(post.id)
        .subscribe(() => {
          post.likedByCurrentUser = false;
          post.likeCount = Math.max(0, post.likeCount - 1);
        });

    } else {

      this.postService.likePost(post.id)
        .subscribe(() => {
          post.likedByCurrentUser = true;
          post.likeCount++;
        });

    }
  }

  // =========================
  // TOGGLE COMMENTS
  // =========================
  toggleComments(post: Post): void {

    post.showComments = !post.showComments;

    if (post.showComments && !post.comments) {
      this.commentService.getComments(post.id)
        .subscribe((res: Comment[]) => {
          post.comments = res;
        });
    }
  }

  // =========================
  // ADD COMMENT
  // =========================
  addComment(post: Post): void {

    if (!post.newComment?.trim()) return;

    this.commentService.addComment(post.id, post.newComment)
      .subscribe((newComment: Comment) => {

        post.newComment = '';

        if (!post.comments) {
          post.comments = [];
        }

        post.comments.unshift(newComment);
        post.commentCount++;

      });
  }

  // =========================
  // FORMAT HASHTAGS
  // =========================
  formatContent(content: string): string {
    return content.replace(
      /#(\w+)/g,
      '<span class="hashtag">#$1</span>'
    );
  }

}