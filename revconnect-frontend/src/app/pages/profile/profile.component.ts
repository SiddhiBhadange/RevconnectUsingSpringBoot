import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  currentUser: any;
  user: any;
  posts: any[] = [];
  isEditing = false;
  // 🔥 ADD THESE
  activeTab: string = 'about';
  followersCount: number = 0;
  followingCount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}



  loadCurrentUser() {
    this.http.get<any>('http://localhost:8080/api/users/me', {
      withCredentials: true
    }).subscribe(user => {
      this.user = user;
      this.loadPosts(user.id);
    });
  }

  loadUserById(id: string) {
    this.http.get<any>(`http://localhost:8080/api/users/${id}`, {
      withCredentials: true
    }).subscribe(user => {
      this.user = user;
      this.loadPosts(user.id);
    });
  }

  loadPosts(userId: number) {
    this.http.get<any[]>(
      `http://localhost:8080/api/posts/user/${userId}`,
      { withCredentials: true }
    ).subscribe(posts => this.posts = posts);
  }
ngOnInit(): void {
  // First get logged-in user
  this.http.get<any>('http://localhost:8080/api/users/me', {
    withCredentials: true
  }).subscribe(me => {

    this.currentUser = me;

    // Then check route
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (id) {
        this.loadUserById(id);
      } else {
        this.user = me; // own profile
        this.loadPosts(me.id);
      }
    });

  });
}  loadProfile() {
    this.http.get('http://localhost:8080/api/users/me', {
      withCredentials: true
    }).subscribe(res => this.user = res);
  }

  enableEdit() {
    this.isEditing = true;
  }

  saveProfile() {
    this.http.put('http://localhost:8080/api/users/me',
      this.user,
      { withCredentials: true }
    ).subscribe(() => {
      this.isEditing = false;
      this.loadProfile();
    });
  }
  isOwnProfile(): boolean {
  return this.currentUser && this.user &&
         this.currentUser.id === this.user.id;
}
}