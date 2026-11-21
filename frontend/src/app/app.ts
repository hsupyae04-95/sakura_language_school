import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, finalize } from 'rxjs';
import { ApiService } from './api.service';
import { IconComponent } from './icon';
import { Batch as ApiBatch, Course as ApiCourse } from './models';

type Course = {
  id?: number;
  title: string;
  level: string;
  duration: string;
  schedule: string;
  description: string;
  category: string;
  accent: string;
  icon: string;
  seats: string;
};

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule, IconComponent, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  protected readonly currentPath = signal(this.router.url || '/');
  protected readonly categories = ['All courses', 'Beginner', 'JLPT prep', 'Conversation', 'Business'];
  protected readonly selectedCategory = signal('All courses');
  protected readonly searchTerm = signal('');
  protected readonly menuOpen = signal(false);
  protected readonly toastMessage = signal('');
  protected readonly catalogLoading = signal(true);
  protected readonly catalogError = signal('');
  protected readonly batches = signal<ApiBatch[]>([]);

  private readonly starterCourses: Course[] = [
    { title: 'Japanese N5 Foundations', level: 'Beginner', duration: '6 months', schedule: 'Sat · 9:00 AM', description: 'Build a confident foundation in kana, everyday phrases, and essential grammar.', category: 'Beginner', accent: 'lavender', icon: 'book-open', seats: '8 seats left' },
    { title: 'Speak Japanese Naturally', level: 'Conversation', duration: '3 months', schedule: 'Wed · 6:30 PM', description: 'A friendly, practice-first class for real conversations and listening confidence.', category: 'Conversation', accent: 'peach', icon: 'message-circle', seats: '12 seats left' },
    { title: 'JLPT N4 Step Up', level: 'JLPT prep', duration: '6 months', schedule: 'Sun · 1:00 PM', description: 'Move beyond the basics with focused reading, listening, and exam strategy.', category: 'JLPT prep', accent: 'rose', icon: 'sparkles', seats: '5 seats left' },
    { title: 'Japanese for the Workplace', level: 'Business', duration: '4 months', schedule: 'Tue · 7:00 PM', description: 'Learn polished language for meetings, email, introductions, and workplace culture.', category: 'Business', accent: 'mint', icon: 'briefcase-business', seats: '10 seats left' },
    { title: 'Kanji Club', level: 'Beginner', duration: '3 months', schedule: 'Thu · 6:00 PM', description: 'Make kanji memorable with stories, patterns, and a little weekly practice.', category: 'Beginner', accent: 'sky', icon: 'pen-line', seats: '14 seats left' },
    { title: 'JLPT N3 Intensive', level: 'JLPT prep', duration: '7 months', schedule: 'Sat · 1:00 PM', description: 'A structured path for intermediate learners ready to take their next big step.', category: 'JLPT prep', accent: 'plum', icon: 'target', seats: '6 seats left' },
  ];
  protected readonly courses = signal<Course[]>(this.starterCourses);

  protected readonly filteredCourses = computed(() => {
    const category = this.selectedCategory();
    const query = this.searchTerm().trim().toLowerCase();
    return this.courses().filter((course) => {
      const matchesCategory = category === 'All courses' || course.category === category;
      const matchesSearch = !query || [course.title, course.level, course.description].some((value) => value.toLowerCase().includes(query));
      return matchesCategory && matchesSearch;
    });
  });

  constructor() {
    this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => this.currentPath.set(event.urlAfterRedirects));
    this.loadCatalog();
  }

  protected isHome(): boolean { return this.currentPath() === '/'; }

  protected goToLogin(): void { void this.router.navigateByUrl('/login'); }
  protected goToRegister(): void { void this.router.navigateByUrl('/register'); }
  protected openCourse(course: Course): void {
    if (course.id) void this.router.navigate(['/course', course.id]);
    else this.showToast('Course details will be available when the school catalog is online.');
  }

  protected nextBatch(): ApiBatch | undefined {
    return this.batches().find((batch) => batch.status === 'OPEN' || batch.status === 'AVAILABLE') ?? this.batches()[0];
  }

  private loadCatalog(): void {
    this.api.courses().pipe(finalize(() => this.catalogLoading.set(false))).subscribe({
      next: (courses) => this.courses.set(courses.filter((course) => course.active).map((course) => this.toDisplayCourse(course))),
      error: () => this.catalogError.set('Showing our starter catalog while the live catalog is unavailable.'),
    });
    this.api.batches().subscribe({
      next: (batches) => this.batches.set(batches),
      error: () => this.catalogError.set('Upcoming batches will appear when the school API is online.'),
    });
  }

  private toDisplayCourse(course: ApiCourse): Course {
    const level = (course.level || 'Beginner').toLowerCase();
    const category = level.includes('jlpt') ? 'JLPT prep' : level.includes('business') ? 'Business' : level.includes('conversation') ? 'Conversation' : 'Beginner';
    const accent = category === 'JLPT prep' ? 'rose' : category === 'Business' ? 'mint' : category === 'Conversation' ? 'peach' : 'lavender';
    return {
      id: course.id,
      title: course.name,
      level: course.level,
      duration: `${course.durationMonths} months`,
      schedule: course.fee ? `Tuition ${course.fee}` : 'Schedule announced soon',
      description: course.description || 'A thoughtful Japanese class designed around practical progress.',
      category,
      accent,
      icon: category === 'Business' ? 'briefcase-business' : category === 'JLPT prep' ? 'target' : 'book-open',
      seats: 'View available batches',
    };
  }

  protected chooseCategory(category: string): void { this.selectedCategory.set(category); }

  protected scrollToCourses(): void { document.querySelector('#courses')?.scrollIntoView({ behavior: 'smooth' }); }

  protected showToast(message: string): void {
    this.toastMessage.set(message);
    window.setTimeout(() => this.toastMessage.set(''), 2800);
  }
}
