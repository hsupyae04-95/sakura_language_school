import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import {
  ApiResponse,
  AuthResponse,
  Batch,
  BatchRequest,
  Course,
  CourseRequest,
  CurrentUser,
  Enrollment,
  StudentRequest,
  TeacherRequest,
  TeacherBatchStudent,
  TeacherSummary,
  UserSummary,
  AdminStudentUpdateRequest,
  StudentSummary,
  UserUpdateRequest,
  AdminTeacherUpdateRequest,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  private data<T>(request: Observable<ApiResponse<T>>): Observable<T> {
    return request.pipe(map((response) => response.data));
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.data(this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/login`, { username, password }));
  }

  register(request: StudentRequest): Observable<unknown> {
    return this.data(this.http.post<ApiResponse<unknown>>(`${this.baseUrl}/auth/register`, request));
  }

  currentUser(): Observable<CurrentUser> {
    return this.data(this.http.get<ApiResponse<CurrentUser>>(`${this.baseUrl}/auth/me`));
  }

  courses(): Observable<Course[]> {
    return this.data(this.http.get<ApiResponse<Course[]>>(`${this.baseUrl}/courses`));
  }

  course(id: number): Observable<Course> {
    return this.data(this.http.get<ApiResponse<Course>>(`${this.baseUrl}/courses/${id}`));
  }

  batches(): Observable<Batch[]> {
    return this.data(this.http.get<ApiResponse<Batch[]>>(`${this.baseUrl}/batches`));
  }

  myEnrollments(): Observable<Enrollment[]> {
    return this.data(this.http.get<ApiResponse<Enrollment[]>>(`${this.baseUrl}/enrollments/my`));
  }

  enroll(batchId: number): Observable<Enrollment> {
    return this.data(this.http.post<ApiResponse<Enrollment>>(`${this.baseUrl}/enrollments`, { batchId, confirmed: true }));
  }

  adminEnrollments(): Observable<Enrollment[]> {
    return this.data(this.http.get<ApiResponse<Enrollment[]>>(`${this.baseUrl}/admin/enrollments`));
  }

  reviewEnrollment(id: number, decision: 'approve' | 'reject', adminNote?: string): Observable<Enrollment> {
    return this.data(this.http.patch<ApiResponse<Enrollment>>(
      `${this.baseUrl}/admin/enrollments/${id}/${decision}`,
      adminNote?.trim() ? { adminNote } : {},
    ));
  }

  adminUsers(): Observable<UserSummary[]> {
    return this.data(this.http.get<ApiResponse<UserSummary[]>>(`${this.baseUrl}/admin/users`));
  }

  adminUser(id: number): Observable<UserSummary> {
    return this.data(this.http.get<ApiResponse<UserSummary>>(`${this.baseUrl}/admin/users/${id}`));
  }

  updateAdminUser(id: number, request: UserUpdateRequest): Observable<UserSummary> {
    return this.data(this.http.put<ApiResponse<UserSummary>>(`${this.baseUrl}/admin/users/${id}`, request));
  }

  adminStudents(): Observable<StudentSummary[]> {
    return this.data(this.http.get<ApiResponse<StudentSummary[]>>(`${this.baseUrl}/admin/students`));
  }

  adminStudent(id: number): Observable<StudentSummary> {
    return this.data(this.http.get<ApiResponse<StudentSummary>>(`${this.baseUrl}/admin/students/${id}`));
  }

  updateAdminStudent(id: number, request: AdminStudentUpdateRequest): Observable<StudentSummary> {
    return this.data(this.http.put<ApiResponse<StudentSummary>>(`${this.baseUrl}/admin/students/${id}`, request));
  }

  deactivateStudent(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/students/${id}/deactivate`, {}));
  }

  activateStudent(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/students/${id}/activate`, {}));
  }

  deactivateUser(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/users/${id}/deactivate`, {}));
  }

  activateUser(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/users/${id}/activate`, {}));
  }

  deactivateTeacher(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/teachers/${id}/deactivate`, {}));
  }

  activateTeacher(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/teachers/${id}/activate`, {}));
  }

  adminTeachers(): Observable<TeacherSummary[]> {
    return this.data(this.http.get<ApiResponse<TeacherSummary[]>>(`${this.baseUrl}/admin/teachers`));
  }

  adminTeacher(id: number): Observable<TeacherSummary> {
    return this.data(this.http.get<ApiResponse<TeacherSummary>>(`${this.baseUrl}/admin/teachers/${id}`));
  }

  updateAdminTeacher(id: number, request: AdminTeacherUpdateRequest): Observable<TeacherSummary> {
    return this.data(this.http.put<ApiResponse<TeacherSummary>>(`${this.baseUrl}/admin/teachers/${id}`, request));
  }

  createTeacher(request: TeacherRequest): Observable<TeacherSummary> {
    return this.data(this.http.post<ApiResponse<TeacherSummary>>(`${this.baseUrl}/admin/teachers`, request));
  }

  createCourse(request: CourseRequest): Observable<Course> {
    return this.data(this.http.post<ApiResponse<Course>>(`${this.baseUrl}/admin/courses`, request));
  }

  adminCourse(id: number): Observable<Course> {
    return this.data(this.http.get<ApiResponse<Course>>(`${this.baseUrl}/admin/courses/${id}`));
  }

  updateCourse(id: number, request: CourseRequest): Observable<Course> {
    return this.data(this.http.put<ApiResponse<Course>>(`${this.baseUrl}/admin/courses/${id}`, request));
  }

  deactivateCourse(id: number): Observable<unknown> {
    return this.data(this.http.patch<ApiResponse<unknown>>(`${this.baseUrl}/admin/courses/${id}/deactivate`, {}));
  }

  createBatch(request: BatchRequest): Observable<Batch> {
    return this.data(this.http.post<ApiResponse<Batch>>(`${this.baseUrl}/admin/batches`, request));
  }

  adminBatch(id: number): Observable<Batch> {
    return this.data(this.http.get<ApiResponse<Batch>>(`${this.baseUrl}/admin/batches/${id}`));
  }

  updateBatch(id: number, request: BatchRequest): Observable<Batch> {
    return this.data(this.http.put<ApiResponse<Batch>>(`${this.baseUrl}/admin/batches/${id}`, request));
  }

  adminEnrollment(id: number): Observable<Enrollment> {
    return this.data(this.http.get<ApiResponse<Enrollment>>(`${this.baseUrl}/admin/enrollments/${id}`));
  }

  teacherBatches(): Observable<Batch[]> {
    return this.data(this.http.get<ApiResponse<Batch[]>>(`${this.baseUrl}/teacher/batches`));
  }

  teacherStudents(batchId: number): Observable<TeacherBatchStudent[]> {
    return this.data(this.http.get<ApiResponse<TeacherBatchStudent[]>>(`${this.baseUrl}/teacher/batches/${batchId}/students`));
  }
}

