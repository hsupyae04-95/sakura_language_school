export type Role = 'ADMIN' | 'TEACHER' | 'STUDENT';

export interface ApiResponse<T> {
  status: string | number;
  code: string;
  message: string;
  responseAt: string;
  data: T;
}

export interface ApiError {
  status?: string | number;
  code?: string;
  message?: string;
}

export interface Course {
  id: number;
  name: string;
  level: string;
  description: string;
  durationMonths: number;
  fee: number;
  active: boolean;
}

export interface Batch {
  id: number;
  courseId: number;
  courseName: string;
  teacherId?: number;
  teacherName?: string;
  batchName: string;
  classType: string;
  startDate: string;
  endDate: string;
  schedule: string;
  capacity: number;
  enrolledCount?: number;
  availableSeats?: number;
  available?: boolean;
  status: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface CurrentUser {
  username: string;
  roles: Role[];
}

export interface StudentRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  address?: string;
}

export interface Enrollment {
  id: number;
  studentId: number;
  studentName: string;
  batchId: number;
  batchName: string;
  courseId: number;
  courseName: string;
  status: string;
  appliedAt: string;
  reviewedAt?: string;
  approvedAt?: string;
  reviewedBy?: number;
  adminNote?: string;
}

export interface UserSummary {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
}

export interface UserUpdateRequest {
  username: string;
  email: string;
}

export interface StudentSummary {
  id: number;
  userId: number;
  username: string;
  email: string;
  fullName: string;
  phone?: string;
  address?: string;
  enabled: boolean;
}

export interface AdminStudentUpdateRequest {
  username: string;
  email: string;
  password?: string;
  fullName: string;
  phone?: string;
  address?: string;
  enabled?: boolean;
}

export interface CourseRequest {
  name: string;
  level: string;
  description: string;
  durationMonths: number;
  fee: number;
}

export interface BatchRequest {
  courseId: number;
  teacherId?: number;
  batchName: string;
  classType: 'ONLINE' | 'IN_PERSON';
  startDate: string;
  endDate: string;
  schedule: string;
  capacity: number;
}

export interface TeacherRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  specialization?: string;
  qualification?: string;
}

export interface AdminTeacherUpdateRequest {
  username: string;
  email: string;
  password?: string;
  fullName: string;
  phone?: string;
  specialization?: string;
  qualification?: string;
  active?: boolean;
}

export interface TeacherSummary {
  id: number;
  userId?: number;
  username?: string;
  email?: string;
  fullName: string;
  phone?: string;
  specialization?: string;
  qualification?: string;
  active: boolean;
}

export interface TeacherBatchStudent extends Enrollment {}

