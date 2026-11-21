import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="auth-page">
      <a class="auth-back" routerLink="/">← Back to Sakura</a>
      <section class="auth-card">
        <div class="auth-brand"><span>✿</span><strong>Sakura</strong></div>
        <p class="section-kicker">Welcome back</p>
        <h1>Continue your<br /><em>Japanese journey.</em></h1>
        <p class="auth-intro">Sign in to view your classes, applications, and learning path.</p>
        @if (error()) { <div class="form-error" role="alert">{{ error() }}</div> }
        <form (ngSubmit)="submit()">
          <label>Username<input name="username" [(ngModel)]="username" autocomplete="username" required /></label>
          <label>Password<input name="password" [(ngModel)]="password" type="password" autocomplete="current-password" required /></label>
          <button class="button button-primary auth-submit" [disabled]="loading()">{{ loading() ? 'Signing in…' : 'Sign in' }} <span>→</span></button>
        </form>
        <p class="auth-switch">New to Sakura? <a routerLink="/register">Create an account</a></p>
      </section>
    </main>
  `,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  username = '';
  password = '';
  readonly loading = signal(false);
  readonly error = signal('');

  submit(): void {
    this.error.set('');
    this.loading.set(true);
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: () => void this.router.navigateByUrl(this.auth.dashboardPath()),
      error: (error) => {
        this.error.set(error?.error?.message ?? 'We could not sign you in. Check your username and password.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }
}

