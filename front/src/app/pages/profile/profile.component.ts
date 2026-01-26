import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { TopicService } from 'src/app/services/topic.service';
import { User } from 'src/app/interfaces/auth.interface';
import { Topic } from 'src/app/interfaces/topic.interface';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  subscriptions: Topic[] = [];
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private topicService: TopicService,
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadUserData();
    this.loadSubscriptions();
  }

  private initForm(): void {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(8)]],
    });
  }

  private loadUserData(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user: User) => {
        this.profileForm.patchValue({
          name: user.name,
          email: user.email,
        });
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement du profil';
        console.error('Error loading user:', err);
      },
    });
  }

  private loadSubscriptions(): void {
    this.topicService.getUserSubscriptions().subscribe({
      next: (topics: Topic[]) => {
        this.subscriptions = topics;
      },
      error: (err) => {
        console.error('Error loading subscriptions:', err);
      },
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;
    this.successMessage = null;

    const updateData: any = {
      name: this.profileForm.value.name,
      email: this.profileForm.value.email,
    };

    // Only include password if it was changed
    if (this.profileForm.value.password) {
      updateData.password = this.profileForm.value.password;
    }

    this.authService.updateProfile(updateData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Profil mis à jour avec succès';
        this.profileForm.patchValue({ password: '' }); // Clear password field
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err.error?.message || 'Erreur lors de la mise à jour du profil';
        console.error('Error updating profile:', err);
      },
    });
  }

  unsubscribe(topicId: number): void {
    this.topicService.unsubscribe(topicId).subscribe({
      next: () => {
        this.subscriptions = this.subscriptions.filter(
          (topic) => topic.id !== topicId,
        );
      },
      error: (err) => {
        this.error = 'Erreur lors du désabonnement';
        console.error('Error unsubscribing:', err);
      },
    });
  }
}
