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

  activeTab: string = 'about';

  followersCount: number = 0;
  followingCount: number = 0;
  isFollowing: boolean = false;

  showFollowersModal: boolean = false;
  showFollowingModal: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    // Load logged-in user first
    this.http.get<any>('http://localhost:8080/api/users/me', {
      withCredentials: true
    }).subscribe(me => {

      this.currentUser = me;

      // Check route param
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');

        if (id) {
          this.loadUserById(id);
        } else {
          this.user = me;
          this.loadPosts(me.id);
          this.loadFollowData();   // 🔥 important
        }
      });

    });
  }

  loadUserById(id: string) {
    this.http.get<any>(`http://localhost:8080/api/users/${id}`, {
      withCredentials: true
    }).subscribe(user => {
      this.user = user;
      this.loadPosts(user.id);
      this.loadFollowData();
    });
  }

  loadPosts(userId: number) {
    this.http.get<any[]>(
      `http://localhost:8080/api/posts/user/${userId}`,
      { withCredentials: true }
    ).subscribe(posts => this.posts = posts);
  }

  loadFollowData() {

    if (!this.user?.id) return;

    // Followers count
    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/followers/count`,
      { withCredentials: true }
    ).subscribe(count => this.followersCount = count);

    // Following count
    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/following/count`,
      { withCredentials: true }
    ).subscribe(count => this.followingCount = count);

    // Check follow state (only if not own profile)
    if (!this.isOwnProfile()) {
      this.http.get<boolean>(
        `http://localhost:8080/api/follow/${this.user.id}/is-following`,
        { withCredentials: true }
      ).subscribe(status => this.isFollowing = status);
    }
  }

  followUser() {

    if (!this.user?.id) return;

    this.http.post(
      `http://localhost:8080/api/follow/${this.user.id}`,
      {},
      { withCredentials: true }
    ).subscribe(() => {
      this.isFollowing = true;
      this.followersCount++;
    });
  }

  unfollowUser() {

    if (!this.user?.id) return;

    this.http.delete(
      `http://localhost:8080/api/follow/${this.user.id}`,
      { withCredentials: true }
    ).subscribe(() => {
      this.isFollowing = false;
      this.followersCount--;
    });
  }

  saveProfile() {
    this.http.put(
      'http://localhost:8080/api/users/me',
      this.user,
      { withCredentials: true }
    ).subscribe(() => {
      this.activeTab = 'about';
    });
  }

  isOwnProfile(): boolean {
    return this.currentUser &&
           this.user &&
           this.currentUser.id === this.user.id;
  }
}