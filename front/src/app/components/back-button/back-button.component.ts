import { Component, Output, EventEmitter, Input } from '@angular/core';

@Component({
  selector: 'app-back-button',
  templateUrl: './back-button.component.html',
  styleUrls: ['./back-button.component.scss'],
})
export class BackButtonComponent {
  @Input() position: 'top-left' | 'inline' = 'top-left';
  @Input() label: string = '←';
  @Output() backClicked = new EventEmitter<void>();

  onBackClick(): void {
    this.backClicked.emit();
  }
}
