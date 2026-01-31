import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-alert-message',
  templateUrl: './alert-message.component.html',
  styleUrls: ['./alert-message.component.scss'],
})
export class AlertMessageComponent {
  @Input() type: 'success' | 'danger' | 'warning' | 'info' = 'info';
  @Input() message: string | null = null;
  @Output() dismissed = new EventEmitter<void>();

  onDismiss(): void {
    this.dismissed.emit();
  }
}
