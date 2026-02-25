import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ConnectionService } from '../../core/connection.service';
import { AuthService } from '../../features/auth/auth.service';

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

  followersCount = 0;
  followingCount = 0;
  isFollowing = false;

  connectionStatus = 'NONE';
  pendingRequestId: number | null = null;

  followers: any[] = [];
  following: any[] = [];

  showFollowersModal = false;
  showFollowingModal = false;
   selectedRole: string = 'PERSONAL';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private connectionService: ConnectionService,
    private authService: AuthService
  ) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    this.http.get<any>('http://localhost:8081/api/users/me', { withCredentials: true })
      .subscribe(me => {
        this.currentUser = me;

        this.route.paramMap.subscribe(params => {
          const id = params.get('id');

          if (id) {
            this.loadUserById(id);
          } else {
            this.user = me;
        
            this.loadPosts(me.id);
            this.loadFollowData();
            this.selectedRole = me.roles?.[0] || 'PERSONAL';
          }
        });
      });
  }

  /* ROLE CHECKS */

  get isBusiness(): boolean {
    return this.user?.roles?.includes('BUSINESS');
  }

  get isCreator(): boolean {
    return this.user?.roles?.includes('CREATOR');
  }

  get isAdmin(): boolean {
    return this.user?.roles?.includes('ADMIN');
  }

  /* LOAD USER */

  loadUserById(id: string) {
    this.http.get<any>(`http://localhost:8080/api/users/${id}`, { withCredentials: true })
      .subscribe(user => {
        this.user = user;
        this.loadPosts(user.id);
        this.loadFollowData();
        this.loadConnectionStatus();
        this.selectedRole = user.roles?.[0] || 'PERSONAL';
      });
  }

  loadPosts(userId: number) {
    this.http.get<any[]>(`http://localhost:8080/api/posts/user/${userId}`, { withCredentials: true })
      .subscribe(posts => this.posts = posts);
  }

  /* FOLLOW */

  loadFollowData() {
    if (!this.user?.id) return;

    this.http.get<number>(`http://localhost:8080/api/follow/${this.user.id}/followers/count`, { withCredentials: true })
      .subscribe(count => this.followersCount = count);

    this.http.get<number>(`http://localhost:8080/api/follow/${this.user.id}/following/count`, { withCredentials: true })
      .subscribe(count => this.followingCount = count);

    if (!this.isOwnProfile()) {
      this.http.get<boolean>(`http://localhost:8080/api/follow/${this.user.id}/is-following`, { withCredentials: true })
        .subscribe(status => this.isFollowing = status);
    }
  }

  followUser() {
    this.http.post(`http://localhost:8080/api/follow/${this.user.id}`, {}, { withCredentials: true })
      .subscribe(() => {
        this.isFollowing = true;
        this.followersCount++;
      });
  }

  unfollowUser() {
    this.http.delete(`http://localhost:8080/api/follow/${this.user.id}`, { withCredentials: true })
      .subscribe(() => {
        this.isFollowing = false;
        this.followersCount--;
      });
  }

  /* CONNECTION */

  loadConnectionStatus() {
    if (this.isOwnProfile() || !this.user?.id) return;

    this.connectionService.getStatus(this.user.id)
      .subscribe((res: any) => {
        if (res) {
          this.connectionStatus = res.status;
          this.pendingRequestId = res.id;
        } else {
          this.connectionStatus = 'NONE';
        }
      });
  }

  sendConnectionRequest() {
    this.connectionService.sendRequest(this.user.id)
      .subscribe(() => this.loadConnectionStatus());
  }

  acceptRequest() {
    if (!this.pendingRequestId) return;
    this.connectionService.respond(this.pendingRequestId, 'ACCEPTED')
      .subscribe(() => this.loadConnectionStatus());
  }

  rejectRequest() {
    if (!this.pendingRequestId) return;
    this.connectionService.respond(this.pendingRequestId, 'REJECTED')
      .subscribe(() => this.loadConnectionStatus());
  }

  removeConnection() {
    this.connectionService.removeConnection(this.user.id)
      .subscribe(() => this.loadConnectionStatus());
  }

  /* PROFILE */

  saveProfile() {

  // 🔥 THIS LINE WAS MISSING
  this.user.roles = [this.selectedRole];

  this.http.put(
    'http://localhost:8080/api/users/me',
    this.user,
    { withCredentials: true }
  ).subscribe({
    next: (updatedUser) => {
      console.log("Updated:", updatedUser);
      this.user = updatedUser;
      this.activeTab = 'about';
    },
    error: (err) => {
      console.error("Update failed:", err);
    }
  });
}

  isOwnProfile(): boolean {
    return this.currentUser?.id === this.user?.id;
  }

  deleteProfile() {
    if (!confirm('Are you sure?')) return;

    this.http.delete('http://localhost:8080/api/users/me',
      { responseType: 'text', withCredentials: true })
      .subscribe(() => {
        this.authService.logout();
        this.router.navigate(['/login']);
      });
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => this.user.profilePictureUrl = reader.result;
    reader.readAsDataURL(file);
  }

  /* FOLLOWERS MODAL */

  loadFollowers() {
    this.http.get<any[]>(`http://localhost:8080/api/follow/followers/${this.user.id}`, { withCredentials: true })
      .subscribe(data => this.followers = data);
  }

  loadFollowing() {
    this.http.get<any[]>(`http://localhost:8080/api/follow/following/${this.user.id}`, { withCredentials: true })
      .subscribe(data => this.following = data);
  }

  openFollowers() {
    this.loadFollowers();
    this.showFollowersModal = true;
  }

  openFollowing() {
    this.loadFollowing();
    this.showFollowingModal = true;
  }

  goToProfile(userId: number) {
    this.showFollowersModal = false;
    this.showFollowingModal = false;
    this.router.navigate(['/profile', userId]);
  }
 goToChat() {
  this.router.navigate(['/chat', this.user.id]);
}
}