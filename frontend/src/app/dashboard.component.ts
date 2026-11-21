import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { Observable, filter, finalize } from 'rxjs';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { AdminStudentUpdateRequest, Batch, BatchRequest, Course, CourseRequest, Enrollment, Role, StudentSummary, TeacherBatchStudent, TeacherRequest, TeacherSummary, UserSummary } from './models';

type DashboardPage = 'overview' | 'catalog' | 'applications' | 'classes' | 'courses' | 'batches' | 'teachers' | 'users' | 'students';
type NavItem = { label: string; path: string };

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="dashboard-shell">
      <header class="dashboard-topbar">
        <a class="brand" routerLink="/"><span class="brand-mark">✿</span><span class="brand-copy"><strong>Sakura</strong><span>Japanese Language School</span></span></a>
        <nav class="dashboard-nav" aria-label="Dashboard navigation">
          @for (item of navItems(); track item.path) { <a [routerLink]="item.path" [class.active]="currentPath() === item.path">{{ item.label }}</a> }
        </nav>
        <div class="dashboard-actions"><span class="user-chip"><span class="user-avatar">{{ initials() }}</span><span>{{ auth.user()?.username || 'Loading…' }}</span></span><button class="text-button" type="button" (click)="auth.logout()">Sign out</button></div>
      </header>

      <main class="dashboard-main">
        @if (error()) { <div class="form-error" role="alert">{{ error() }}</div> }
        @if (auth.isLoadingUser()) { <div class="loading-panel">Loading your Sakura space…</div> }
        @else if (!auth.user()) { <div class="loading-panel">We could not load your profile. Please sign in again.</div> }
        @else {
          <div class="dashboard-heading"><h1>{{ pageHeading() }}</h1></div>

          @if (role() === 'STUDENT') {
            @if (page() === 'overview') {
              <section class="metric-grid"><div class="metric-card"><span>Open batches</span><strong>{{ availableBatches().length }}</strong><small>Ready for applications</small></div><div class="metric-card"><span>Applications</span><strong>{{ enrollments().length }}</strong><small>Your submitted applications</small></div><div class="metric-card"><span>Enrolled classes</span><strong>{{ currentEnrollments().length }}</strong><small>Approved learning path</small></div></section>
              <section class="dashboard-grid"><div class="panel"><div class="panel-heading"><div><p class="section-kicker">Next step</p><h2>Find your next class</h2></div></div><p class="panel-copy">Browse the open batches, choose a rhythm that works for you, and send an application in a few seconds.</p><a class="button button-primary" routerLink="/student/catalog">Browse batches <span>→</span></a></div><div class="panel"><div class="panel-heading"><div><p class="section-kicker">Your progress</p><h2>Keep going gently.</h2></div></div><p class="panel-copy">Approved classes and schedules live in My classes. Completed courses move into your course history automatically.</p><a class="inline-link" routerLink="/student/classes">View my classes <span>→</span></a></div></section>
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Latest activity</p><h2>Recent applications</h2></div><a class="inline-link" routerLink="/student/applications">View all <span>→</span></a></div><div class="enrollment-list">@for (enrollment of enrollments().slice(0, 3); track enrollment.id) { <article class="enrollment-row"><div><strong>{{ enrollment.courseName }}</strong><span>{{ enrollment.batchName }} · {{ enrollment.appliedAt | date:'mediumDate' }}</span></div><span [class]="'status-pill ' + enrollment.status.toLowerCase()">{{ enrollment.status }}</span></article> } @empty { <div class="empty-panel">You have not applied to a batch yet.</div> }</div></section>
            }
            @if (page() === 'catalog') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Find your rhythm</p><h2>Available class batches</h2></div><span class="count-badge">{{ availableBatches().length }}</span></div><div class="batch-grid">@for (batch of availableBatches(); track batch.id) { <article class="batch-card"><span class="batch-status">{{ batch.status }}</span><h3>{{ batch.batchName }}</h3><p>{{ batch.courseName }}</p><dl><div><dt>Starts</dt><dd>{{ batch.startDate | date:'mediumDate' }}</dd></div><div><dt>Schedule</dt><dd>{{ batch.schedule }}</dd></div><div><dt>Teacher</dt><dd>{{ batch.teacherName || 'To be assigned' }}</dd></div></dl><button class="button button-primary small-button" type="button" [disabled]="enrolling() === batch.id" (click)="enroll(batch)">{{ enrolling() === batch.id ? 'Applying…' : 'Apply for this batch' }} <span>→</span></button></article> } @empty { <div class="empty-panel">There are no open batches available right now.</div> }</div></section>
            }
            @if (page() === 'applications') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Your requests</p><h2>Enrollment applications</h2></div></div><div class="enrollment-list">@for (enrollment of enrollments(); track enrollment.id) { <article class="enrollment-row"><div><strong>{{ enrollment.courseName }}</strong><span>{{ enrollment.batchName }} · Applied {{ enrollment.appliedAt | date:'mediumDate' }}</span><small>{{ enrollment.adminNote || 'Waiting for the school team to review your application.' }}</small></div><span [class]="'status-pill ' + enrollment.status.toLowerCase()">{{ enrollment.status }}</span></article> } @empty { <div class="empty-panel">You have not applied to a batch yet. Browse available batches to get started.</div> }</div></section>
            }
            @if (page() === 'classes') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">After approval</p><h2>My enrolled classes</h2></div><span class="count-badge">{{ currentEnrollments().length }}</span></div>@if (currentEnrollments().length) { <div class="enrollment-list">@for (enrollment of currentEnrollments(); track enrollment.id) { <article class="class-schedule-card"><div><span class="batch-status">ENROLLED</span><h3>{{ enrollment.courseName }}</h3><p>{{ enrollment.batchName }}</p></div><dl><div><dt>Schedule</dt><dd>{{ batchFor(enrollment)?.schedule || 'Schedule available from your teacher' }}</dd></div><div><dt>Dates</dt><dd>{{ batchFor(enrollment)?.startDate | date:'mediumDate' }} – {{ batchFor(enrollment)?.endDate | date:'mediumDate' }}</dd></div><div><dt>Teacher</dt><dd>{{ batchFor(enrollment)?.teacherName || 'To be assigned' }}</dd></div></dl></article> }</div> } @else { <div class="empty-panel">No approved classes yet. Your approved classes and schedules will appear here.</div> }</section>
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Course history</p><h2>Completed courses</h2></div></div>@if (completedEnrollments().length) { <div class="compact-list">@for (enrollment of completedEnrollments(); track enrollment.id) { <div class="compact-row"><strong>{{ enrollment.courseName }}</strong><small>{{ enrollment.batchName }} · Completed</small></div> }</div> } @else { <div class="empty-panel">No completed courses yet.</div> }</section>
            }
          }

          @if (role() === 'ADMIN') {
            @if (page() === 'overview') {
              <section class="metric-grid"><div class="metric-card"><span>Applications</span><strong>{{ enrollments().length }}</strong><small>All learner applications</small></div><div class="metric-card"><span>Users</span><strong>{{ users().length }}</strong><small>Registered accounts</small></div><div class="metric-card"><span>Active courses</span><strong>{{ courses().length }}</strong><small>Public catalog courses</small></div></section><section class="dashboard-grid"><a class="action-card" routerLink="/admin/applications"><span class="action-icon">⌁</span><strong>Review applications</strong><small>Approve or reject student requests.</small><span class="inline-link">Open applications →</span></a><a class="action-card" routerLink="/admin/courses"><span class="action-icon">文</span><strong>Manage catalog</strong><small>Create and deactivate courses.</small><span class="inline-link">Open courses →</span></a><a class="action-card" routerLink="/admin/batches"><span class="action-icon">◷</span><strong>Manage classes</strong><small>Create batches and assign teachers.</small><span class="inline-link">Open batches →</span></a></section>
            }
            @if (page() === 'applications') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Needs your review</p><h2>Enrollment applications</h2></div></div><div class="table-wrap"><table><thead><tr><th>Learner</th><th>Course / batch</th><th>Applied</th><th>Status</th><th></th></tr></thead><tbody>@for (enrollment of enrollments(); track enrollment.id) { <tr class="clickable-row" (click)="openDetail('enrollment', enrollment.id)"><td><strong>{{ enrollment.studentName }}</strong></td><td>{{ enrollment.courseName }}<small>{{ enrollment.batchName }}</small></td><td>{{ enrollment.appliedAt | date:'mediumDate' }}</td><td><span [class]="'status-pill ' + enrollment.status.toLowerCase()">{{ enrollment.status }}</span></td><td class="row-actions">@if (enrollment.status === 'PENDING') { <button class="approve-button" type="button" (click)="$event.stopPropagation(); review(enrollment, 'approve')">Approve</button><button class="reject-button" type="button" (click)="$event.stopPropagation(); review(enrollment, 'reject')">Reject</button> } @else { <span class="muted">Reviewed</span> }</td></tr> } @empty { <tr><td colspan="5" class="empty-table">No applications yet.</td></tr> }</tbody></table></div></section>
            }
            @if (page() === 'courses') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Catalog</p><h2>Manage courses</h2></div><button class="button button-primary small-button" type="button" (click)="toggleForm('course')">{{ formOpen() === 'course' ? 'Close form' : 'New course' }}</button></div>@if (formOpen() === 'course') { <form class="management-form management-form-wide" (ngSubmit)="createCourse()"><div class="form-grid"><label>Name<input name="courseName" [(ngModel)]="courseForm.name" required /></label><label>Level<input name="courseLevel" [(ngModel)]="courseForm.level" placeholder="N5 / N4" required /></label></div><div class="form-grid"><label>Duration in months<input name="courseMonths" [(ngModel)]="courseForm.durationMonths" type="number" min="1" required /></label><label>Fee<input name="courseFee" [(ngModel)]="courseForm.fee" type="number" min="0" step="0.01" required /></label></div><label>Description<textarea name="courseDescription" [(ngModel)]="courseForm.description" rows="3"></textarea></label><button class="button button-primary small-button" [disabled]="busyAction() === 'course'">{{ busyAction() === 'course' ? 'Saving…' : 'Create course' }}</button></form> }<div class="admin-list">@for (course of courses(); track course.id) { <div class="overview-line clickable-row" (click)="openDetail('course', course.id)"><span><strong>{{ course.name }}</strong><small>{{ course.level }} · {{ course.durationMonths }} months · {{ course.fee | number:'1.0-2' }}</small></span><button class="danger-button" type="button" [disabled]="busyAction() === 'course-' + course.id" (click)="$event.stopPropagation(); deactivateCourse(course)">Deactivate</button></div> } @empty { <div class="empty-panel">No active courses yet.</div> }</div></section>
            }
            @if (page() === 'batches') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Class schedule</p><h2>Manage batches</h2></div><button class="button button-primary small-button" type="button" (click)="toggleForm('batch')">{{ formOpen() === 'batch' ? 'Close form' : 'New batch' }}</button></div>@if (formOpen() === 'batch') { <form class="management-form management-form-wide" (ngSubmit)="createBatch()"><div class="form-grid"><label>Course<select name="batchCourse" [(ngModel)]="batchForm.courseId" required><option [ngValue]="0">Choose a course</option>@for (course of courses(); track course.id) { <option [ngValue]="course.id">{{ course.name }}</option> }</select></label><label>Teacher <span class="optional">optional</span><select name="batchTeacher" [(ngModel)]="batchForm.teacherId"><option [ngValue]="undefined">Unassigned</option>@for (teacher of teachers(); track teacher.id) { <option [ngValue]="teacher.id">{{ teacher.fullName }}</option> }</select></label></div><div class="form-grid"><label>Batch name<input name="batchName" [(ngModel)]="batchForm.batchName" required /></label><label>Schedule<input name="schedule" [(ngModel)]="batchForm.schedule" placeholder="Saturday · 9:00 AM" required /></label></div><div class="form-grid"><label>Class type<select name="classType" [(ngModel)]="batchForm.classType"><option value="ONLINE">Online</option><option value="IN_PERSON">In person</option></select></label><label>Capacity<input name="batchCapacity" [(ngModel)]="batchForm.capacity" type="number" min="1" required /></label></div><div class="form-grid"><label>Starts<input name="startDate" [(ngModel)]="batchForm.startDate" type="date" required /></label><label>Ends<input name="endDate" [(ngModel)]="batchForm.endDate" type="date" required /></label></div><button class="button button-primary small-button" [disabled]="busyAction() === 'batch'">{{ busyAction() === 'batch' ? 'Saving…' : 'Create batch' }}</button></form> }<div class="batch-grid">@for (batch of batches(); track batch.id) { <article class="batch-card clickable-row" (click)="openDetail('batch', batch.id)"><span class="batch-status">{{ batch.status }}</span><h3>{{ batch.batchName }}</h3><p>{{ batch.courseName }}</p><dl><div><dt>Dates</dt><dd>{{ batch.startDate | date:'mediumDate' }} – {{ batch.endDate | date:'mediumDate' }}</dd></div><div><dt>Schedule</dt><dd>{{ batch.schedule }}</dd></div><div><dt>Teacher</dt><dd>{{ batch.teacherName || 'Unassigned' }}</dd></div></dl></article> } @empty { <div class="empty-panel">No batches yet.</div> }</div></section>
            }
            @if (page() === 'teachers') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Teaching team</p><h2>Manage teachers</h2></div><button class="button button-primary small-button" type="button" (click)="toggleForm('teacher')">{{ formOpen() === 'teacher' ? 'Close form' : 'New teacher' }}</button></div>@if (formOpen() === 'teacher') { <form class="management-form management-form-wide" (ngSubmit)="createTeacher()"><div class="form-grid"><label>Full name<input name="teacherName" [(ngModel)]="teacherForm.fullName" required /></label><label>Username<input name="teacherUsername" [(ngModel)]="teacherForm.username" required /></label></div><div class="form-grid"><label>Email<input name="teacherEmail" [(ngModel)]="teacherForm.email" type="email" required /></label><label>Temporary password<input name="teacherPassword" [(ngModel)]="teacherForm.password" type="password" minlength="8" required /></label></div><div class="form-grid"><label>Specialization<input name="specialization" [(ngModel)]="teacherForm.specialization" /></label><label>Qualification<input name="qualification" [(ngModel)]="teacherForm.qualification" /></label></div><button class="button button-primary small-button" [disabled]="busyAction() === 'teacher'">{{ busyAction() === 'teacher' ? 'Saving…' : 'Create teacher' }}</button></form> }<div class="admin-list">@for (teacher of teachers(); track teacher.id) { <div class="overview-line clickable-row" (click)="openDetail('teacher', teacher.id)"><span><strong>{{ teacher.fullName }}</strong><small>{{ teacher.username || 'Teacher account' }} · {{ teacher.specialization || 'Japanese language teacher' }} · {{ teacher.active ? 'Active' : 'Inactive' }}</small></span><button class="danger-button" type="button" (click)="$event.stopPropagation(); toggleTeacher(teacher)">{{ teacher.active ? 'Deactivate' : 'Activate' }}</button></div> } @empty { <div class="empty-panel">No teachers yet.</div> }</div></section>
            }
            @if (page() === 'students') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Learner records</p><h2>Manage students</h2></div><span class="count-badge">{{ students().length }}</span></div>
                @if (editingStudentId() !== null) {
                  <form class="management-form management-form-wide" (ngSubmit)="updateStudent()"><div class="form-grid"><label>Full name<input name="studentFullName" [(ngModel)]="studentForm.fullName" required /></label><label>Username<input name="studentUsername" [(ngModel)]="studentForm.username" required /></label></div><div class="form-grid"><label>Email<input name="studentEmail" [(ngModel)]="studentForm.email" type="email" required /></label><label>New password <span class="optional">optional</span><input name="studentPassword" [(ngModel)]="studentForm.password" type="password" minlength="8" placeholder="Leave blank to keep current" /></label></div><div class="form-grid"><label>Phone<input name="studentPhone" [(ngModel)]="studentForm.phone" /></label><label>Address<input name="studentAddress" [(ngModel)]="studentForm.address" /></label></div><label class="checkbox-field"><input name="studentEnabled" [(ngModel)]="studentForm.enabled" type="checkbox" /> Account active</label><div class="form-actions"><button class="button button-primary small-button" type="submit" [disabled]="busyAction() === 'student'">{{ busyAction() === 'student' ? 'Saving…' : 'Save student' }}</button><button class="button button-quiet small-button" type="button" (click)="cancelStudentEdit()">Cancel</button></div></form>
                }
                <div class="table-wrap"><table><thead><tr><th>Student</th><th>Contact</th><th>Phone</th><th>Status</th><th></th></tr></thead><tbody>@for (student of students(); track student.id) { <tr><td><strong>{{ student.fullName }}</strong><small>{{ student.username }}</small></td><td>{{ student.email }}<small>{{ student.address || 'No address provided' }}</small></td><td>{{ student.phone || '—' }}</td><td><span [class]="'status-pill ' + (student.enabled ? 'approved' : 'rejected')">{{ student.enabled ? 'ACTIVE' : 'INACTIVE' }}</span></td><td class="row-actions"><button class="button button-quiet small-button" type="button" (click)="editStudent(student)">Edit</button><button class="danger-button" type="button" (click)="toggleStudent(student)">{{ student.enabled ? 'Deactivate' : 'Activate' }}</button></td></tr> } @empty { <tr><td colspan="5" class="empty-table">No students found.</td></tr> }</tbody></table></div>
              </section>
            }
            @if (page() === 'users') {
              <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Accounts</p><h2>Manage users</h2></div></div><div class="admin-list">@for (user of users(); track user.id) { <div class="overview-line clickable-row" (click)="openDetail('user', user.id)"><span><strong>{{ user.username }}</strong><small>{{ user.email }} · {{ user.enabled ? 'Active' : 'Inactive' }}</small></span>@if (user.username !== auth.user()?.username) { <button class="danger-button" type="button" (click)="$event.stopPropagation(); toggleUser(user)">{{ user.enabled ? 'Deactivate' : 'Activate' }}</button> }</div> } @empty { <div class="empty-panel">No users found.</div> }</div></section>
            }
          }

          @if (role() === 'TEACHER') {
            @if (page() === 'overview') { <section class="metric-grid"><div class="metric-card"><span>Assigned classes</span><strong>{{ teacherBatches().length }}</strong><small>Your current teaching schedule</small></div><div class="metric-card"><span>Selected class</span><strong>{{ selectedBatch() ? '1' : '—' }}</strong><small>Open a class to see students</small></div><div class="metric-card"><span>Role</span><strong>Sensei</strong><small>Supporting learner progress</small></div></section><section class="panel"><div class="panel-heading"><div><p class="section-kicker">Teaching schedule</p><h2>Your assigned classes</h2></div><a class="inline-link" routerLink="/teacher/classes">View classes <span>→</span></a></div><div class="batch-grid">@for (batch of teacherBatches().slice(0, 3); track batch.id) { <article class="batch-card"><span class="batch-status">{{ batch.status }}</span><h3>{{ batch.batchName }}</h3><p>{{ batch.courseName }}</p><span class="muted">{{ batch.schedule }}</span></article> } @empty { <div class="empty-panel">No batches are assigned to you yet.</div> }</div></section> }
            @if (page() === 'classes') { <section class="panel"><div class="panel-heading"><div><p class="section-kicker">Your classes</p><h2>Assigned batches</h2></div><span class="count-badge">{{ teacherBatches().length }}</span></div><div class="batch-grid">@for (batch of teacherBatches(); track batch.id) { <article class="batch-card"><span class="batch-status">{{ batch.status }}</span><h3>{{ batch.batchName }}</h3><p>{{ batch.courseName }}</p><dl><div><dt>Schedule</dt><dd>{{ batch.schedule }}</dd></div><div><dt>Dates</dt><dd>{{ batch.startDate | date:'mediumDate' }} – {{ batch.endDate | date:'mediumDate' }}</dd></div></dl><button class="button button-quiet small-button" type="button" (click)="viewStudents(batch)">{{ selectedBatch()?.id === batch.id ? 'Hide students' : 'View students' }} <span>→</span></button></article> } @empty { <div class="empty-panel">No batches are assigned to you yet.</div> }</div></section>@if (selectedBatch()) { <section class="panel"><div class="panel-heading"><div><p class="section-kicker">{{ selectedBatch()?.batchName }}</p><h2>Approved students</h2></div></div><div class="student-list">@for (student of teacherStudents(); track student.id) { <div class="student-row"><span class="user-avatar small-avatar">{{ student.studentName.charAt(0) }}</span><span><strong>{{ student.studentName }}</strong><small>{{ student.courseName }}</small></span></div> } @empty { <div class="empty-panel">No approved students in this batch yet.</div> }</div></section> } }
          }
        }
      </main>
    </div>
  `,
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  readonly error = signal('');
  readonly role = signal<Role | null>(null);
  readonly currentPath = signal(this.router.url || '/dashboard');
  readonly enrollments = signal<Enrollment[]>([]);
  readonly batches = signal<Batch[]>([]);
  readonly courses = signal<Course[]>([]);
  readonly users = signal<UserSummary[]>([]);
  readonly students = signal<StudentSummary[]>([]);
  readonly teachers = signal<TeacherSummary[]>([]);
  readonly teacherBatches = signal<Batch[]>([]);
  readonly teacherStudents = signal<TeacherBatchStudent[]>([]);
  readonly selectedBatch = signal<Batch | null>(null);
  readonly enrolling = signal<number | null>(null);
  readonly busyAction = signal<string | null>(null);
  readonly formOpen = signal<'course' | 'batch' | 'teacher' | null>(null);
  readonly editingStudentId = signal<number | null>(null);
  readonly page = computed<DashboardPage>(() => {
    const path = this.currentPath();
    if (path.includes('/applications')) return 'applications';
    if (path.includes('/catalog')) return 'catalog';
    if (path.includes('/classes')) return 'classes';
    if (path.includes('/courses')) return 'courses';
    if (path.includes('/batches')) return 'batches';
    if (path.includes('/teachers')) return 'teachers';
    if (path.includes('/users')) return 'users';
    if (path.includes('/students')) return 'students';
    return 'overview';
  });
  readonly availableBatches = computed(() => { const appliedBatchIds = new Set(this.enrollments().map((item) => item.batchId)); return this.batches().filter((item) => item.status === 'OPEN' && item.available !== false && (item.availableSeats === undefined || item.availableSeats > 0) && !appliedBatchIds.has(item.id)); });
  readonly currentEnrollments = computed(() => this.enrollments().filter((item) => item.status === 'APPROVED' && this.batchFor(item)?.status !== 'COMPLETED'));
  readonly completedEnrollments = computed(() => this.enrollments().filter((item) => item.status === 'APPROVED' && this.batchFor(item)?.status === 'COMPLETED'));
  readonly navItems = computed<NavItem[]>(() => {
    if (this.role() === 'ADMIN') return [{ label: 'Overview', path: '/admin/dashboard' }, { label: 'Applications', path: '/admin/applications' }, { label: 'Students', path: '/admin/students' }, { label: 'Courses', path: '/admin/courses' }, { label: 'Batches', path: '/admin/batches' }, { label: 'Teachers', path: '/admin/teachers' }, { label: 'Users', path: '/admin/users' }];
    if (this.role() === 'TEACHER') return [{ label: 'Overview', path: '/teacher/dashboard' }, { label: 'My classes', path: '/teacher/classes' }];
    return [{ label: 'Overview', path: '/student/dashboard' }, { label: 'Browse batches', path: '/student/catalog' }, { label: 'Applications', path: '/student/applications' }, { label: 'My classes', path: '/student/classes' }];
  });
  courseForm: CourseRequest = { name: '', level: '', description: '', durationMonths: 1, fee: 0 };
  batchForm: BatchRequest = { courseId: 0, batchName: '', classType: 'ONLINE', startDate: '', endDate: '', schedule: '', capacity: 10 };
  teacherForm: TeacherRequest = { username: '', email: '', password: '', fullName: '', phone: '', specialization: '', qualification: '' };
  studentForm: AdminStudentUpdateRequest = { username: '', email: '', password: '', fullName: '', phone: '', address: '', enabled: true };
  private loadedRole: Role | null = null;

  constructor() {
    this.auth.restoreSession();
    this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe((event) => this.currentPath.set(event.urlAfterRedirects));
    effect(() => { const user = this.auth.user(); const role = user?.roles[0] ?? null; if (role && role !== this.loadedRole) { this.loadedRole = role; this.role.set(role); this.loadForRole(role); } });
  }

  pageHeading(): string { const p = this.page(); return p === 'overview' ? `Good to see you, ${this.auth.user()?.username || 'learner'}.` : p === 'catalog' ? 'Choose a class that fits your rhythm.' : p === 'applications' ? 'Your enrollment applications.' : p === 'classes' ? 'Your learning schedule.' : p === 'courses' ? 'Shape the Sakura catalog.' : p === 'batches' ? 'Build the class schedule.' : p === 'teachers' ? 'Care for the teaching team.' : p === 'students' ? 'Keep every learner record up to date.' : 'Keep the school community organized.'; }
  initials(): string { return (this.auth.user()?.username ?? 'S').slice(0, 1).toUpperCase(); }
  openDetail(type: 'user' | 'student' | 'teacher' | 'course' | 'batch' | 'enrollment', id: number): void {
    const routes: Record<typeof type, string> = {
      user: 'users',
      student: 'students',
      teacher: 'teachers',
      course: 'courses',
      batch: 'batches',
      enrollment: 'enrollments',
    };
    void this.router.navigate(['/admin', routes[type], id]);
  }
  toggleForm(form: 'course' | 'batch' | 'teacher'): void { this.formOpen.set(this.formOpen() === form ? null : form); this.error.set(''); }
  enroll(batch: Batch): void { if (!this.auth.hasRole('STUDENT')) { this.error.set('Only student accounts can apply for a batch.'); return; } const confirmed = window.confirm(`Apply for ${batch.batchName}?\n\nWe will send this application to Sakura for review.`); if (!confirmed) return; this.error.set(''); this.enrolling.set(batch.id); this.api.enroll(batch.id).pipe(finalize(() => this.enrolling.set(null))).subscribe({ next: (enrollment) => this.enrollments.update((items) => [enrollment, ...items]), error: (error) => this.handleError(error, 'We could not submit that application.') }); }
  review(enrollment: Enrollment, decision: 'approve' | 'reject'): void { this.api.reviewEnrollment(enrollment.id, decision).subscribe({ next: (updated) => this.enrollments.update((items) => items.map((item) => item.id === updated.id ? updated : item)), error: (error) => this.handleError(error, 'The application could not be updated.') }); }
  batchFor(enrollment: Enrollment): Batch | undefined { return this.batches().find((batch) => batch.id === enrollment.batchId); }
  viewStudents(batch: Batch): void { if (this.selectedBatch()?.id === batch.id) { this.selectedBatch.set(null); return; } this.selectedBatch.set(batch); this.api.teacherStudents(batch.id).subscribe({ next: (students) => this.teacherStudents.set(students), error: (error) => this.handleError(error, 'Students could not be loaded.') }); }
  createCourse(): void { this.save('course', this.api.createCourse(this.courseForm), (course) => { this.courses.update((items) => [course, ...items]); this.courseForm = { name: '', level: '', description: '', durationMonths: 1, fee: 0 }; this.formOpen.set(null); }); }
  createBatch(): void { this.save('batch', this.api.createBatch(this.batchForm), (batch) => { this.batches.update((items) => [batch, ...items]); this.batchForm = { courseId: 0, batchName: '', classType: 'ONLINE', startDate: '', endDate: '', schedule: '', capacity: 10 }; this.formOpen.set(null); }); }
  createTeacher(): void { this.save('teacher', this.api.createTeacher(this.teacherForm), (teacher) => { this.teachers.update((items) => [teacher, ...items]); this.teacherForm = { username: '', email: '', password: '', fullName: '', phone: '', specialization: '', qualification: '' }; this.formOpen.set(null); }); }
  deactivateCourse(course: Course): void { this.busyAction.set(`course-${course.id}`); this.api.deactivateCourse(course.id).pipe(finalize(() => this.busyAction.set(null))).subscribe({ next: () => this.courses.update((items) => items.filter((item) => item.id !== course.id)), error: (error) => this.handleError(error, 'The course could not be deactivated.') }); }
  toggleUser(user: UserSummary): void { const request = user.enabled ? this.api.deactivateUser(user.id) : this.api.activateUser(user.id); request.subscribe({ next: () => this.users.update((items) => items.map((item) => item.id === user.id ? { ...item, enabled: !user.enabled } : item)), error: (error) => this.handleError(error, 'The account status could not be changed.') }); }
  toggleTeacher(teacher: TeacherSummary): void { const request = teacher.active ? this.api.deactivateTeacher(teacher.id) : this.api.activateTeacher(teacher.id); request.subscribe({ next: () => this.teachers.update((items) => items.map((item) => item.id === teacher.id ? { ...item, active: !teacher.active } : item)), error: (error) => this.handleError(error, 'The teacher status could not be changed.') }); }
  editStudent(student: StudentSummary): void { this.openDetail('student', student.id); }
  cancelStudentEdit(): void { this.editingStudentId.set(null); }
  updateStudent(): void { const id = this.editingStudentId(); if (id === null) return; const request = { ...this.studentForm }; if (!request.password?.trim()) delete request.password; this.save('student', this.api.updateAdminStudent(id, request), (updated) => { this.students.update((items) => items.map((item) => item.id === updated.id ? updated : item)); this.cancelStudentEdit(); }); }
  toggleStudent(student: StudentSummary): void { const request = student.enabled ? this.api.deactivateStudent(student.id) : this.api.activateStudent(student.id); request.subscribe({ next: () => this.students.update((items) => items.map((item) => item.id === student.id ? { ...item, enabled: !student.enabled } : item)), error: (error) => this.handleError(error, 'The student status could not be changed.') }); }
  private loadForRole(role: Role): void { this.error.set(''); if (role === 'STUDENT') { this.api.myEnrollments().subscribe({ next: (items) => this.enrollments.set(items), error: (error) => this.handleError(error) }); this.api.batches().subscribe({ next: (items) => this.batches.set(items), error: (error) => this.handleError(error) }); } else if (role === 'ADMIN') { this.api.adminEnrollments().subscribe({ next: (items) => this.enrollments.set(items), error: (error) => this.handleError(error) }); this.api.adminUsers().subscribe({ next: (items) => this.users.set(items), error: (error) => this.handleError(error) }); this.api.adminStudents().subscribe({ next: (items) => this.students.set(items), error: (error) => this.handleError(error) }); this.api.courses().subscribe({ next: (items) => this.courses.set(items), error: (error) => this.handleError(error) }); this.api.batches().subscribe({ next: (items) => this.batches.set(items), error: (error) => this.handleError(error) }); this.api.adminTeachers().subscribe({ next: (items) => this.teachers.set(items), error: (error) => this.handleError(error) }); } else { this.api.teacherBatches().subscribe({ next: (items) => this.teacherBatches.set(items), error: (error) => this.handleError(error) }); } }
  private save<T>(action: string, request: Observable<T>, onSuccess: (value: T) => void): void { this.busyAction.set(action); request.pipe(finalize(() => this.busyAction.set(null))).subscribe({ next: onSuccess, error: (error) => this.handleError(error) }); }
  private handleError(error: { error?: { message?: string } }, fallback = 'Some dashboard data could not be loaded.'): void { this.error.set(error?.error?.message ?? fallback); }
}
