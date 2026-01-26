-- Insert 10 different articles with various topics
-- Assuming user_id 1 exists and topics with IDs 1-10 exist

-- Article 1: JavaScript + React
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Getting Started with React Hooks', 'React Hooks revolutionized the way we write React components. In this article, we explore useState, useEffect, and custom hooks. Hooks allow you to use state and other React features without writing a class component. The useState hook lets you add state to functional components, while useEffect handles side effects like data fetching and subscriptions.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 1), -- JavaScript
(LAST_INSERT_ID(), 4); -- React

-- Article 2: Java + Spring Boot
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Building RESTful APIs with Spring Boot', 'Spring Boot makes it incredibly easy to create production-ready REST APIs. In this comprehensive guide, we cover controllers, services, repositories, and DTOs. We also explore best practices for exception handling, validation, and securing your endpoints with Spring Security. Learn how to leverage JPA for database operations and implement proper layered architecture.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 2), -- Java
(LAST_INSERT_ID(), 3); -- Spring Boot

-- Article 3: Angular + TypeScript
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Mastering Angular Services and Dependency Injection', 'Angular services are the backbone of any Angular application. This article dives deep into creating services, using HttpClient for API calls, and understanding dependency injection. We cover singleton services, providers, and how to share data between components efficiently. TypeScript types and interfaces play a crucial role in maintaining type safety across your application.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 3), -- Angular
(LAST_INSERT_ID(), 1); -- JavaScript (TypeScript)

-- Article 4: DevOps + Docker
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Containerizing Your Applications with Docker', 'Docker has transformed the way we deploy applications. Learn how to create Dockerfiles, build images, and run containers. We explore multi-stage builds for optimizing image size, docker-compose for orchestrating multiple services, and best practices for production deployments. Discover how Docker simplifies development environments and ensures consistency across different stages.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 5), -- DevOps
(LAST_INSERT_ID(), 6); -- Docker

-- Article 5: Python + Machine Learning
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Introduction to Machine Learning with Python', 'Python is the go-to language for machine learning and data science. This article introduces scikit-learn, pandas, and numpy for building your first ML models. We cover data preprocessing, feature engineering, model training, and evaluation. Learn about supervised learning algorithms like linear regression, decision trees, and neural networks. Practical examples help you understand the concepts better.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 7), -- Python
(LAST_INSERT_ID(), 8); -- Machine Learning

-- Article 6: Database + SQL
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Advanced SQL Queries and Optimization Techniques', 'Mastering SQL is essential for any backend developer. This guide covers complex joins, subqueries, window functions, and query optimization. Learn how to use indexes effectively, analyze query execution plans, and write efficient queries that scale. We also explore common table expressions (CTEs), transactions, and database normalization principles for better data integrity.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 9); -- Database

-- Article 7: Testing + DevOps
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Implementing CI/CD Pipelines for Modern Applications', 'Continuous Integration and Continuous Deployment are crucial for agile development. This article walks through setting up CI/CD pipelines with GitHub Actions, GitLab CI, or Jenkins. Learn about automated testing, code quality checks, security scanning, and automated deployments. Discover how to implement blue-green deployments and rollback strategies for zero-downtime releases.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 10), -- Testing
(LAST_INSERT_ID(), 5);  -- DevOps

-- Article 8: React + Testing
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Testing React Applications with Jest and React Testing Library', 'Writing tests for React components ensures code quality and prevents regressions. This comprehensive guide covers unit testing with Jest, component testing with React Testing Library, and integration testing strategies. Learn how to test hooks, mock API calls, and simulate user interactions. We also explore snapshot testing and best practices for maintainable test suites.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 4),  -- React
(LAST_INSERT_ID(), 10); -- Testing

-- Article 9: Angular + Spring Boot + Database
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Building a Full-Stack Application: Angular + Spring Boot + MySQL', 'Creating a complete full-stack application requires mastering frontend, backend, and database technologies. This tutorial guides you through building a CRUD application with Angular for the UI, Spring Boot for the REST API, and MySQL for data persistence. We cover authentication, form validation, error handling, and deployment strategies. Learn how to structure your project for scalability and maintainability.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 3), -- Angular
(LAST_INSERT_ID(), 2), -- Java/Spring Boot
(LAST_INSERT_ID(), 9); -- Database

-- Article 10: Python + Docker + DevOps
INSERT INTO posts (title, content, user_id, created_at, updated_at) VALUES
('Deploying Python Applications with Docker and Kubernetes', 'Modern Python applications require robust deployment strategies. This article demonstrates how to containerize Python apps with Docker, create production-ready images, and orchestrate them with Kubernetes. Learn about environment variables, secrets management, health checks, and scaling strategies. We cover best practices for deploying Flask, Django, and FastAPI applications in production environments.', 2, NOW(), NOW());

INSERT INTO post_topics (post_id, topic_id) VALUES
(LAST_INSERT_ID(), 7), -- Python
(LAST_INSERT_ID(), 6), -- Docker
(LAST_INSERT_ID(), 5); -- DevOps

-- Verify the inserts
SELECT COUNT(*) as total_posts FROM posts;
SELECT p.title, GROUP_CONCAT(t.name SEPARATOR ', ') as topics 
FROM posts p 
LEFT JOIN post_topics pt ON p.id = pt.post_id 
LEFT JOIN topics t ON pt.topic_id = t.id 
GROUP BY p.id, p.title 
ORDER BY p.created_at DESC;
