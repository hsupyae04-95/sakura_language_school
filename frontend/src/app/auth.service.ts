import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';
import { ApiService } from './api.service';
import { CurrentUser, Role } from './models';

const TOKEN_KEY = 'sakura_access_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly user = signal<CurrentUser | null>(null);
  readonly isAuthenticated = computed(() => Boolean(this.token()));
  readonly isLoadingUser = signal(false);

  restoreSession(): void {
    if (!this.token() || this.user() || this.isLoadingUser()) return;
    this.isLoadingUser.set(true);
    this.api.currentUser().pipe(
      tap((user) => this.user.set(user)),
      catchError(() => {
        this.clearSession();
        return of(null);
      }),
      finalize(() => this.isLoadingUser.set(false)),
    ).subscribe();
  }

  login(username: string, password: string) {
    return this.api.login(username, password).pipe(
      tap((response) => {
        localStorage.setItem(TOKEN_KEY, response.accessToken);
        this.token.set(response.accessToken);
      }),
      switchMap(() => this.api.currentUser()),
      tap((user) => this.user.set(user)),
    );
  }

  logout(): void {
    this.clearSession();
    void this.router.navigateByUrl('/');
  }

  hasRole(role: Role): boolean {
    return this.user()?.roles.includes(role) ?? false;
  }

  dashboardPath(): string {
    const role = this.user()?.roles[0];
    return role === 'ADMIN' ? '/admin/dashboard' : role === 'TEACHER' ? '/teacher/dashboard' : '/student/dashboard';
  }

  getAccessToken(): string | null {
    return this.token();
  }

  clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
    this.user.set(null);
  }
}

