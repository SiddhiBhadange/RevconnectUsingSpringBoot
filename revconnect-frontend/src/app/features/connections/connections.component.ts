import { Component, OnInit } from '@angular/core';
import { ConnectionService } from '../../core/connection.service';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html'
})
export class ConnectionsComponent implements OnInit {

  pendingRequests: any[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';


  constructor(private connectionService: ConnectionService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending() {
    this.connectionService.getPending()
      .subscribe(res => this.pendingRequests = res);
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