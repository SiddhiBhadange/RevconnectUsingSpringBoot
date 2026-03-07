import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ConnectionService } from '../../core/connection.service';
import { AuthService } from '../../features/auth/auth.service';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';


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
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    changeDetection: ChangeDetectionStrategy.OnPush
  }



ngOnInit(): void {

  this.http.get<any>('http://localhost:8080/api/users/me')
    .subscribe(me => {
      this.currentUser = me;

      const idFromUrl = this.route.snapshot.paramMap.get('id');

      // 🔥 If no ID in URL, load own profile
      if (!idFromUrl) {
        this.loadUserById(me.id);
        
      }
    });

  this.route.paramMap
    .pipe(
      map(params => params.get('id')),
      distinctUntilChanged()
    )
    .subscribe(id => {
      if (id) {
        this.loadUserById(id);
        
      }
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
  console.log(" loadUserById called");

  this.http.get<any>(`http://localhost:8080/api/users/${id}`)
    .subscribe(user => {
      console.log(" user reloaded from backend");
      this.user = user;
      this.loadPosts(user.id);
      this.loadFollowData();
      this.loadConnectionStatus();
      this.selectedRole = user.roles?.[0] || 'PERSONAL';
    });
}

  loadPosts(userId: number) {
    this.http.get<any[]>(`http://localhost:8080/api/posts/user/${userId}`)
      .subscribe(posts => this.posts = posts);
  }

  /* FOLLOW */

  loadFollowData() {
    if (!this.user?.id) return;

    this.http.get<number>(`http://localhost:8080/api/follow/${this.user.id}/followers/count`)
      .subscribe(count => this.followersCount = count);

    this.http.get<number>(`http://localhost:8080/api/follow/${this.user.id}/following/count`)
      .subscribe(count => this.followingCount = count);

    if (!this.isOwnProfile()) {
      this.http.get<boolean>(`http://localhost:8080/api/follow/${this.user.id}/is-following`)
        .subscribe(status => this.isFollowing = status);
    }
  }

followUser() {
  this.isFollowing = true;
  this.followersCount++;

  this.http.post(
    `http://localhost:8080/api/follow/${this.user.id}`,

    {},
    { responseType: 'text' }
  ).subscribe(() => {
       this.isFollowing = true;
      this.followersCount--;
      this.cdr.detectChanges(); 
    });
}

unfollowUser() {
  this.isFollowing = false;

  if (this.followersCount > 0) {
    this.followersCount--;
  }

  this.http.delete(
    `http://localhost:8080/api/follow/${this.user.id}`,{responseType: 'text'}
  ).subscribe( () => {
      this.isFollowing = false;
      this.followersCount++;
      this.cdr.detectChanges();
    });
}
  /* CONNECTION */

loadConnectionStatus() {
  if (this.isOwnProfile() || !this.user?.id) return;

  this.connectionService.getStatus(this.user.id)
    .subscribe((res: any) => {

      if (!res || res.status === 'NONE') {
        this.connectionStatus = 'NONE';
        this.pendingRequestId = null;
        return;
      }

      this.connectionStatus = res.status;

      if (res.status === 'PENDING_RECEIVED') {
        this.pendingRequestId = res.id;
      } else {
        this.pendingRequestId = null;
      }

      this.cdr.detectChanges();
    });
}

acceptRequest() {
  if (!this.pendingRequestId) return;

  this.connectionService.respond(this.pendingRequestId, 'ACCEPTED')
    .subscribe(() => {
      this.connectionStatus = 'CONNECTED';
      this.pendingRequestId = null;
      this.cdr.detectChanges();
    });
}
rejectRequest() {
  if (!this.pendingRequestId) return;

  this.connectionService.respond(this.pendingRequestId, 'REJECTED')
    .subscribe(() => {
      this.connectionStatus = 'NONE';
      this.pendingRequestId = null;
      this.cdr.detectChanges();
    });
}

sendConnectionRequest() {
  this.connectionStatus = 'PENDING_SENT';

  this.connectionService.sendRequest(this.user.id)
    .subscribe({
      error: () => {
        this.connectionStatus = 'NONE';
      }
    });
}

removeConnection() {
  this.connectionService.removeConnection(this.user.id)
    .subscribe(() => {
      this.connectionStatus = 'NONE';
      this.cdr.detectChanges();
    });
}

  /* PROFILE */

  saveProfile() {

  // THIS LINE WAS MISSING
  this.user.roles = [this.selectedRole];

  this.http.put(
    'http://localhost:8080/api/users/me',
    this.user,
  ).subscribe({
    next: (updatedUser) => {
      console.log("Updated:", updatedUser);
      this.user = updatedUser;
      this.activeTab = 'about';
      this.cdr.detectChanges(); // Trigger change detection
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

    this.http.delete('http://localhost:8080/api/users/me')
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
    this.http.get<any[]>(`http://localhost:8080/api/follow/followers/${this.user.id}`)
      .subscribe(data => {
        this.followers = data;
        this.cdr.detectChanges(); // Trigger change detection
      });
  }

  loadFollowing() {
    this.http.get<any[]>(`http://localhost:8080/api/follow/following/${this.user.id}`)
      .subscribe(data => {
        this.following = data;
        this.cdr.detectChanges(); // Trigger change detection
      });
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
followUserFromList(userId: number) {
  this.http.post(
    `http://localhost:8080/api/follow/${userId}`,
    {},
    { responseType: 'text' }
  ).subscribe(() => {

    const user =
      this.followers.find(f => f.id === userId) ||
      this.following.find(f => f.id === userId);

    if (user) user.isFollowing = true;
  });
}

unfollowUserFromList(userId: number) {
  this.http.delete(
    `http://localhost:8080/api/follow/${userId}`,
    { responseType: 'text' }
  ).subscribe(() => {

    const user =
      this.followers.find(f => f.id === userId) ||
      this.following.find(f => f.id === userId);

    if (user) user.isFollowing = false;
  });
}
}