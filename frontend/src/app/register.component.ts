import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { ApiService } from './api.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="auth-page">
      <a class="auth-back" routerLink="/">← Back to Sakura</a>
      <section class="auth-card register-card">
        <div class="auth-brand"><span>✿</span><strong>Sakura</strong></div>
        <p class="section-kicker">Start gently</p>
        <h1>Make room for<br /><em>something new.</em></h1>
        <p class="auth-intro">Create your learner account and find a class that fits your rhythm.</p>
        @if (error()) { <div class="form-error" role="alert">{{ error() }}</div> }
        @if (success()) { <div class="form-success" role="status">{{ success() }}</div> }
        <form (ngSubmit)="submit()">
          <div class="form-grid">
            <label>Full name<input name="fullName" [(ngModel)]="form.fullName" required /></label>
            <label>Username<input name="username" [(ngModel)]="form.username" autocomplete="username" required /></label>
          </div>
          <label>Email<input name="email" [(ngModel)]="form.email" type="email" autocomplete="email" required /></label>
          <label>Password<input name="password" [(ngModel)]="form.password" type="password" autocomplete="new-password" minlength="8" required /></label>
          <div class="form-grid">
            <label>Phone <span class="optional">optional</span><input name="phone" [(ngModel)]="form.phone" /></label>
            <label>Address <span class="optional">optional</span><input name="address" [(ngModel)]="form.address" /></label>
          </div>
          <button class="button button-primary auth-submit" [disabled]="loading()">{{ loading() ? 'Creating account…' : 'Create learner account' }} <span>→</span></button>
        </form>
        <p class="auth-switch">Already have an account? <a routerLink="/login">Sign in</a></p>
      </section>
    </main>
  `,
})
export class RegisterComponent {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  form = { username: '', email: '', password: '', fullName: '', phone: '', address: '' };

  submit(): void {
    this.error.set('');
    this.success.set('');
    this.loading.set(true);
    this.api.register(this.form).subscribe({
      next: () => {
        this.success.set('Your account is ready. Signing you in…');
        this.auth.login(this.form.username, this.form.password).subscribe({
          next: () => void this.router.navigateByUrl(this.auth.dashboardPath()),
          error: () => void this.router.navigateByUrl('/login'),
        });
      },
      error: (error) => {
        this.error.set(error?.error?.message ?? 'We could not create your account. Please review the details.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }
}

