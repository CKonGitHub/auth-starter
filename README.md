# Auth Starter — Spring Boot Hexagonal Architecture

Starter kit Java Spring Boot pour mettre en place une base d’authentification avec JWT, rôles et architecture hexagonale.

L’objectif du projet est de fournir une base claire, testée et évolutive pour construire une API REST sécurisée sans mélanger le métier avec les détails techniques de Spring, JPA ou Spring Security.

---

## Fonctionnalités

Le projet contient actuellement :

- inscription utilisateur
- connexion utilisateur
- génération d’un JWT à la connexion
- authentification stateless avec header `Authorization: Bearer <token>`
- endpoint protégé `/api/auth/me`
- hash du mot de passe avec Spring Security
- gestion simple des rôles
- exceptions métier dédiées
- réponses d’erreur API structurées
- persistance avec Spring Data JPA
- base H2 en mémoire
- tests unitaires des cas d’usage
- tests d’intégration HTTP avec MockMvc

---

## Stack technique

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Security
- Spring Data JPA
- H2 Database
- Bean Validation
- Lombok
- Java JWT
- JUnit 5
- Mockito
- MockMvc

---

## Architecture

Le projet suit une architecture hexagonale.

```text
src/main/java/com/example/auth_starter
├── domain
│   ├── exception
│   ├── model
│   └── port
│       ├── in
│       └── out
├── application
│   └── service
├── infrastructure
│   ├── adapter
│   │   ├── in
│   │   │   ├── security
│   │   │   └── web
│   │   └── out
│   │       ├── persistence
│   │       └── security
│   └── config
```

---

## Domain

La couche `domain` contient le cœur métier pur.

Exemples :

- `User`
- `Role`
- `EmailAlreadyUsedException`
- `InvalidCredentialsException`
- `RegisterUserUseCase`
- `LoginUserUseCase`
- `RegisterUserCommand`
- `LoginUserCommand`
- `LoginUserResult`
- `UserRepositoryPort`
- `PasswordEncoderPort`
- `JwtTokenPort`

Cette couche ne dépend pas de Spring, JPA, Spring Security ou d’une librairie JWT concrète.

---

## Application

La couche `application` contient les cas d’usage.

Exemples :

- `RegisterUserUseCaseImpl`
- `LoginUserUseCaseImpl`

Cette couche orchestre les règles métier via les ports.

Exemple de flux login :

```text
LoginUserCommand
-> LoginUserUseCase
-> UserRepositoryPort
-> PasswordEncoderPort
-> JwtTokenPort
-> LoginUserResult
```

---

## Infrastructure

La couche `infrastructure` contient les détails techniques.

Exemples :

- contrôleurs REST
- DTO HTTP
- filtre JWT
- principal Spring Security
- entités JPA
- repository Spring Data
- mapper persistence
- configuration Spring Security
- adapter de hash de mot de passe
- adapter de génération et validation JWT

---

## Endpoints disponibles

| Méthode | URL                  | Description                         | Public |
| ------- | -------------------- | ----------------------------------- | ------ |
| POST    | `/api/auth/register` | Inscrire un utilisateur             | Oui    |
| POST    | `/api/auth/login`    | Connecter un utilisateur            | Oui    |
| GET     | `/api/auth/me`       | Récupérer l’utilisateur authentifié | Non    |

---

## Inscription

Endpoint :

```http
POST /api/auth/register
```

Body :

```json
{
    "email": "test@example.com",
    "password": "password12345"
}
```

Réponse :

```json
{
    "id": "uuid",
    "email": "test@example.com",
    "roles": ["USER"]
}
```

Le mot de passe n’est jamais retourné dans la réponse.

---

## Connexion

Endpoint :

```http
POST /api/auth/login
```

Body :

```json
{
    "email": "test@example.com",
    "password": "password12345"
}
```

Réponse :

```json
{
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "user": {
        "id": "uuid",
        "email": "test@example.com",
        "roles": ["USER"]
    }
}
```

Le token retourné doit ensuite être envoyé dans les requêtes protégées avec le header :

```http
Authorization: Bearer <accessToken>
```

---

## Utilisateur courant

Endpoint :

```http
GET /api/auth/me
```

Header requis :

```http
Authorization: Bearer <accessToken>
```

Réponse :

```json
{
    "id": "uuid",
    "email": "test@example.com",
    "roles": ["USER"]
}
```

Sans token valide, l’accès est refusé.

---

## Gestion des erreurs

Les erreurs API utilisent une réponse structurée.

Format :

```json
{
    "code": "ERROR_CODE",
    "message": "Message lisible"
}
```

Exemples :

### Email déjà utilisé

```json
{
    "code": "EMAIL_ALREADY_USED",
    "message": "Email déjà utilisé"
}
```

### Identifiants invalides

```json
{
    "code": "INVALID_CREDENTIALS",
    "message": "Identifiants invalides"
}
```

### Erreur interne

```json
{
    "code": "INTERNAL_ERROR",
    "message": "Une erreur interne est survenue"
}
```

---

## Sécurité

La configuration de sécurité est stateless.

Les routes publiques sont :

```text
POST /api/auth/register
POST /api/auth/login
```

Toutes les autres routes nécessitent une authentification.

Le JWT est transmis via :

```http
Authorization: Bearer <token>
```

Le filtre JWT lit le token, le valide, extrait les claims, puis reconstruit un utilisateur authentifié pour Spring Security.

Le CSRF est désactivé car l’API fonctionne en mode stateless avec un token Bearer dans le header `Authorization`, et non avec une session serveur ou un cookie d’authentification automatiquement envoyé par le navigateur.

La session Spring Security est explicitement désactivée avec :

```text
SessionCreationPolicy.STATELESS
```

---

## JWT

Le token contient actuellement :

- l’id utilisateur dans le `subject`
- l’email dans un claim `email`
- les rôles dans un claim `roles`
- une date d’émission
- une date d’expiration

Exemple de claims :

```json
{
    "sub": "user-uuid",
    "email": "test@example.com",
    "roles": ["USER"],
    "iat": 1710000000,
    "exp": 1710003600
}
```

La configuration JWT se trouve dans `application.properties`.

```properties
app.jwt.secret=dev-secret-change-me-with-at-least-32-characters
app.jwt.expiration-seconds=3600
```

La valeur du secret est uniquement prévue pour le développement local.

---

## Configuration H2

Le projet utilise H2 en mémoire pour le développement.

Exemple de configuration dans `application.properties` :

```properties
spring.datasource.url=jdbc:h2:mem:auth_starter
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

app.jwt.secret=dev-secret-change-me-with-at-least-32-characters
app.jwt.expiration-seconds=3600
```

Console H2 :

```text
http://localhost:8080/h2-console
```

JDBC URL :

```text
jdbc:h2:mem:auth_starter
```

---

## Lancer le projet

Depuis la racine du projet :

```bash
./mvnw spring-boot:run
```

Ou avec Maven installé :

```bash
mvn spring-boot:run
```

L’application démarre par défaut sur :

```text
http://localhost:8080
```

---

## Lancer les tests

```bash
./mvnw test
```

Ou :

```bash
mvn test
```

---

## Tests unitaires

Les tests unitaires couvrent les cas d’usage applicatifs sans lancer Spring.

### RegisterUserUseCaseImpl

Cas testés :

- inscription réussie quand l’email n’est pas utilisé
- erreur métier quand l’email est déjà utilisé

### LoginUserUseCaseImpl

Cas testés :

- connexion réussie avec identifiants valides
- génération du token après authentification réussie
- erreur métier quand l’utilisateur n’existe pas
- erreur métier quand le mot de passe est incorrect
- absence de génération de token en cas d’identifiants invalides

---

## Tests d’intégration

Les tests d’intégration utilisent :

- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- `MockMvc`
- H2
- le contexte Spring complet

Ils testent la chaîne complète :

```text
Controller
-> UseCase
-> Port
-> Adapter JPA
-> H2
```

Cas testés :

- inscription réussie
- inscription refusée si email déjà utilisé
- connexion réussie
- réponse login avec JWT
- connexion refusée si mot de passe incorrect
- inscription refusée si email invalide
- inscription refusée si mot de passe trop court
- accès refusé à `/api/auth/me` sans token
- accès autorisé à `/api/auth/me` avec token valide

---

## Exemple de scénario manuel

### 1. Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password12345"
  }'
```

### 2. Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password12345"
  }'
```

### 3. Utilisateur courant

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <accessToken>"
```

---

## Limites actuelles

Le projet reste volontairement simple.

Il ne contient pas :

- refresh token
- rotation de token
- révocation de token
- endpoint admin
- gestion avancée des rôles
- stratégie de validation avancée des mots de passe
- reset password
- vérification d’email
- rate limiting
- audit logs

---

## Prochaines évolutions possibles

Évolutions cohérentes avec le périmètre actuel :

- ajouter un endpoint admin protégé
- ajouter une protection par rôle avec `ADMIN`
- ajouter des tests `403 Forbidden`
- ajouter une meilleure réponse pour les erreurs de validation
- ajouter une stratégie de password policy
- ajouter un refresh token
- ajouter un endpoint de logout côté client
- ajouter des tests pour token invalide
- ajouter des tests pour token expiré

---

## Notes

Ce starter kit est volontairement limité à une base locale avec H2.

L’objectif n’est pas de fournir une infrastructure complète de production, mais une base backend propre pour apprendre et démontrer :

- architecture hexagonale
- séparation métier / infrastructure
- authentification JWT stateless
- gestion d’erreurs propre
- tests unitaires et tests d’intégration HTTP
