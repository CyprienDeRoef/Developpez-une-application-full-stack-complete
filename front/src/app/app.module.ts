import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HeaderComponent } from './components/header/header.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { FeedComponent } from './pages/feed/feed.component';
import { CreatePostComponent } from './pages/create-post/create-post.component';
import { TopicsComponent } from './pages/topics/topics.component';
import { PostDetailComponent } from './pages/post-detail/post-detail.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { PostCardComponent } from './components/post-card/post-card.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { AlertMessageComponent } from './components/alert-message/alert-message.component';
import { TopicCardComponent } from './components/topic-card/topic-card.component';
import { CommentComponent } from './components/comment/comment.component';
import { CommentFormComponent } from './components/comment-form/comment-form.component';
import { TopicSelectorComponent } from './components/topic-selector/topic-selector.component';
import { FormHeaderComponent } from './components/form-header/form-header.component';
import { FormFieldComponent } from './components/form-field/form-field.component';
import { BackButtonComponent } from './components/back-button/back-button.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    HeaderComponent,
    FeedComponent,
    CreatePostComponent,
    TopicsComponent,
    PostDetailComponent,
    ProfileComponent,
    PostCardComponent,
    LoadingSpinnerComponent,
    AlertMessageComponent,
    TopicCardComponent,
    CommentComponent,
    CommentFormComponent,
    TopicSelectorComponent,
    FormHeaderComponent,
    FormFieldComponent,
    BackButtonComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
