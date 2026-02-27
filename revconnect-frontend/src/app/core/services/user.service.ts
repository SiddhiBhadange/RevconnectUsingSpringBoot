import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private API_URL = 'http://localhost:8080/api/users';

    constructor(private http: HttpClient) { }

    searchUsers(query: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.API_URL}/search-users?query=${encodeURIComponent(query)}`);
    }
}
