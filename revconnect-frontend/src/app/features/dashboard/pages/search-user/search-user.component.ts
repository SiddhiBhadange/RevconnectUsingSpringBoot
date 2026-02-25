import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent {

  username: string = '';
  searchResults: any[] = [];
  searched: boolean = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  searchUsers() {

    const query = this.username.trim();

    //  Prevent single alphabet search
    if (!query || query.length < 2) {
      this.searchResults = [];
      this.searched = false;
      return;
    }

   this.http.get<any[]>(
  `http://localhost:8080/api/users/search?keyword=${query}`,
  { withCredentials: true }
).subscribe({
      next: (res) => {
        this.searchResults = Array.isArray(res) ? res : [res];
        this.searched = true;
      },
      error: () => {
        this.searchResults = [];
        this.searched = true;
      }
    });
  }

  goToProfile(userId: number) {
    this.router.navigate(['/profile', userId]);
  }
}