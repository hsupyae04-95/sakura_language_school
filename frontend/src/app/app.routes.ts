import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';
import { DashboardComponent } from './dashboard.component';
import { CourseDetailComponent } from './course-detail.component';
import { LoginComponent } from './login.component';
import { RegisterComponent } from './register.component';
import { AdminDetailComponent } from './admin-detail.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'course/:id', component: CourseDetailComponent },
  { path: 'admin/users/:id', component: AdminDetailComponent, data: { detailType: 'user' }, canActivate: [authGuard] },
  { path: 'admin/students/:id', component: AdminDetailComponent, data: { detailType: 'student' }, canActivate: [authGuard] },
  { path: 'admin/teachers/:id', component: AdminDetailComponent, data: { detailType: 'teacher' }, canActivate: [authGuard] },
  { path: 'admin/courses/:id', component: AdminDetailComponent, data: { detailType: 'course' }, canActivate: [authGuard] },
  { path: 'admin/batches/:id', component: AdminDetailComponent, data: { detailType: 'batch' }, canActivate: [authGuard] },
  { path: 'admin/enrollments/:id', component: AdminDetailComponent, data: { detailType: 'enrollment' }, canActivate: [authGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'student/:page', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'admin/:page', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'teacher/:page', component: DashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
