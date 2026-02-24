import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ConnectionService } from '../../core/connection.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
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

  connectionStatus: string = 'NONE';
  pendingRequestId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private connectionService: ConnectionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.http.get<any>('http://localhost:8080/api/users/me', {
      withCredentials: true
    }).subscribe(me => {
      this.currentUser = me;
      this.cdr.markForCheck();

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
      this.cdr.markForCheck();
    });
  }

  loadPosts(userId: number) {
    this.http.get<any[]>(
      `http://localhost:8080/api/posts/user/${userId}`,
      { withCredentials: true }
    ).subscribe(posts => {
      this.posts = posts;
      this.cdr.markForCheck();
    });
  }

  /* ========================= */
  /* FOLLOW */
  /* ========================= */

  loadFollowData() {
    if (!this.user?.id) return;

    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/followers/count`,
      { withCredentials: true }
    ).subscribe(count => {
      this.followersCount = count;
      this.cdr.markForCheck();
    });

    this.http.get<number>(
      `http://localhost:8080/api/follow/${this.user.id}/following/count`,
      { withCredentials: true }
    ).subscribe(count => {
      this.followingCount = count;
      this.cdr.markForCheck();
    });

    if (!this.isOwnProfile()) {
      this.http.get<boolean>(
        `http://localhost:8080/api/follow/${this.user.id}/is-following`,
        { withCredentials: true }
      ).subscribe(status => {
        this.isFollowing = status;
        this.cdr.markForCheck();
      });
    }
  }

  followUser() {
    if (!this.user?.id) return;

    this.http.post(
      `http://localhost:8080/api/follow/${this.user.id}`,
      {},
      { withCredentials: true }
    ).subscribe(() => {
      this.loadFollowData();
    });
  }

  unfollowUser() {
    if (!this.user?.id) return;

    this.http.delete(
      `http://localhost:8080/api/follow/${this.user.id}`,
      { withCredentials: true }
    ).subscribe(() => {
      this.loadFollowData();
    });
  }

  /* ========================= */
  /* CONNECTION */
  /* ========================= */

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
        this.cdr.markForCheck();
      });
  }

  sendConnectionRequest() {
    if (!this.user?.id) return;

    this.connectionService.sendRequest(this.user.id)
      .subscribe(() => {
        this.loadConnectionStatus();
      });
  }

  acceptRequest() {
    if (!this.pendingRequestId) return;

    this.connectionService.respond(this.pendingRequestId, 'ACCEPTED')
      .subscribe(() => {
        this.loadConnectionStatus();
      });
  }

  rejectRequest() {
    if (!this.pendingRequestId) return;

    this.connectionService.respond(this.pendingRequestId, 'REJECTED')
      .subscribe(() => {
        this.loadConnectionStatus();
      });
  }

  removeConnection() {
    if (!this.user?.id) return;

    this.connectionService.removeConnection(this.user.id)
      .subscribe(() => {
        this.loadConnectionStatus();
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
      this.cdr.markForCheck();
    });
  }

  isOwnProfile(): boolean {
    return this.currentUser &&
           this.user &&
           this.currentUser.id === this.user.id;
  }
}