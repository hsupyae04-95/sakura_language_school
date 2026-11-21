import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { Batch, Course } from './models';

@Component({
  selector: 'app-course-detail',
  imports: [CommonModule, RouterLink],
  template: `
    <main class="detail-page">
      <header class="detail-topbar"><a class="brand" routerLink="/"><span class="brand-mark">✿</span><span class="brand-copy"><strong>Sakura</strong><span>Japanese Language School</span></span></a></header>
      @if (loading()) { <div class="loading-panel detail-loading">Loading course details…</div> }
      @else if (error()) { <div class="form-error detail-message">{{ error() }}</div> }
      @else if (course()) {
        <section class="detail-hero"><div><p class="section-kicker">{{ course()?.level }} course</p><h1>{{ course()?.name }}</h1><p class="detail-description">{{ course()?.description }}</p><div class="detail-meta"><span>{{ course()?.durationMonths }} months</span><span>Tuition {{ course()?.fee | number:'1.0-2' }}</span></div></div><div class="detail-seal">日本語<span>さくら</span></div></section>
        <section class="detail-content"><div><p class="section-kicker">Choose your rhythm</p><h2>Available batches</h2><div class="detail-batches">@for (batch of batches(); track batch.id) { <article class="detail-batch" [class.is-unavailable]="batch.available === false"><div><span class="batch-status">{{ batch.available === false ? 'UNAVAILABLE' : batch.status }}</span><h3>{{ batch.batchName }}</h3><p>{{ batch.teacherName || 'Teacher to be assigned' }}</p></div><dl><div><dt>Dates</dt><dd>{{ batch.startDate | date:'mediumDate' }} – {{ batch.endDate | date:'mediumDate' }}</dd></div><div><dt>Schedule</dt><dd>{{ batch.schedule }}</dd></div><div><dt>Class type</dt><dd>{{ batch.classType === 'IN_PERSON' ? 'In person' : 'Online' }}</dd></div></dl>@if (batch.available === false) { <span class="unavailable-copy">This batch is full</span> } @else { <a class="button button-primary small-button" [routerLink]="applyPath()">{{ applyLabel() }} <span>→</span></a> }</article> } @empty { <div class="empty-panel">No batches are currently available for this course.</div> }</div></div><aside class="detail-aside"><p class="section-kicker">A thoughtful start</p><h2>Learn at a pace that lasts.</h2><p>Every Sakura course pairs practical lessons with a welcoming class community. Choose a batch, then take the next step from your dashboard.</p><a class="inline-link" routerLink="/register">Create a learner account <span>→</span></a></aside></section>
      }
    </main>
  `,
})
export class CourseDetailComponent {
  readonly auth = inject(AuthService);
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  readonly course = signal<Course | null>(null);
  readonly batches = signal<Batch[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  constructor() {
    this.auth.restoreSession();
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.course(id).subscribe({ next: (course) => this.course.set(course), error: (error) => this.error.set(error?.error?.message ?? 'This course could not be loaded.'), complete: () => this.loading.set(false) });
    this.api.batches().subscribe({ next: (batches) => this.batches.set(batches.filter((batch) => batch.courseId === id && (batch.status === 'OPEN' || batch.available === false))), error: () => undefined });
  }

  applyPath(): string { return this.auth.isAuthenticated() ? this.auth.dashboardPath() : '/login'; }
  applyLabel(): string { return this.auth.isAuthenticated() ? (this.auth.hasRole('STUDENT') ? 'Apply from dashboard' : 'Open dashboard') : 'Sign in to apply'; }
}
