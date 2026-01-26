import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  isSidebarOpen = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  public isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  public logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    this.closeSidebar();
  }

  public toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  public closeSidebar(): void {
    this.isSidebarOpen = false;
  }
}
