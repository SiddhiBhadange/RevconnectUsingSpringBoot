import { Component, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  posts: any[] = [];
  newPost = '';

  constructor(private postService: PostService) {}

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts() {
    this.postService.getAllPosts().subscribe((data: any) => {
      this.posts = data;
    });
  }

  createPost() {
    if (!this.newPost.trim()) return;

    this.postService.createPost(this.newPost).subscribe(() => {
      this.newPost = '';
      this.loadPosts();
    });
  }
}