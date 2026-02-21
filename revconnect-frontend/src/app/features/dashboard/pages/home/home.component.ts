import { Component, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  posts: any[] = [];
  newPost = '';

  constructor(private postService: PostService,
              private commentService: CommentService
  ) {}

  ngOnInit() {
    this.loadPosts();
  }


  createPost() {
    if (!this.newPost.trim()) return;

    this.postService.createPost(this.newPost).subscribe(() => {
      this.newPost = '';
      this.loadPosts();
    });
  }
  loadPosts() {
  this.postService.getAllPosts().subscribe((data: any) => {
    console.log("POSTS:", data);
    this.posts = data;
  });
}
toggleLike(post: any) {
  this.postService.toggleLike(post.id).subscribe(() => {
    post.likedByCurrentUser = !post.likedByCurrentUser;

    if (post.likedByCurrentUser) {
      post.likeCount++;
    } else {
      post.likeCount--;
    }
  });
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