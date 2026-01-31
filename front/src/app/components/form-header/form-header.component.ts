import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-form-header',
  templateUrl: './form-header.component.html',
  styleUrls: ['./form-header.component.scss'],
})
export class FormHeaderComponent {
  @Input() title: string = '';
  @Input() showBackButton: boolean = true;
  @Input() centered: boolean = false;
  @Output() backClicked = new EventEmitter<void>();

  onBackClick(): void {
    this.backClicked.emit();
  }
}
