# MDD - Monde de Dév - Réseau Social pour Développeurs

Application full-stack permettant aux développeurs de s'abonner à des thèmes techniques, publier des articles et commenter les publications.

## Table des matières

- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [Documentation API](#documentation-api)
  - [Authentification](#authentification)
  - [Thèmes](#thèmes)
  - [Articles](#articles)
  - [Commentaires](#commentaires)
  - [Utilisateurs](#utilisateurs)
- [Architecture](#architecture)
  - [Architecture Backend](#architecture-backend)
  - [Architecture Frontend](#architecture-frontend)
  - [Sécurité des données](#sécurité-des-données)
- [Structure du code](#structure-du-code)
  - [Structure Backend](#structure-backend)
  - [Structure Frontend](#structure-frontend)
- [Conventions de code](#conventions-de-code)
- [Tests](#tests)
  - [Tests Backend](#tests-backend)
  - [Tests Frontend](#tests-frontend)
- [FAQ Utilisateur](#faq-utilisateur)

---

## Prérequis

- **Java**: JDK 17 ou supérieur
- **Node.js**: v14 ou supérieur
- **npm**: v6 ou supérieur
- **MySQL**: v8 ou supérieur
- **Maven**: v3.6 ou supérieur

---

## Installation

### 1. Cloner le dépôt

```bash
git clone <repository-url>
cd P6-Full-Stack-reseau-dev
```

### 2. Configuration de la base de données

Créer une base de données MySQL :

```sql
CREATE DATABASE mdd_db;
```

### 3. Backend

```bash
cd back
# Configurer application.properties (voir section Configuration)
mvn clean install
```

### 4. Frontend

```bash
cd front
npm install
```

---

## Configuration

### Backend - application.properties

Créer le fichier `back/src/main/resources/application.properties` :

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mdd_db?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=your-secret-key-min-256-bits
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### Frontend - Proxy Configuration

Le fichier `front/proxy.conf.json` est déjà configuré pour rediriger les appels API :

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

---

## Démarrage

### Backend

```bash
cd back
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080`

### Frontend

```bash
cd front
npm start
# ou
ng serve
```

L'application sera disponible sur `http://localhost:4200`

---

## Structure du code

### Structure Backend

Le backend suit une architecture MVC avec séparation des responsabilités :

#### Contrôleurs (Controllers)

Les contrôleurs gèrent les requêtes HTTP et délèguent la logique métier aux services.

**Exemple** - [AuthController.java](back/src/main/java/com/openclassrooms/mddapi/controllers/AuthController.java):

```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final RegisterService registerService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(registerService.registerUser(request));
    }
}
```

**Responsabilités**:

- Validation des données entrantes (`@Valid`)
- Mapping des endpoints REST
- Gestion des codes HTTP de réponse
- Transformation DTO ↔ Entités

#### Services

Les services contiennent toute la logique métier de l'application.

**Exemple** - [TopicService.java](back/src/main/java/com/openclassrooms/mddapi/services/TopicService.java):

```java
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        // Vérification unicité
        if (topicRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Topic already exists");
        }

        // Création et sauvegarde
        Topic topic = new Topic(request.getName(), request.getDescription());
        Topic savedTopic = topicRepository.save(topic);

        return convertToResponse(savedTopic);
    }
}
```

**Responsabilités**:

- Logique métier complexe
- Validation des règles métier
- Orchestration des appels repository
- Gestion des transactions (`@Transactional`)
- Transformation entités → DTO

#### Repositories

Les repositories gèrent l'accès aux données via JPA.

**Exemple** - [PostRepository.java](back/src/main/java/com/openclassrooms/mddapi/repositories/PostRepository.java):

```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByTopicsId(Long topicId);

    @Query("SELECT p FROM Post p JOIN p.topics t WHERE t.id IN :topicIds ORDER BY p.createdAt DESC")
    List<Post> findByTopicIdsOrderByCreatedAtDesc(@Param("topicIds") List<Long> topicIds);
}
```

**Responsabilités**:

- Requêtes base de données
- Méthodes CRUD automatiques (JpaRepository)
- Requêtes personnalisées (`@Query`)
- Gestion du cache JPA

#### Entités (Entities)

Les entités représentent les tables de la base de données.

**Exemple** - [Post.java](back/src/main/java/com/openclassrooms/mddapi/entities/Post.java):

```java
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
        name = "post_topics",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<Topic> topics = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Relations**:

- `User` ←→ `Post` : One-to-Many
- `Post` ←→ `Topic` : Many-to-Many
- `Post` ←→ `Comment` : One-to-Many
- `User` ←→ `Topic` : Many-to-Many (subscriptions)

#### DTOs (Data Transfer Objects)

Les DTOs sont utilisés pour les échanges client/serveur.

**Exemple** - [PostResponse.java](back/src/main/java/com/openclassrooms/mddapi/dto/PostResponse.java):

```java
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private List<Long> topicIds;
    private List<String> topicNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Avantages**:

- Masquage des détails d'implémentation
- Sécurité (pas d'exposition de mot de passe)
- Flexibilité (structure différente de l'entité)
- Performance (évite le lazy loading inutile)

---

### Structure Frontend

Le frontend Angular suit le pattern Component-Service-Model.

#### Composants (Components)

Les composants gèrent l'affichage et les interactions utilisateur.

**Exemple** - [feed.component.ts](front/src/app/pages/feed/feed.component.ts):

```typescript
@Component({
  selector: "app-feed",
  templateUrl: "./feed.component.html",
  styleUrls: ["./feed.component.scss"],
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  loading = false;

  constructor(
    private postService: PostService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.loading = true;
    this.postService
      .getAllPosts()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (posts) => (this.posts = posts),
        error: (error) => console.error("Error loading posts", error),
      });
  }

  navigateToPost(postId: number): void {
    this.router.navigate(["/posts", postId]);
  }
}
```

**Responsabilités**:

- Gestion de l'état du composant
- Interaction avec les services
- Binding des données dans le template
- Gestion des événements utilisateur

#### Services

Les services gèrent la logique métier et les appels API.

**Exemple** - [post.service.ts](front/src/app/services/post.service.ts):

```typescript
@Injectable({
  providedIn: "root",
})
export class PostService {
  private apiUrl = "/api/posts";

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPost(post: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }

  updatePost(id: number, post: CreatePostRequest): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, post);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

**Responsabilités**:

- Communication HTTP avec le backend
- Gestion des Observables RxJS
- Transformation des données
- Mise en cache si nécessaire

#### Models (Interfaces)

Les models définissent la structure des données TypeScript.

**Exemple** - [post.model.ts](front/src/app/models/post.model.ts):

```typescript
export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorName: string;
  topicIds: number[];
  topicNames: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  topicIds: number[];
}
```

#### Guards

Les guards protègent les routes nécessitant une authentification.

**Exemple** - [auth.guard.ts](front/src/app/guards/auth.guard.ts):

```typescript
@Injectable({
  providedIn: "root",
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    this.router.navigate(["/login"]);
    return false;
  }
}
```

#### Intercepteurs

Les intercepteurs modifient automatiquement les requêtes HTTP.

**Exemple** - [jwt.interceptor.ts](front/src/app/interceptors/jwt.interceptor.ts):

```typescript
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    const token = localStorage.getItem("token");

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    return next.handle(req);
  }
}
```

---

## Conventions de code

### Conventions Backend (Java)

#### Nommage

- **Classes**: PascalCase
  - Entités: `User`, `Topic`, `Post`
  - Services: `UserService`, `TopicService`
  - Controllers: `UserController`, `AuthController`
  - DTOs: `UserDto`, `RegisterRequest`, `JwtResponse`

- **Méthodes**: camelCase
  - `getUserById()`, `createTopic()`, `subscribeToTopic()`
  - Préfixes: `get`, `find`, `create`, `update`, `delete`

- **Variables**: camelCase
  - `userId`, `topicName`, `createdAt`

- **Constants**: UPPER_SNAKE_CASE
  - `MAX_LOGIN_ATTEMPTS`, `JWT_EXPIRATION`

#### Organisation du code

```java
// 1. Annotations de classe
@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
public class TopicController {

    // 2. Dépendances (injection par constructeur)
    private final TopicService topicService;
    private final CurrentUserService currentUserService;

    // 3. Constructeur
    public TopicController(TopicService topicService, CurrentUserService currentUserService) {
        this.topicService = topicService;
        this.currentUserService = currentUserService;
    }

    // 4. Endpoints (ordre logique: GET, POST, PUT, DELETE)
    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        // ...
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        // ...
    }
}
```

#### Documentation

```java
/**
 * Service for managing topics
 * Handles CRUD operations and user subscriptions
 */
@Service
public class TopicService {

    /**
     * Subscribe a user to a topic
     *
     * @param topicId the ID of the topic
     * @param userId the ID of the user
     * @throws ResourceNotFoundException if topic or user not found
     * @throws BadRequestException if already subscribed
     */
    @Transactional
    public void subscribeToTopic(Long topicId, Long userId) {
        // Implementation
    }
}
```

#### Tests

- Nom des classes de test: `{ClasseName}Test`
  - `UserServiceTest`, `TopicControllerTest`

- Nom des méthodes de test: `methodName_condition_expectedResult`
  - `getUserById_WhenUserExists_ShouldReturnUser()`
  - `createTopic_WhenNameAlreadyExists_ShouldThrowException()`

```java
@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void getAllTopics_ShouldReturnAllTopics() {
        // Given
        List<Topic> topics = Arrays.asList(
            new Topic("Java", "Java programming"),
            new Topic("Python", "Python programming")
        );
        when(topicRepository.findAll()).thenReturn(topics);

        // When
        List<TopicResponse> result = topicService.getAllTopics();

        // Then
        assertEquals(2, result.size());
        verify(topicRepository, times(1)).findAll();
    }
}
```

---

### Conventions Frontend (TypeScript/Angular)

#### Nommage

- **Classes/Interfaces**: PascalCase
  - `AppComponent`, `AuthService`, `Post`, `User`

- **Fichiers**: kebab-case
  - `app.component.ts`, `auth.service.ts`, `post-detail.component.ts`

- **Méthodes**: camelCase
  - `loadPosts()`, `onSubmit()`, `navigateToPost()`

- **Variables**: camelCase
  - `currentUser`, `isLoading`, `posts$` (Observable)

- **Constants**: UPPER_SNAKE_CASE ou camelCase
  - `API_URL`, `DEFAULT_PAGE_SIZE`

#### Organisation des composants

```typescript
@Component({
  selector: "app-post-detail",
  templateUrl: "./post-detail.component.html",
  styleUrls: ["./post-detail.component.scss"],
})
export class PostDetailComponent implements OnInit, OnDestroy {
  // 1. Propriétés publiques (accessibles dans le template)
  post?: Post;
  comments: Comment[] = [];
  loading = false;

  // 2. Propriétés privées
  private destroy$ = new Subject<void>();

  // 3. Constructeur (injection de dépendances)
  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
  ) {}

  // 4. Lifecycle hooks
  ngOnInit(): void {
    this.loadPost();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // 5. Méthodes publiques
  loadPost(): void {
    // ...
  }

  onSubmitComment(content: string): void {
    // ...
  }

  // 6. Méthodes privées
  private handleError(error: any): void {
    // ...
  }
}
```

#### RxJS et Observables

```typescript
// ✅ Bonne pratique: Unsubscribe automatique
export class FeedComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.postService
      .getAllPosts()
      .pipe(takeUntil(this.destroy$))
      .subscribe((posts) => (this.posts = posts));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

// ✅ Alternative: async pipe (unsubscribe automatique)
export class FeedComponent {
  posts$ = this.postService.getAllPosts();
}
```

```html
<!-- Template avec async pipe -->
<div *ngIf="posts$ | async as posts">
  <app-post-card
    *ngFor="let post of posts"
    [post]="post"
  ></app-post-card>
</div>
```

#### Tests Angular

```typescript
describe("PostDetailComponent", () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postService: jasmine.SpyObj<PostService>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj("PostService", ["getPost"]);

    await TestBed.configureTestingModule({
      declarations: [PostDetailComponent],
      providers: [{ provide: PostService, useValue: postServiceSpy }],
    }).compileComponents();

    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
  });

  it("should load post on init", () => {
    const mockPost: Post = {
      id: 1,
      title: "Test Post",
      // ...
    };
    postService.getPost.and.returnValue(of(mockPost));

    component.ngOnInit();

    expect(component.post).toEqual(mockPost);
    expect(postService.getPost).toHaveBeenCalledWith(1);
  });
});
```

#### Style SCSS

```scss
// Variables
$primary-color: #3f51b5;
$font-size-base: 16px;

// Nesting limité (max 3 niveaux)
.post-detail {
  padding: 20px;

  &__header {
    margin-bottom: 16px;

    &-title {
      font-size: 24px;
      font-weight: bold;
    }
  }

  &__content {
    line-height: 1.6;
  }
}
```

---

## Documentation API

### Base URL

```
http://localhost:8080/api
```

### Format des réponses

Toutes les réponses sont au format JSON.

---

## Authentification

### Inscription

**Endpoint**: `POST /api/auth/register`

**Description**: Créer un nouveau compte utilisateur

**Body**:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Réponse réussie** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "john@example.com",
  "name": "John Doe"
}
```

**Erreurs possibles**:

- `400 Bad Request`: Email déjà utilisé
- `400 Bad Request`: Données invalides (email mal formaté, mot de passe trop court)

---

### Connexion

**Endpoint**: `POST /api/auth/login`

**Description**: Se connecter avec email et mot de passe

**Body**:

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Réponse réussie** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "john@example.com",
  "name": "John Doe"
}
```

**Erreurs possibles**:

- `401 Unauthorized`: Email ou mot de passe incorrect
- `400 Bad Request`: Données manquantes

---

### Obtenir le profil actuel

**Endpoint**: `GET /api/auth/me`

**Description**: Récupérer les informations de l'utilisateur connecté

**Headers requis**:

```
Authorization: Bearer <token>
```

**Réponse réussie** (200 OK):

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Erreurs possibles**:

- `401 Unauthorized`: Token manquant ou invalide

---

### Mettre à jour le profil

**Endpoint**: `PUT /api/auth/me`

**Description**: Modifier les informations du profil utilisateur

**Headers requis**:

```
Authorization: Bearer <token>
```

**Body**:

```json
{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "password": "newpassword123"
}
```

**Note**: Tous les champs sont optionnels. Seuls les champs fournis seront mis à jour.

**Réponse réussie** (200 OK):

```json
{
  "id": 1,
  "name": "John Smith",
  "email": "john.smith@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:25:00"
}
```

**Erreurs possibles**:

- `400 Bad Request`: Email déjà utilisé par un autre compte
- `401 Unauthorized`: Token manquant ou invalide

---

### Déconnexion

**Endpoint**: `POST /api/auth/logout`

**Description**: Déconnecter l'utilisateur

**Headers requis**:

```
Authorization: Bearer <token>
```

**Réponse réussie** (200 OK):

```json
{
  "message": "Déconnexion réussie"
}
```

---

## Thèmes

### Lister tous les thèmes

**Endpoint**: `GET /api/topics`

**Description**: Récupérer la liste de tous les thèmes disponibles

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "name": "Java",
    "description": "Tout sur le langage Java",
    "createdAt": "2024-01-10T09:00:00",
    "updatedAt": "2024-01-10T09:00:00"
  },
  {
    "id": 2,
    "name": "JavaScript",
    "description": "Le monde du JavaScript et de ses frameworks",
    "createdAt": "2024-01-10T09:15:00",
    "updatedAt": "2024-01-10T09:15:00"
  }
]
```

---

### Obtenir un thème

**Endpoint**: `GET /api/topics/{id}`

**Description**: Récupérer les détails d'un thème spécifique

**Paramètres URL**:

- `id` (number): ID du thème

**Réponse réussie** (200 OK):

```json
{
  "id": 1,
  "name": "Java",
  "description": "Tout sur le langage Java",
  "createdAt": "2024-01-10T09:00:00",
  "updatedAt": "2024-01-10T09:00:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Thème non trouvé

---

### Créer un thème

**Endpoint**: `POST /api/topics`

**Description**: Créer un nouveau thème (admin uniquement)

**Headers requis**:

```
Authorization: Bearer <token>
```

**Body**:

```json
{
  "name": "Python",
  "description": "Programmation Python et ses applications"
}
```

**Réponse réussie** (201 Created):

```json
{
  "id": 3,
  "name": "Python",
  "description": "Programmation Python et ses applications",
  "createdAt": "2024-01-20T15:30:00",
  "updatedAt": "2024-01-20T15:30:00"
}
```

**Erreurs possibles**:

- `400 Bad Request`: Un thème avec ce nom existe déjà
- `401 Unauthorized`: Non authentifié

---

### Modifier un thème

**Endpoint**: `PUT /api/topics/{id}`

**Description**: Modifier un thème existant (admin uniquement)

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID du thème

**Body**:

```json
{
  "name": "Python 3",
  "description": "Python moderne et ses frameworks"
}
```

**Réponse réussie** (200 OK):

```json
{
  "id": 3,
  "name": "Python 3",
  "description": "Python moderne et ses frameworks",
  "createdAt": "2024-01-20T15:30:00",
  "updatedAt": "2024-01-21T10:15:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Thème non trouvé
- `400 Bad Request`: Le nom du thème existe déjà

---

### Supprimer un thème

**Endpoint**: `DELETE /api/topics/{id}`

**Description**: Supprimer un thème (admin uniquement)

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID du thème

**Réponse réussie** (200 OK):

```json
{
  "message": "Thème supprimé avec succès"
}
```

**Erreurs possibles**:

- `404 Not Found`: Thème non trouvé

---

### S'abonner à un thème

**Endpoint**: `POST /api/topics/{id}/subscribe`

**Description**: S'abonner à un thème pour voir ses articles dans le fil d'actualité

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID du thème

**Réponse réussie** (200 OK):

```json
{
  "message": "Abonnement réussi"
}
```

**Erreurs possibles**:

- `404 Not Found`: Thème non trouvé
- `400 Bad Request`: Déjà abonné à ce thème
- `401 Unauthorized`: Non authentifié

---

### Se désabonner d'un thème

**Endpoint**: `DELETE /api/topics/{id}/subscribe`

**Description**: Se désabonner d'un thème

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID du thème

**Réponse réussie** (200 OK):

```json
{
  "message": "Désabonnement réussi"
}
```

**Erreurs possibles**:

- `404 Not Found`: Thème non trouvé
- `400 Bad Request`: Pas abonné à ce thème

---

### Lister mes abonnements

**Endpoint**: `GET /api/topics/subscriptions`

**Description**: Récupérer la liste des thèmes auxquels l'utilisateur est abonné

**Headers requis**:

```
Authorization: Bearer <token>
```

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "name": "Java",
    "description": "Tout sur le langage Java",
    "createdAt": "2024-01-10T09:00:00",
    "updatedAt": "2024-01-10T09:00:00"
  }
]
```

---

## Articles

### Lister tous les articles

**Endpoint**: `GET /api/posts`

**Description**: Récupérer tous les articles (filtrés par abonnements pour utilisateurs connectés)

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "title": "Introduction à Spring Boot",
    "content": "Spring Boot facilite la création d'applications...",
    "authorId": 2,
    "authorName": "Jane Developer",
    "topicIds": [1],
    "topicNames": ["Java"],
    "createdAt": "2024-01-15T14:30:00",
    "updatedAt": "2024-01-15T14:30:00"
  }
]
```

---

### Obtenir un article

**Endpoint**: `GET /api/posts/{id}`

**Description**: Récupérer les détails d'un article spécifique

**Paramètres URL**:

- `id` (number): ID de l'article

**Réponse réussie** (200 OK):

```json
{
  "id": 1,
  "title": "Introduction à Spring Boot",
  "content": "Spring Boot facilite la création d'applications Spring...",
  "authorId": 2,
  "authorName": "Jane Developer",
  "topicIds": [1],
  "topicNames": ["Java"],
  "createdAt": "2024-01-15T14:30:00",
  "updatedAt": "2024-01-15T14:30:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Article non trouvé

---

### Créer un article

**Endpoint**: `POST /api/posts`

**Description**: Publier un nouvel article

**Headers requis**:

```
Authorization: Bearer <token>
```

**Body**:

```json
{
  "title": "Les nouveautés Java 21",
  "content": "Java 21 apporte de nombreuses améliorations...",
  "topicIds": [1, 2]
}
```

**Réponse réussie** (201 Created):

```json
{
  "id": 5,
  "title": "Les nouveautés Java 21",
  "content": "Java 21 apporte de nombreuses améliorations...",
  "authorId": 1,
  "authorName": "John Doe",
  "topicIds": [1, 2],
  "topicNames": ["Java", "Programmation"],
  "createdAt": "2024-01-20T16:45:00",
  "updatedAt": "2024-01-20T16:45:00"
}
```

**Erreurs possibles**:

- `400 Bad Request`: Aucun thème valide fourni
- `401 Unauthorized`: Non authentifié

---

### Modifier un article

**Endpoint**: `PUT /api/posts/{id}`

**Description**: Modifier un article existant (auteur uniquement)

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID de l'article

**Body**:

```json
{
  "title": "Les nouveautés Java 21 - Mise à jour",
  "content": "Contenu mis à jour...",
  "topicIds": [1]
}
```

**Réponse réussie** (200 OK):

```json
{
  "id": 5,
  "title": "Les nouveautés Java 21 - Mise à jour",
  "content": "Contenu mis à jour...",
  "authorId": 1,
  "authorName": "John Doe",
  "topicIds": [1],
  "topicNames": ["Java"],
  "createdAt": "2024-01-20T16:45:00",
  "updatedAt": "2024-01-21T09:20:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Article non trouvé
- `403 Forbidden`: Vous n'êtes pas l'auteur de cet article
- `400 Bad Request`: Aucun thème valide fourni

---

### Supprimer un article

**Endpoint**: `DELETE /api/posts/{id}`

**Description**: Supprimer un article (auteur uniquement)

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `id` (number): ID de l'article

**Réponse réussie** (200 OK):

```json
{
  "message": "Article supprimé avec succès"
}
```

**Erreurs possibles**:

- `404 Not Found`: Article non trouvé
- `403 Forbidden`: Vous n'êtes pas l'auteur de cet article

---

### Articles par auteur

**Endpoint**: `GET /api/posts/author/{authorId}`

**Description**: Récupérer tous les articles d'un auteur spécifique

**Paramètres URL**:

- `authorId` (number): ID de l'auteur

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "title": "Introduction à Spring Boot",
    "content": "Spring Boot facilite...",
    "authorId": 2,
    "authorName": "Jane Developer",
    "topicIds": [1],
    "topicNames": ["Java"],
    "createdAt": "2024-01-15T14:30:00",
    "updatedAt": "2024-01-15T14:30:00"
  }
]
```

---

### Articles par thème

**Endpoint**: `GET /api/posts/topic/{topicId}`

**Description**: Récupérer tous les articles associés à un thème

**Paramètres URL**:

- `topicId` (number): ID du thème

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "title": "Introduction à Spring Boot",
    "content": "Spring Boot facilite...",
    "authorId": 2,
    "authorName": "Jane Developer",
    "topicIds": [1],
    "topicNames": ["Java"],
    "createdAt": "2024-01-15T14:30:00",
    "updatedAt": "2024-01-15T14:30:00"
  }
]
```

---

## Commentaires

### Lister les commentaires d'un article

**Endpoint**: `GET /api/posts/{postId}/comments`

**Description**: Récupérer tous les commentaires d'un article

**Paramètres URL**:

- `postId` (number): ID de l'article

**Réponse réussie** (200 OK):

```json
[
  {
    "id": 1,
    "content": "Excellent article !",
    "authorId": 3,
    "authorName": "Bob Reviewer",
    "postId": 1,
    "createdAt": "2024-01-15T15:00:00"
  },
  {
    "id": 2,
    "content": "Merci pour ces explications claires",
    "authorId": 4,
    "authorName": "Alice Reader",
    "postId": 1,
    "createdAt": "2024-01-15T16:30:00"
  }
]
```

**Erreurs possibles**:

- `404 Not Found`: Article non trouvé

---

### Créer un commentaire

**Endpoint**: `POST /api/posts/{postId}/comments`

**Description**: Ajouter un commentaire à un article

**Headers requis**:

```
Authorization: Bearer <token>
```

**Paramètres URL**:

- `postId` (number): ID de l'article

**Body**:

```json
{
  "content": "Très intéressant, merci !"
}
```

**Réponse réussie** (201 Created):

```json
{
  "id": 5,
  "content": "Très intéressant, merci !",
  "authorId": 1,
  "authorName": "John Doe",
  "postId": 1,
  "createdAt": "2024-01-20T17:15:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Article non trouvé
- `400 Bad Request`: Contenu du commentaire manquant
- `401 Unauthorized`: Non authentifié

---

## Utilisateurs

### Obtenir un utilisateur

**Endpoint**: `GET /api/user/{id}`

**Description**: Récupérer les informations publiques d'un utilisateur

**Paramètres URL**:

- `id` (number): ID de l'utilisateur

**Réponse réussie** (200 OK):

```json
{
  "id": 2,
  "name": "Jane Developer",
  "email": "jane@example.com",
  "createdAt": "2024-01-10T08:00:00",
  "updatedAt": "2024-01-10T08:00:00"
}
```

**Erreurs possibles**:

- `404 Not Found`: Utilisateur non trouvé

---

## Architecture

### Architecture Backend

L'application backend suit une architecture en couches (Layered Architecture) respectant les principes SOLID et les bonnes pratiques Spring Boot.

#### Structure en couches

```
back/
├── src/main/java/com/openclassrooms/mddapi/
│   ├── controllers/       # Couche présentation (API REST)
│   │   ├── AuthController.java
│   │   ├── TopicController.java
│   │   ├── PostController.java
│   │   ├── CommentController.java
│   │   └── UserController.java
│   │
│   ├── services/          # Couche métier (Business Logic)
│   │   ├── TopicService.java
│   │   ├── PostService.java
│   │   ├── CommentService.java
│   │   ├── UserService.java
│   │   └── auth/
│   │       ├── RegisterService.java
│   │       ├── LoginService.java
│   │       └── CurrentUserService.java
│   │
│   ├── repositories/      # Couche accès données (JPA)
│   │   ├── TopicRepository.java
│   │   ├── PostRepository.java
│   │   ├── CommentRepository.java
│   │   └── UserRepository.java
│   │
│   ├── entities/          # Modèles de données
│   │   ├── Topic.java
│   │   ├── Post.java
│   │   ├── Comment.java
│   │   └── User.java
│   │
│   ├── dto/               # Data Transfer Objects
│   │   ├── requests/      # DTOs pour les requêtes
│   │   └── responses/     # DTOs pour les réponses
│   │
│   ├── config/            # Configuration Spring
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java
│   │   └── OpenApiConfig.java
│   │
│   ├── filter/            # Filtres de sécurité
│   │   └── JwtAuthenticationFilter.java
│   │
│   ├── util/              # Utilitaires
│   │   └── JwtUtils.java
│   │
│   └── exceptions/        # Gestion des exceptions
│       ├── ResourceNotFoundException.java
│       ├── BadRequestException.java
│       └── UnauthorizedException.java
```

#### Flux de données

1. **Requête HTTP** → `Controller` (validation des entrées)
2. `Controller` → `Service` (logique métier)
3. `Service` → `Repository` (accès base de données)
4. `Repository` → **Base de données MySQL**
5. Retour avec transformation en **DTO** pour la réponse

#### Technologies clés

- **Spring Boot 3.2.0**: Framework principal
- **Spring Security**: Authentification et autorisation
- **Spring Data JPA**: ORM et accès données
- **Hibernate**: Implémentation JPA
- **JWT (jjwt 0.12.3)**: Tokens d'authentification
- **MySQL Connector**: Driver JDBC
- **Lombok**: Réduction du code boilerplate
- **OpenAPI/Swagger**: Documentation API

---

### Architecture Frontend

L'application frontend suit l'architecture Angular avec une séparation claire des responsabilités.

#### Structure des composants

```
front/src/app/
├── components/            # Composants réutilisables
│   ├── header/           # En-tête de navigation
│   ├── topic-card/       # Carte d'affichage de thème
│   ├── post-card/        # Carte d'affichage d'article
│   └── comment-item/     # Élément de commentaire
│
├── pages/                # Pages de l'application
│   ├── home/            # Page d'accueil
│   ├── auth/            # Pages d'authentification
│   │   ├── login/
│   │   └── register/
│   ├── feed/            # Fil d'actualité
│   ├── topics/          # Liste des thèmes
│   ├── post-detail/     # Détail d'un article
│   ├── create-post/     # Création d'article
│   └── profile/         # Profil utilisateur
│
├── services/             # Services Angular
│   ├── auth.service.ts  # Gestion authentification
│   ├── topic.service.ts # Gestion des thèmes
│   ├── post.service.ts  # Gestion des articles
│   └── comment.service.ts # Gestion des commentaires
│
├── guards/              # Guards de navigation
│   └── auth.guard.ts   # Protection des routes
│
├── interceptors/        # Intercepteurs HTTP
│   └── jwt.interceptor.ts # Injection du token JWT
│
├── models/              # Interfaces TypeScript
│   ├── user.model.ts
│   ├── topic.model.ts
│   ├── post.model.ts
│   └── comment.model.ts
│
└── shared/              # Modules partagés
    └── material.module.ts # Imports Angular Material
```

#### Flux de données Angular

1. **Composant** déclenche une action utilisateur
2. **Service** effectue l'appel HTTP via HttpClient
3. **Interceptor** ajoute le token JWT automatiquement
4. **Backend API** traite la requête
5. **Service** reçoit la réponse (Observable)
6. **Composant** met à jour la vue avec les données

#### Technologies clés

- **Angular 14.1.3**: Framework SPA
- **Angular Material**: Composants UI
- **RxJS**: Programmation réactive
- **TypeScript**: Typage statique
- **SCSS**: Styles avancés
- **Angular Router**: Navigation
- **HttpClient**: Communication HTTP

---

### Sécurité des données

#### Backend - Sécurité multicouche

##### 1. Authentification JWT

```java
// Génération du token avec informations utilisateur
String jwt = jwtUtils.generateTokenFromUsername(user.getEmail());

// Token contient:
// - Subject: Email utilisateur
// - Expiration: 24 heures
// - Signature: HMAC-SHA256
```

**Sécurisation**:

- Secret JWT stocké dans `application.properties` (min 256 bits)
- Token signé avec HMAC-SHA256
- Expiration automatique après 24h
- Token validé à chaque requête

##### 2. Hashage des mots de passe

```java
// Utilisation de BCrypt via Spring Security
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Encodage lors de l'inscription
user.setPassword(passwordEncoder.encode(rawPassword));
```

**Sécurisation**:

- BCrypt avec salt automatique
- Coût de calcul élevé (protection contre brute-force)
- Pas de stockage en clair
- Comparaison sécurisée avec `matches()`

##### 3. Protection CSRF et CORS

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().disable()  // Désactivé pour API REST
            .cors()            // CORS configuré
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated();
    }
}
```

##### 4. Validation des entrées

```java
// Validation avec Bean Validation
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, max = 100)
    private String password;
}
```

##### 5. Gestion des exceptions sécurisée

```java
// Pas d'exposition de stacktraces en production
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleException(Exception ex) {
    // Log détaillé côté serveur
    logger.error("Error occurred", ex);

    // Message générique côté client
    return ResponseEntity.status(500)
        .body("Une erreur est survenue");
}
```

#### Frontend - Sécurité côté client

##### 1. Stockage sécurisé du token

```typescript
// LocalStorage avec token JWT
localStorage.setItem("token", response.token);

// Lecture et validation
const token = localStorage.getItem("token");
if (token && !this.isTokenExpired(token)) {
  // Utiliser le token
}
```

**Limitations du localStorage**:

- Vulnérable aux attaques XSS
- Accessible via JavaScript
- Alternative: HttpOnly cookies (plus sécurisé)

##### 2. Intercepteur JWT

```typescript
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem("token");
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }
    return next.handle(req);
  }
}
```

##### 3. Guards de navigation

```typescript
@Injectable()
export class AuthGuard implements CanActivate {
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    this.router.navigate(["/login"]);
    return false;
  }
}
```

##### 4. Sanitization des données

```typescript
// Angular sanitize automatiquement dans les templates
// Évite les injections XSS
<div [innerHTML]="sanitizedContent"></div>

// Pour le HTML dynamique
constructor(private sanitizer: DomSanitizer) {}
sanitizedContent = this.sanitizer.sanitize(
  SecurityContext.HTML,
  userInput
);
```

#### Base de données - Sécurité MySQL

##### 1. Connexion sécurisée

```properties
# Credentials en variables d'environnement (production)
spring.datasource.url=jdbc:mysql://${DB_HOST}:3306/mdd_db?useSSL=true
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

##### 2. Protection contre SQL Injection

```java
// JPA utilise des PreparedStatements automatiquement
@Query("SELECT p FROM Post p WHERE p.author.id = :authorId")
List<Post> findByAuthorId(@Param("authorId") Long authorId);

// Les paramètres sont échappés automatiquement
```

##### 3. Principe du moindre privilège

```sql
-- Utilisateur avec droits limités
CREATE USER 'mdd_app'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON mdd_db.* TO 'mdd_app'@'localhost';
-- Pas de DROP, CREATE, ALTER en production
```

#### Checklist de sécurité

- ✅ Authentification JWT avec expiration
- ✅ Mots de passe hashés avec BCrypt
- ✅ Validation des entrées (Bean Validation)
- ✅ Protection CSRF désactivée (API REST)
- ✅ CORS configuré
- ✅ HTTPS recommandé en production
- ✅ Secrets dans variables d'environnement
- ✅ Logs sans données sensibles
- ✅ Guards de navigation Angular
- ✅ Intercepteur JWT automatique
- ✅ SQL Injection protégé (JPA)
- ✅ XSS protégé (Angular sanitization)

---

## Tests

### Backend - Tests unitaires avec JUnit 5 et Mockito

Le projet contient **79 tests unitaires** avec une couverture de **77%**.

#### Architecture des tests

Les tests sont organisés selon l'architecture de l'application :

```
back/src/test/java/com/openclassrooms/mddapi/
├── controllers/          # Tests des endpoints REST
│   ├── AuthControllerTest.java       (6 tests)
│   ├── TopicControllerTest.java      (8 tests)
│   ├── PostControllerTest.java       (7 tests)
│   ├── CommentControllerTest.java    (3 tests)
│   └── UserControllerTest.java       (2 tests)
├── services/            # Tests de la logique métier
│   ├── TopicServiceTest.java         (18 tests)
│   ├── PostServiceTest.java          (15 tests)
│   ├── CommentServiceTest.java       (5 tests)
│   ├── UserServiceTest.java          (8 tests)
│   ├── RegisterServiceTest.java      (2 tests)
│   ├── LoginServiceTest.java         (1 test)
│   └── CurrentUserServiceTest.java   (3 tests)
```

#### Exécuter les tests

```bash
cd back

# Exécuter tous les tests
mvn test

# Exécuter une classe de test spécifique
mvn test -Dtest=TopicServiceTest

# Exécuter un test spécifique
mvn test -Dtest=TopicServiceTest#getAllTopics_ShouldReturnAllTopics

# Mode verbose (afficher les logs)
mvn test -X
```

#### Générer le rapport de couverture JaCoCo

```bash
cd back

# Nettoyer + tester + générer le rapport
mvn clean test

# Le rapport HTML est généré dans :
# back/target/site/jacoco/index.html
```

**Ouvrir le rapport dans le navigateur**:

```bash
# Windows
start back/target/site/jacoco/index.html

# Linux/Mac
open back/target/site/jacoco/index.html
```

#### Interpréter le rapport JaCoCo

Le rapport affiche plusieurs métriques de couverture :

| Métrique         | Description                | Objectif |
| ---------------- | -------------------------- | -------- |
| **Instructions** | Bytecode Java exécuté      | > 80%    |
| **Branches**     | Conditions if/else testées | > 75%    |
| **Lines**        | Lignes de code exécutées   | > 80%    |
| **Methods**      | Méthodes testées           | > 85%    |
| **Classes**      | Classes testées            | > 90%    |

**Couverture actuelle du projet**:

- **Total**: 77% ✅
- **Controllers**: 100% ✅
- **Services**: 96% ✅
- **Auth Services**: 100% ✅
- **Repositories**: 0% (interfaces JPA, pas de tests nécessaires)
- **Entities**: ~30% (getters/setters, peu d'intérêt à tester)

#### Structure d'un test type

```java
@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    // 1. Mocks des dépendances
    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    // 2. Classe testée (avec injection des mocks)
    @InjectMocks
    private TopicService topicService;

    // 3. Test avec pattern Given-When-Then
    @Test
    void getAllTopics_ShouldReturnAllTopics() {
        // Given (Arrange)
        List<Topic> topics = Arrays.asList(
            new Topic("Java", "Java programming"),
            new Topic("Python", "Python programming")
        );
        when(topicRepository.findAll()).thenReturn(topics);

        // When (Act)
        List<TopicResponse> result = topicService.getAllTopics();

        // Then (Assert)
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        verify(topicRepository, times(1)).findAll();
    }
}
```

#### Conventions de nommage des tests

```java
// Pattern: methodName_condition_expectedResult

getAllTopics_ShouldReturnAllTopics()
getTopicById_WhenTopicExists_ShouldReturnTopic()
getTopicById_WhenTopicNotFound_ShouldThrowException()
createTopic_WhenNameAlreadyExists_ShouldThrowBadRequestException()
subscribeToTopic_WhenAlreadySubscribed_ShouldThrowBadRequestException()
```

#### Techniques de test Mockito

**1. Mock d'un repository**

```java
@Mock
private TopicRepository topicRepository;

when(topicRepository.findById(1L))
    .thenReturn(Optional.of(new Topic("Java", "Description")));
```

**2. Vérification d'appel**

```java
verify(topicRepository, times(1)).save(any(Topic.class));
verify(topicRepository, never()).delete(any());
```

**3. Test d'exception**

```java
@Test
void getTopicById_WhenNotFound_ShouldThrowException() {
    when(topicRepository.findById(999L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> topicService.getTopicById(999L));
}
```

**4. ArgumentCaptor (capturer les arguments passés)**

```java
@Captor
private ArgumentCaptor<Topic> topicCaptor;

// Dans le test
verify(topicRepository).save(topicCaptor.capture());
Topic savedTopic = topicCaptor.getValue();
assertEquals("Java", savedTopic.getName());
```

#### Résoudre les problèmes courants

**1. Erreur Java 25 / Byte Buddy**

```
Caused by: java.lang.UnsupportedOperationException: class file version 69.0
```

✅ **Solution**: Déjà configuré dans [pom.xml](back/pom.xml#L180-L185) avec `-Dnet.bytebuddy.experimental=true`

**2. NullPointerException dans les tests**

```java
// ❌ Mauvais
@InjectMocks
private TopicService topicService; // Les mocks ne sont pas injectés

// ✅ Bon
@ExtendWith(MockitoExtension.class) // Ajouter cette annotation
class TopicServiceTest {
    @InjectMocks
    private TopicService topicService;
}
```

**3. Test échoue car le mock ne retourne rien**

```java
// ❌ Mauvais
when(userRepository.save(any())).thenReturn(null);

// ✅ Bon
User user = new User("test@test.com", "hashedPassword", "Test User");
when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
    User saved = invocation.getArgument(0);
    saved.setId(1L); // Simuler l'ID auto-généré
    return saved;
});
```

---

### Frontend - Tests unitaires Angular avec Jest

#### Exécuter les tests

```bash
cd front

# Exécuter tous les tests avec Jest
npm test

# Exécuter les tests en mode watch
npm run test:watch

# Exécuter les tests avec couverture
npm run test:coverage

# Le rapport de couverture est généré dans : front/coverage/lcov-report/index.html
```

#### Structure d'un test Angular avec Jest

```typescript
describe("PostDetailComponent", () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postService: jest.Mocked<PostService>;
  let commentService: jest.Mocked<CommentService>;

  beforeEach(async () => {
    // 1. Créer des mocks Jest pour les services
    const postServiceMock = {
      getPost: jest.fn(),
      getAllPosts: jest.fn(),
      createPost: jest.fn(),
    };
    const commentServiceMock = {
      getComments: jest.fn(),
      addComment: jest.fn(),
    };

    // 2. Configurer le TestBed
    await TestBed.configureTestingModule({
      declarations: [PostDetailComponent],
      imports: [HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: CommentService, useValue: commentServiceMock },
      ],
    }).compileComponents();

    // 3. Récupérer les instances
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jest.Mocked<PostService>;
    commentService = TestBed.inject(
      CommentService,
    ) as jest.Mocked<CommentService>;
  });

  it("should load post on init", () => {
    // Given
    const mockPost: Post = {
      id: 1,
      title: "Test Post",
      content: "Test content",
      authorId: 1,
      authorName: "John Doe",
      topicIds: [1],
      topicNames: ["Java"],
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    postService.getPost.mockReturnValue(of(mockPost));

    // When
    component.ngOnInit();

    // Then
    expect(component.post).toEqual(mockPost);
    expect(postService.getPost).toHaveBeenCalledWith(1);
  });
});
```

#### Tester les Observables RxJS avec Jest

```typescript
it("should handle error when loading post", () => {
  // Given
  const errorResponse = new HttpErrorResponse({
    error: "Not found",
    status: 404,
  });
  postService.getPost.mockReturnValue(throwError(() => errorResponse));

  // When
  component.ngOnInit();

  // Then
  expect(component.error).toBe("Article non trouvé");
  expect(component.loading).toBe(false);
});

// Tester les appels multiples
it("should call service multiple times", () => {
  postService.getPost.mockReturnValue(of(mockPost));

  component.loadPost();
  component.loadPost();

  expect(postService.getPost).toHaveBeenCalledTimes(2);
});
```

#### Tester les formulaires réactifs

```typescript
it("should validate comment form", () => {
  // Given
  component.commentForm = new FormGroup({
    content: new FormControl("", [
      Validators.required,
      Validators.minLength(3),
    ]),
  });

  // When
  component.commentForm.patchValue({ content: "ab" }); // Trop court

  // Then
  expect(component.commentForm.valid).toBe(false);
  expect(
    component.commentForm.get("content")?.errors?.["minlength"],
  ).toBeTruthy();

  // When
  component.commentForm.patchValue({ content: "Valid comment" });

  // Then
  expect(component.commentForm.valid).toBe(true);
});
```

---

## FAQ Utilisateur

### Cas d'usage courants

#### 1. Comment créer un compte ?

1. Accédez à la page d'inscription
2. Remplissez le formulaire avec :
   - Votre nom (minimum 2 caractères)
   - Votre email (format valide requis)
   - Un mot de passe (minimum 6 caractères)
3. Cliquez sur "S'inscrire"
4. Vous êtes automatiquement connecté après l'inscription

**Note**: Conservez votre token JWT qui est stocké automatiquement dans le localStorage.

---

#### 2. Comment s'abonner à des thèmes ?

1. Connectez-vous à votre compte
2. Accédez à la page "Thèmes"
3. Parcourez la liste des thèmes disponibles
4. Cliquez sur le bouton "S'abonner" pour chaque thème qui vous intéresse
5. Les articles de ces thèmes apparaîtront dans votre fil d'actualité

**Astuce**: Vous pouvez vous abonner à plusieurs thèmes pour personnaliser votre expérience.

---

#### 3. Comment publier un article ?

1. Connectez-vous à votre compte
2. Accédez à la page "Créer un article"
3. Remplissez les champs :
   - **Titre**: Titre accrocheur et descriptif
   - **Contenu**: Corps de l'article (Markdown supporté)
   - **Thèmes**: Sélectionnez au moins un thème (plusieurs possibles)
4. Cliquez sur "Publier"

**Important**: Vous devez être abonné à au moins un thème pour publier.

---

#### 4. Comment modifier mon profil ?

1. Connectez-vous à votre compte
2. Accédez à la page "Profil" ou "Mon compte"
3. Modifiez les informations souhaitées :
   - Nom
   - Email
   - Mot de passe (optionnel)
4. Cliquez sur "Enregistrer"

**Note**: Si vous changez d'email, assurez-vous qu'il n'est pas déjà utilisé par un autre compte.

---

#### 5. Comment commenter un article ?

1. Connectez-vous à votre compte
2. Ouvrez un article en cliquant dessus
3. Faites défiler jusqu'à la section commentaires
4. Rédigez votre commentaire dans le champ prévu
5. Cliquez sur "Publier le commentaire"

**Les commentaires sont triés par ordre chronologique.**

---

### Erreurs courantes et solutions

#### ❌ Erreur 401 : "Unauthorized"

**Cause**: Votre session a expiré ou vous n'êtes pas authentifié.

**Solutions**:

- Reconnectez-vous à votre compte
- Vérifiez que le token JWT est présent dans le localStorage
- Si le problème persiste, déconnectez-vous complètement et reconnectez-vous

---

#### ❌ Erreur 400 : "Email is already taken"

**Cause**: L'email que vous essayez d'utiliser est déjà associé à un compte existant.

**Solutions**:

- Utilisez une adresse email différente
- Si c'est votre email, essayez de vous connecter avec celui-ci
- Utilisez la fonction "Mot de passe oublié" si nécessaire

---

#### ❌ Erreur 400 : "User is already subscribed to this topic"

**Cause**: Vous essayez de vous abonner à un thème auquel vous êtes déjà abonné.

**Solutions**:

- Vérifiez votre liste d'abonnements
- Si vous voulez vous désabonner, utilisez le bouton "Se désabonner"

---

#### ❌ Erreur 403 : "You are not authorized to update/delete this post"

**Cause**: Vous essayez de modifier ou supprimer un article que vous n'avez pas créé.

**Solutions**:

- Seul l'auteur d'un article peut le modifier ou le supprimer
- Vérifiez que vous êtes connecté avec le bon compte
- Contactez l'auteur si vous souhaitez demander une modification

---

#### ❌ Erreur 404 : "Topic/Post/User not found"

**Cause**: La ressource demandée n'existe pas ou a été supprimée.

**Solutions**:

- Vérifiez l'URL et l'ID de la ressource
- Actualisez la page pour voir les données les plus récentes
- Si la ressource a été supprimée, elle n'est plus accessible

---

#### ❌ "No valid topics found" lors de la création d'article

**Cause**: Les IDs de thèmes fournis n'existent pas dans la base de données.

**Solutions**:

- Vérifiez que vous avez sélectionné des thèmes valides
- Actualisez la liste des thèmes disponibles
- Sélectionnez au moins un thème existant

---

#### ❌ Le fil d'actualité est vide

**Cause**: Vous n'êtes abonné à aucun thème ou aucun article n'a été publié dans vos thèmes.

**Solutions**:

- Abonnez-vous à au moins un thème depuis la page "Thèmes"
- Attendez que des articles soient publiés dans vos thèmes
- Créez vous-même du contenu !

---

#### ❌ "Password must be between 6 and 100 characters"

**Cause**: Le mot de passe ne respecte pas les critères de sécurité.

**Solutions**:

- Utilisez au moins 6 caractères
- Ne dépassez pas 100 caractères
- Combinez lettres, chiffres et caractères spéciaux pour plus de sécurité

---

#### ❌ "Email should be valid"

**Cause**: Le format de l'email n'est pas valide.

**Solutions**:

- Vérifiez que votre email contient un @ et un domaine valide
- Exemple de format valide : `utilisateur@domaine.com`
- Supprimez les espaces avant ou après l'email

---

#### ❌ Problème de connexion à la base de données

**Cause**: La base de données MySQL n'est pas accessible.

**Solutions** (pour les développeurs):

```bash
# Vérifier que MySQL est démarré
sudo service mysql status

# Démarrer MySQL si nécessaire
sudo service mysql start

# Vérifier les credentials dans application.properties
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

---

#### ❌ Token JWT invalide ou expiré

**Cause**: Le token JWT a expiré (durée de vie : 24h) ou est corrompu.

**Solutions**:

- Déconnectez-vous et reconnectez-vous
- Videz le cache du navigateur
- Si le problème persiste, supprimez les données du localStorage :
  ```javascript
  localStorage.clear();
  ```

---

### Bonnes pratiques

#### 📝 Rédaction d'articles

- **Titre clair**: Utilisez un titre descriptif et accrocheur
- **Structure**: Organisez votre contenu avec des paragraphes et des titres
- **Thèmes appropriés**: Sélectionnez les thèmes pertinents pour votre article
- **Relecture**: Vérifiez l'orthographe et la grammaire avant de publier

---

#### 💬 Commentaires

- **Restez courtois**: Soyez respectueux envers les autres utilisateurs
- **Soyez constructif**: Proposez des suggestions plutôt que des critiques
- **Pertinence**: Assurez-vous que votre commentaire apporte de la valeur
- **Pas de spam**: Évitez les commentaires répétitifs ou hors-sujet

---

#### 🔐 Sécurité

- **Mot de passe fort**: Utilisez un mot de passe unique et complexe
- **Ne partagez pas votre compte**: Gardez vos identifiants confidentiels
- **Déconnexion**: Déconnectez-vous sur les ordinateurs partagés
- **Mise à jour email**: Gardez votre email à jour pour récupérer votre compte

---

### Support technique

#### Problème non résolu ?

1. **Vérifiez la console du navigateur** (F12) pour les erreurs JavaScript
2. **Consultez les logs du backend** si vous êtes développeur
3. **Testez dans un autre navigateur** pour éliminer les problèmes locaux
4. **Videz le cache** : Ctrl+F5 ou Cmd+Shift+R

#### Pour les développeurs

**Logs backend**:

```bash
cd back
mvn spring-boot:run
# Les logs s'affichent dans le terminal
```

**Tests frontend**:

```bash
cd front
npm test
# Exécute les tests et affiche les résultats
```

**Base de données**:

```sql
-- Vérifier les données
USE mdd_db;
SELECT * FROM users;
SELECT * FROM topics;
SELECT * FROM posts;
```

---

### Ressources supplémentaires

- **Angular Material**: https://material.angular.io/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **JWT**: https://jwt.io/
- **MySQL**: https://dev.mysql.com/doc/

---

## Licence

Ce projet est développé dans le cadre d'une formation OpenClassrooms.
