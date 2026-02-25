import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ConnectionService } from '../../core/connection.service';
import { ChangeDetectorRef } from '@angular/core';

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

  // 🔥 CONNECTION STATE
  connectionStatus: string = 'NONE';
  // NONE | PENDING_SENT | PENDING_RECEIVED | CONNECTED
  pendingRequestId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private connectionService: ConnectionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    // Load logged-in user
    this.http.get<any>('http://localhost:8080/api/users/me', {
      withCredentials: true
    }).subscribe(me => {

      this.currentUser = me;

      this.route.paramMap.subscribe(params => {
        const id = params.get('id');

        if (id) {
          this.loadUserById(id);
        } else {
          this.user = me;
          this.loadPosts(me.id);
          this.loadFollowData();
        }
      });

    });
  }

  /* ========================= */
  /* LOAD USER */
  /* ========================= */

  loadUserById(id: string) {
    this.http.get<any>(
      `http://localhost:8080/api/users/${id}`,
      { withCredentials: true }
    ).subscribe(user => {
      this.user = user;
      this.loadPosts(user.id);
      this.loadFollowData();
      this.loadConnectionStatus();
    });
  }

  loadPosts(userId: number) {
    this.http.get<any[]>(
      `http://localhost:8080/api/posts/user/${userId}`,
      { withCredentials: true }
    ).subscribe(posts => this.posts = posts);
  }

  /* ========================= */
  /* FOLLOW */
  /* ========================= */

  loadFollowData() {

    if (!this.user?.id) return;

    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/followers/count`,
      { withCredentials: true }
    ).subscribe(count => this.followersCount = count);

    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/following/count`,
      { withCredentials: true }
    ).subscribe(count => this.followingCount = count);

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

  
  /* ========================= */

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
  /* ========================= */
  /* CONNECTION */
  /* ========================= */

loadConnectionStatus() {
  if (this.isOwnProfile() || !this.user?.id) return;

  this.connectionService.getStatus(this.user.id)
    .subscribe({
      next: (res: any) => {
        if (res) {
          this.connectionStatus = res.status;
          this.pendingRequestId = res.id;
        } else {
          this.connectionStatus = 'NONE';
        }
      },
      error: (err) => {
        console.error('Status error:', err);
        this.connectionStatus = 'NONE';
      }
    });
}

sendConnectionRequest() {
  if (!this.user?.id) return;

  this.connectionService.sendRequest(this.user.id)
    .subscribe({
      next: () => {
        // Immediately update UI
        this.connectionStatus = 'PENDING_SENT';

        // 🔥 Re-fetch real status from backend
        this.loadConnectionStatus();
      },
      error: (err) => {
        console.error('Connection request failed:', err);
      }
    });
}
  acceptRequest() {
  if (!this.pendingRequestId) return;

  this.connectionService.respond(this.pendingRequestId, 'ACCEPTED')
    .subscribe({
      next: () => {
        this.connectionStatus = 'CONNECTED';
      },
      error: (err) => console.error(err)
    });
}
rejectRequest() {
  if (!this.pendingRequestId) return;

  this.connectionService.respond(this.pendingRequestId, 'REJECTED')
    .subscribe({
      next: () => {
        this.connectionStatus = 'NONE';
      },
      error: (err) => console.error(err)
    });
}
removeConnection() {
  if (!this.user?.id) return;

  this.connectionService.removeConnection(this.user.id)
    .subscribe({
      next: () => {
        this.connectionStatus = 'NONE';
      },
      error: (err) => console.error(err)
    });
}

} 