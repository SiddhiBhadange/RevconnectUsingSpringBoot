import { Component, OnInit, OnDestroy } from '@angular/core';
import { ConnectionService } from '../../core/connection.service';
import { AuthService } from '../auth/auth.service';
import { Subscription, timer, forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html'
})
export class ConnectionsComponent implements OnInit, OnDestroy {
  searchQuery = '';
  pendingRequests: any[] = [];
  connections: any[] = [];
  isLoading = true;
  currentUserId: number | null = null;
  errorMessage = '';

  private pollingSubscription?: Subscription;

  constructor(
    private connectionService: ConnectionService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe((user: any) => {
      if (user) {
        this.currentUserId = user.id;
      }
    });

    this.startPolling();
  }

  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  startPolling() {
    this.pollingSubscription = timer(0, 15000).pipe(
      switchMap(() => forkJoin({
        pending: this.connectionService.getPending(),
        accepted: this.connectionService.getConnections()
      }).pipe(
        catchError(error => {
          console.error('Error fetching connections', error);
          this.isLoading = false;
          this.errorMessage = 'Failed to load connections.';
          return of({ pending: [], accepted: [] });
        })
      ))
    ).subscribe((res: any) => {
      this.pendingRequests = res.pending;
      this.connections = res.accepted;
      this.isLoading = false;
    });
  }

  loadPending() {
    this.connectionService.getPending().subscribe((res: any) => {
      this.pendingRequests = res;
    });
  }

  accept(id: number) {
    this.connectionService.respond(id, 'ACCEPTED')
      .subscribe(() => this.loadPending());
  }

  reject(id: number) {
    this.connectionService.respond(id, 'REJECTED')
      .subscribe(() => this.loadPending());
  }

}
