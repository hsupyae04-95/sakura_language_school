import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, finalize } from 'rxjs';
import { ApiService } from './api.service';
import {
  AdminStudentUpdateRequest,
  AdminTeacherUpdateRequest,
  Batch,
  BatchRequest,
  Course,
  CourseRequest,
  Enrollment,
  StudentSummary,
  TeacherSummary,
  UserSummary,
  UserUpdateRequest,
} from './models';

type DetailType = 'user' | 'student' | 'teacher' | 'course' | 'batch' | 'enrollment';
type DetailData = UserSummary | StudentSummary | TeacherSummary | Course | Batch | Enrollment;

@Component({
  selector: 'app-admin-detail',
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="dashboard-shell">
      <header class="dashboard-topbar">
        <a class="brand" routerLink="/"><span class="brand-mark">✿</span><span class="brand-copy"><strong>Sakura</strong><span>Japanese Language School</span></span></a>
        <div class="dashboard-actions"><a class="text-button" [routerLink]="backPath">← Back to list</a></div>
      </header>

      <main class="dashboard-main admin-detail-page">
        @if (error()) { <div class="form-error" role="alert">{{ error() }}</div> }
        @if (!detail()) {
          <div class="loading-panel">Loading details…</div>
        } @else {
          <div class="dashboard-heading"><h1>{{ detailTitle() }}</h1><button class="button button-primary" type="button" (click)="editing.set(!editing())" [disabled]="detailType === 'enrollment'">{{ editing() ? 'Close form' : 'Update' }}</button></div>

          @if (editing()) {
            <section class="panel detail-form-panel">
              @if (detailType === 'user') {
                <form class="management-form" (ngSubmit)="saveUser()"><div class="form-grid"><label>Username<input name="username" [(ngModel)]="userForm.username" required /></label><label>Email<input name="email" [(ngModel)]="userForm.email" type="email" required /></label></div><button class="button button-primary small-button" [disabled]="saving()">{{ saving() ? 'Saving…' : 'Save changes' }}</button></form>
              }
              @if (detailType === 'student') {
                <form class="management-form" (ngSubmit)="saveStudent()"><div class="form-grid"><label>Full name<input name="studentFullName" [(ngModel)]="studentForm.fullName" required /></label><label>Username<input name="studentUsername" [(ngModel)]="studentForm.username" required /></label></div><div class="form-grid"><label>Email<input name="studentEmail" [(ngModel)]="studentForm.email" type="email" required /></label><label>New password <span class="optional">optional</span><input name="studentPassword" [(ngModel)]="studentForm.password" type="password" minlength="8" /></label></div><div class="form-grid"><label>Phone<input name="studentPhone" [(ngModel)]="studentForm.phone" /></label><label>Address<input name="studentAddress" [(ngModel)]="studentForm.address" /></label></div><label class="checkbox-field"><input name="studentEnabled" [(ngModel)]="studentForm.enabled" type="checkbox" /> Account active</label><button class="button button-primary small-button" [disabled]="saving()">{{ saving() ? 'Saving…' : 'Save changes' }}</button></form>
              }
              @if (detailType === 'teacher') {
                <form class="management-form" (ngSubmit)="saveTeacher()"><div class="form-grid"><label>Full name<input name="teacherFullName" [(ngModel)]="teacherForm.fullName" required /></label><label>Username<input name="teacherUsername" [(ngModel)]="teacherForm.username" required /></label></div><div class="form-grid"><label>Email<input name="teacherEmail" [(ngModel)]="teacherForm.email" type="email" required /></label><label>New password <span class="optional">optional</span><input name="teacherPassword" [(ngModel)]="teacherForm.password" type="password" minlength="8" /></label></div><div class="form-grid"><label>Phone<input name="teacherPhone" [(ngModel)]="teacherForm.phone" /></label><label>Specialization<input name="specialization" [(ngModel)]="teacherForm.specialization" /></label></div><label>Qualification<input name="qualification" [(ngModel)]="teacherForm.qualification" /></label><label class="checkbox-field"><input name="teacherActive" [(ngModel)]="teacherForm.active" type="checkbox" /> Teacher active</label><button class="button button-primary small-button" [disabled]="saving()">{{ saving() ? 'Saving…' : 'Save changes' }}</button></form>
              }
              @if (detailType === 'course') {
                <form class="management-form" (ngSubmit)="saveCourse()"><div class="form-grid"><label>Name<input name="courseName" [(ngModel)]="courseForm.name" required /></label><label>Level<input name="courseLevel" [(ngModel)]="courseForm.level" required /></label></div><div class="form-grid"><label>Duration in months<input name="courseMonths" [(ngModel)]="courseForm.durationMonths" type="number" min="1" required /></label><label>Fee<input name="courseFee" [(ngModel)]="courseForm.fee" type="number" min="0" step="0.01" required /></label></div><label>Description<textarea name="courseDescription" [(ngModel)]="courseForm.description" rows="4"></textarea></label><button class="button button-primary small-button" [disabled]="saving()">{{ saving() ? 'Saving…' : 'Save changes' }}</button></form>
              }
              @if (detailType === 'batch') {
                <form class="management-form" (ngSubmit)="saveBatch()"><div class="form-grid"><label>Course<select name="batchCourse" [(ngModel)]="batchForm.courseId" required>@for (course of courses(); track course.id) { <option [ngValue]="course.id">{{ course.name }}</option> }</select></label><label>Teacher<select name="batchTeacher" [(ngModel)]="batchForm.teacherId"><option [ngValue]="undefined">Unassigned</option>@for (teacher of teachers(); track teacher.id) { <option [ngValue]="teacher.id">{{ teacher.fullName }}</option> }</select></label></div><div class="form-grid"><label>Batch name<input name="batchName" [(ngModel)]="batchForm.batchName" required /></label><label>Schedule<input name="schedule" [(ngModel)]="batchForm.schedule" required /></label></div><div class="form-grid"><label>Class type<select name="classType" [(ngModel)]="batchForm.classType"><option value="ONLINE">Online</option><option value="IN_PERSON">In person</option></select></label><label>Capacity<input name="capacity" [(ngModel)]="batchForm.capacity" type="number" min="1" required /></label></div><div class="form-grid"><label>Starts<input name="startDate" [(ngModel)]="batchForm.startDate" type="date" required /></label><label>Ends<input name="endDate" [(ngModel)]="batchForm.endDate" type="date" required /></label></div><button class="button button-primary small-button" [disabled]="saving()">{{ saving() ? 'Saving…' : 'Save changes' }}</button></form>
              }
            </section>
          }

          <section class="panel detail-record">
            @if (detailType === 'user') { <div class="detail-kv"><span>Username</span><strong>{{ detail()?.username }}</strong><span>Email</span><strong>{{ detail()?.email }}</strong><span>Status</span><strong>{{ detail()?.enabled ? 'Active' : 'Inactive' }}</strong></div> }
            @if (detailType === 'student') { <div class="detail-kv"><span>Full name</span><strong>{{ detail()?.fullName }}</strong><span>Username</span><strong>{{ detail()?.username }}</strong><span>Email</span><strong>{{ detail()?.email }}</strong><span>Phone</span><strong>{{ detail()?.phone || '—' }}</strong><span>Address</span><strong>{{ detail()?.address || '—' }}</strong><span>Status</span><strong>{{ detail()?.enabled ? 'Active' : 'Inactive' }}</strong></div> }
            @if (detailType === 'teacher') { <div class="detail-kv"><span>Full name</span><strong>{{ detail()?.fullName }}</strong><span>Username</span><strong>{{ detail()?.username }}</strong><span>Email</span><strong>{{ detail()?.email }}</strong><span>Phone</span><strong>{{ detail()?.phone || '—' }}</strong><span>Specialization</span><strong>{{ detail()?.specialization || '—' }}</strong><span>Qualification</span><strong>{{ detail()?.qualification || '—' }}</strong><span>Status</span><strong>{{ detail()?.active ? 'Active' : 'Inactive' }}</strong></div> }
            @if (detailType === 'course') { <div class="detail-kv"><span>Name</span><strong>{{ detail()?.name }}</strong><span>Level</span><strong>{{ detail()?.level }}</strong><span>Duration</span><strong>{{ detail()?.durationMonths }} months</strong><span>Fee</span><strong>{{ detail()?.fee | number:'1.0-2' }}</strong><span>Description</span><strong>{{ detail()?.description || '—' }}</strong><span>Status</span><strong>{{ detail()?.active ? 'Active' : 'Inactive' }}</strong></div> }
            @if (detailType === 'batch') { <div class="detail-kv"><span>Batch</span><strong>{{ detail()?.batchName }}</strong><span>Course</span><strong>{{ detail()?.courseName }}</strong><span>Teacher</span><strong>{{ detail()?.teacherName || 'Unassigned' }}</strong><span>Schedule</span><strong>{{ detail()?.schedule }}</strong><span>Dates</span><strong>{{ detail()?.startDate | date:'mediumDate' }} – {{ detail()?.endDate | date:'mediumDate' }}</strong><span>Capacity</span><strong>{{ detail()?.enrolledCount || 0 }} / {{ detail()?.capacity }}</strong><span>Status</span><strong>{{ detail()?.status }}</strong></div> }
            @if (detailType === 'enrollment') { <div class="detail-kv"><span>Student</span><strong>{{ detail()?.studentName }}</strong><span>Course</span><strong>{{ detail()?.courseName }}</strong><span>Batch</span><strong>{{ detail()?.batchName }}</strong><span>Applied</span><strong>{{ detail()?.appliedAt | date:'mediumDate' }}</strong><span>Status</span><strong>{{ detail()?.status }}</strong><span>Admin note</span><strong>{{ detail()?.adminNote || '—' }}</strong></div><div class="detail-actions">@if (detail()?.status === 'PENDING') { <button class="approve-button" type="button" (click)="review('approve')">Approve</button><button class="reject-button" type="button" (click)="review('reject')">Reject</button> }</div> }
          </section>
        }
      </main>
    </div>
  `,
})
export class AdminDetailComponent {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly detailType = this.route.snapshot.data['detailType'] as DetailType;
  readonly detail = signal<any>(null);
  readonly error = signal('');
  readonly editing = signal(false);
  readonly saving = signal(false);
  readonly courses = signal<Course[]>([]);
  readonly teachers = signal<TeacherSummary[]>([]);
  readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly backPath = `/admin/${this.detailType === 'enrollment' ? 'applications' : this.detailType === 'batch' ? 'batches' : `${this.detailType}s`}`;

  userForm: UserUpdateRequest = { username: '', email: '' };
  studentForm: AdminStudentUpdateRequest = { username: '', email: '', fullName: '', password: '', phone: '', address: '', enabled: true };
  teacherForm: AdminTeacherUpdateRequest = { username: '', email: '', fullName: '', password: '', phone: '', specialization: '', qualification: '', active: true };
  courseForm: CourseRequest = { name: '', level: '', description: '', durationMonths: 1, fee: 0 };
  batchForm: BatchRequest = { courseId: 0, teacherId: undefined, batchName: '', classType: 'ONLINE', startDate: '', endDate: '', schedule: '', capacity: 1 };

  constructor() {
    this.load();
    if (this.detailType === 'batch') {
      this.api.courses().subscribe({ next: (items) => this.courses.set(items) });
      this.api.adminTeachers().subscribe({ next: (items) => this.teachers.set(items) });
    }
  }

  detailTitle(): string { const item = this.detail() as any; return item?.fullName || item?.name || item?.batchName || item?.courseName || item?.username || `${this.detailType} details`; }

  saveUser(): void { this.submit(this.api.updateAdminUser(this.id, this.userForm)); }
  saveStudent(): void { const request = { ...this.studentForm }; if (!request.password?.trim()) delete request.password; this.submit(this.api.updateAdminStudent(this.id, request)); }
  saveTeacher(): void { const request = { ...this.teacherForm }; if (!request.password?.trim()) delete request.password; this.submit(this.api.updateAdminTeacher(this.id, request)); }
  saveCourse(): void { this.submit(this.api.updateCourse(this.id, this.courseForm)); }
  saveBatch(): void { this.submit(this.api.updateBatch(this.id, this.batchForm)); }

  review(decision: 'approve' | 'reject'): void {
    this.api.reviewEnrollment(this.id, decision).subscribe({
      next: (updated) => this.detail.set(updated),
      error: (error) => this.handleError(error, 'The enrollment could not be reviewed.'),
    });
  }

  private load(): void {
    let request: Observable<any>;
    switch (this.detailType) {
      case 'user': request = this.api.adminUser(this.id); break;
      case 'student': request = this.api.adminStudent(this.id); break;
      case 'teacher': request = this.api.adminTeacher(this.id); break;
      case 'course': request = this.api.adminCourse(this.id); break;
      case 'batch': request = this.api.adminBatch(this.id); break;
      case 'enrollment': request = this.api.adminEnrollment(this.id); break;
    }
    request.subscribe({ next: (item) => { this.detail.set(item); this.fillForm(item); }, error: (error) => this.handleError(error, 'The record could not be loaded.') });
  }

  private fillForm(item: DetailData): void {
    const value = item as any;
    this.userForm = { username: value.username ?? '', email: value.email ?? '' };
    this.studentForm = { username: value.username ?? '', email: value.email ?? '', fullName: value.fullName ?? '', password: '', phone: value.phone ?? '', address: value.address ?? '', enabled: value.enabled ?? true };
    this.teacherForm = { username: value.username ?? '', email: value.email ?? '', fullName: value.fullName ?? '', password: '', phone: value.phone ?? '', specialization: value.specialization ?? '', qualification: value.qualification ?? '', active: value.active ?? true };
    this.courseForm = { name: value.name ?? '', level: value.level ?? '', description: value.description ?? '', durationMonths: value.durationMonths ?? 1, fee: value.fee ?? 0 };
    this.batchForm = { courseId: value.courseId ?? 0, teacherId: value.teacherId, batchName: value.batchName ?? '', classType: value.classType ?? 'ONLINE', startDate: value.startDate ?? '', endDate: value.endDate ?? '', schedule: value.schedule ?? '', capacity: value.capacity ?? 1 };
  }

  private submit(request: Observable<any>): void {
    this.saving.set(true);
    request.pipe(finalize(() => this.saving.set(false))).subscribe({ next: (item) => { this.detail.set(item); this.fillForm(item); this.editing.set(false); }, error: (error) => this.handleError(error, 'The record could not be updated.') });
  }

  private handleError(error: { error?: { message?: string } }, fallback: string): void { this.error.set(error?.error?.message ?? fallback); }
}
