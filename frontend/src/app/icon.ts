import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `
    <svg class="icon" [attr.width]="size" [attr.height]="size" viewBox="0 0 24 24" fill="none" stroke="currentColor" [attr.stroke-width]="strokeWidth" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      @switch (name) {
        @case ('sakura') { <path d="M12 13c-2.3-1.8-4.8-1.8-6.2-.5-1.1 1.1-.7 3.3 1.3 4.3 2.1 1 3.9-.3 4.9-2.4 1 2.1 2.8 3.4 4.9 2.4 2-1 2.4-3.2 1.3-4.3C16.8 11.2 14.3 11.2 12 13Z"/><path d="M12 13c1.8-2.3 1.8-4.8.5-6.2-1.1-1.1-3.3-.7-4.3 1.3-1 2.1.3 3.9 2.4 4.9-2.1 1-3.4 2.8-2.4 4.9 1 2 3.2 2.4 4.3 1.3C13.8 17.8 13.8 15.3 12 13Z"/><path d="M12 13c-1.8-2.3-1.8-4.8-.5-6.2 1.1-1.1 3.3-.7 4.3 1.3 1 2.1-.3 3.9-2.4 4.9 2.1 1 3.4 2.8 2.4 4.9-1 2-3.2 2.4-4.3 1.3C10.2 17.8 10.2 15.3 12 13Z"/><circle cx="12" cy="13" r="1.2"/> }
        @case ('menu') { <path d="M4 6h16M4 12h16M4 18h16"/> }
        @case ('x') { <path d="m6 6 12 12M18 6 6 18"/> }
        @case ('arrow-right') { <path d="M5 12h14M13 6l6 6-6 6"/> }
        @case ('arrow-up-right') { <path d="M7 17 17 7M7 7h10v10"/> }
        @case ('user-plus') { <path d="M15 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M8.5 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM19 8v6M22 11h-6"/> }
        @case ('heart') { <path d="M20.8 8.8c0 5.2-8.8 10.1-8.8 10.1S3.2 14 3.2 8.8a4.5 4.5 0 0 1 8-2.7 4.5 4.5 0 0 1 8 2.7Z"/> }
        @case ('calendar-days') { <rect x="3" y="4.5" width="18" height="17" rx="2"/><path d="M16 2.5v4M8 2.5v4M3 10h18M8 14h.01M12 14h.01M16 14h.01M8 18h.01M12 18h.01"/> }
        @case ('search') { <circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 5 5"/> }
        @case ('sparkle') { <path d="m12 3-1.2 4.8L6 9l4.8 1.2L12 15l1.2-4.8L18 9l-4.8-1.2L12 3ZM19 15l-.6 2.4L16 18l2.4.6L19 21l.6-2.4L22 18l-2.4-.6L19 15Z"/> }
        @case ('sparkles') { <path d="m12 3-1.4 5.6L5 10l5.6 1.4L12 17l1.4-5.6L19 10l-5.6-1.4L12 3ZM5 17l-.7 2.3L2 20l2.3.7L5 23l.7-2.3L8 20l-2.3-.7L5 17Z"/> }
        @case ('clock') { <circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/> }
        @case ('users') { <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM22 21v-2a4 4 0 0 0-3-3.9M16 3.1a4 4 0 0 1 0 7.8"/> }
        @case ('book-open') { <path d="M2.5 5.5A2.5 2.5 0 0 1 5 3h5a2 2 0 0 1 2 2v16a2 2 0 0 0-2-2H5a2.5 2.5 0 0 0-2.5 2.5v-16ZM21.5 5.5A2.5 2.5 0 0 0 19 3h-5a2 2 0 0 0-2 2v16a2 2 0 0 1 2-2h5a2.5 2.5 0 0 1 2.5 2.5v-16Z"/> }
        @case ('message-circle') { <path d="M20.5 11.5a8.5 8.5 0 0 1-8.7 8.5 8.5 8.5 0 0 1-4.2-1.1L3 20l1.2-4.2a8.5 8.5 0 1 1 16.3-4.3Z"/><path d="M8 12h.01M12 12h.01M16 12h.01"/> }
        @case ('briefcase-business') { <rect x="3" y="7" width="18" height="13" rx="2"/><path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M3 12h18M10 12v2h4v-2"/> }
        @case ('pen-line') { <path d="m13 21 8-8M3 21h3l11.5-11.5a2.1 2.1 0 0 0-3-3L3 18v3ZM15 6l3 3"/> }
        @case ('target') { <circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1"/> }
        @case ('messages-square') { <path d="M21 11.5a6.5 6.5 0 0 1-6.5 6.5H8l-5 3v-9.5A6.5 6.5 0 0 1 9.5 5H14"/><path d="M16 3h3a3 3 0 0 1 3 3v5l-2 1.5M10 10h.01M14 10h.01"/> }
        @case ('sprout') { <path d="M7 20h10M12 20V9M12 13a5 5 0 0 0-5-5H5v1a5 5 0 0 0 5 5h2ZM12 10a5 5 0 0 1 5-5h2v1a5 5 0 0 1-5 5h-2Z"/> }
        @case ('coffee') { <path d="M4 10h13v5a4 4 0 0 1-4 4H8a4 4 0 0 1-4-4v-5ZM17 12h1.5a2.5 2.5 0 0 1 0 5H17M6 3v3M10 3v3M14 3v3"/> }
        @case ('search-x') { <circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 5 5M8.5 8.5l4.5 4.5M13 8.5l-4.5 4.5"/> }
        @case ('info') { <circle cx="12" cy="12" r="9"/><path d="M12 11v5M12 8h.01"/> }
      }
    </svg>
  `,
})
export class IconComponent {
  @Input() name = '';
  @Input() size = 20;
  @Input() strokeWidth = 1.8;
}
