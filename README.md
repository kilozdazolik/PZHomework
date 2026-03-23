# PZ - Family Management Application with AI Integration

A full-stack application for managing family profiles and users with AI-powered decision support. The system includes a Spring Boot backend API and a React TypeScript frontend.

## Overview

The PZ application provides:
- Family and user profile management
- Role-based access control (Admin, User)
- AI-powered decision support via OpenAI GPT-4
- Rate limiting on AI requests
- PostgreSQL database 

## Prerequisites

Before running the application, ensure you have installed:
- Java 25 or later
- Maven 3.6+
- Node.js 18.0+ and npm
- OpenAI API Key (for AI features)
- PostgreSQL 12+ 

## Project Structure

```
Homework/
├── PZ/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/java/     # Java source code
│   │   └── resources/     # Configuration files
│   ├── pom.xml            # Maven configuration
│   └── mvnw               # Maven wrapper scripts
└── pz-frontend/           # React TypeScript frontend
    ├── src/               # React components and pages
    ├── package.json       # Node dependencies
    └── vite.config.ts     # Vite build configuration
```

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database and modify `application.properties`:

```sql
CREATE DATABASE homework_pz;
```

Then update `application.properties` to use PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/homework_pz
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 2. Backend Setup (Spring Boot)

Navigate to the `PZ` directory:

```bash
cd PZ
```

#### Configure Environment Variables

Create a `.env` file in the `PZ` directory with the following variables:

```properties
OPENAI_API_KEY=your-openai-api-key-here
```

Replace `your-openai-api-key-here` with your actual OpenAI API key.

#### Application Properties

The backend uses `src/main/resources/application.properties` for configuration:

```properties
# Application name
spring.application.name=PZ

# Environment variable import (loads from .env file)
spring.config.import=optional:file:.env[.properties]

# Database configuration 
# spring.datasource.url=jdbc:postgresql://localhost:5432/homework_pz
# spring.datasource.username=postgres
# spring.datasource.password=postgres

# JPA/Hibernate settings
spring.jpa.hibernate.ddl-auto=update    # Auto-create/update database schema
spring.jpa.show-sql=true                # Log SQL queries (development only)
spring.jpa.open-in-view=false          # Prevent lazy loading issues

# OpenAI Configuration
openai.api.key=${OPENAI_API_KEY:}      # Loaded from .env file, empty string as fallback
openai.api.url=https://api.openai.com/v1/chat/completions
openai.api.model=gpt-4o-mini

# AI Rate Limiting
app.ai.rate-limit.capacity=10          # Max 10 AI requests
app.ai.rate-limit.window-hours=1       # Per 1 hour window

# CLI Feature Toggle
app.cli.enabled=false
```

#### Build and Run Backend

Using Maven wrapper (no Maven installation required):

```bash
# Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw clean install
./mvnw spring-boot:run
```

Or with Maven (if installed globally):

```bash
mvn clean install
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`

### 3. Frontend Setup (React + TypeScript)

Navigate to the `pz-frontend` directory:

```bash
cd pz-frontend
```

Install dependencies:

```bash
npm install
```

#### Development Server

Run the development server with hot reload:

```bash
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies API calls to `http://localhost:8080/api`

#### Build for Production

Build the optimized production bundle:

```bash
npm run build
npm run preview
```

## Running the Application

### Development Mode (Both Backend and Frontend)

1. **Terminal 1 - Backend:**
   ```bash
   cd PZ
   mvnw.cmd spring-boot:run   # Windows
   # or
   ./mvnw spring-boot:run     # Linux/Mac
   ```

2. **Terminal 2 - Frontend:**
   ```bash
   cd pz-frontend
   npm run dev
   ```

3. Open `http://localhost:5173` in your browser

## API Endpoints

The backend provides REST API endpoints at `http://localhost:8080/api`

### User Endpoints

- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user (requires user background information)
- `DELETE /api/users/{id}` - Delete user

### Family Endpoints

- `POST /api/families` - Create a new family
  - Payload: `{ "name": "string" }`
  - First created member automatically becomes ADMIN, subsequent members are USER role
- `GET /api/families` - Get all families
- `GET /api/families/{id}` - Get family by ID with members
- `PUT /api/families/{id}` - Update family
- `DELETE /api/families/{id}` - Delete family

### Family Member Endpoints

- `POST /api/families/{id}/members` - Add member to family
  - Payload: `{ "name": "string" }`
- `GET /api/families/{id}/members` - Get all members of a family
- `DELETE /api/families/{id}/members/{memberId}` - Remove member from family

### Application Endpoints

- `POST /api/applications` - Create a new application
- `GET /api/applications` - Get all applications
- `GET /api/applications/{id}` - Get application by ID
- `PUT /api/applications/{id}` - Update application
- `DELETE /api/applications/{id}` - Delete application

### AI Command Endpoints

- `POST /api/ai/command` - Process an AI command
  - Requires OpenAI API key configured in `.env` file
  - Subject to rate limiting (10 requests per hour)
  - Request DTO: `AiCommandRequestDTO` with fields: `userId` (string), `prompt` (string)
  - Response DTO: `AiCommandResponseDTO` with AI decision result

## Configuration Details

### Environment Variables (.env)

The `.env` file  should contain:

```properties
OPENAI_API_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```


### Database
The application uses Hibernate ORM with auto-schema generation. Tables are automatically created/updated on startup:
- `users` - User profiles with background information
- `families` - Family records
- `family_members` - Links between users and families with roles
- `applications` - Application records
- And related entities

### Application Properties by Environment

**Development:**
```properties
spring.jpa.show-sql=true                  # SQL queries logged
spring.jpa.hibernate.ddl-auto=update      # Schema auto-updates
```

**Production:**
```properties
spring.jpa.show-sql=false                 # No query logs
spring.jpa.hibernate.ddl-auto=validate    # Only validates schema
```

## Troubleshooting

### Backend won't start
- Verify PostgreSQL is running on localhost:5432
- Check database credentials in `application.properties`
- Ensure Java 25+ is installed: `java -version`
- Check for port 8080 conflicts: Backend must use port 8080

### Frontend build fails
- Clear node_modules: `rm -rf node_modules && npm install`
- Clear build cache: `npm run build` regenerates
- Verify Node.js version: `node --version` (18.0+)

### API calls failing
- Ensure backend is running: `http://localhost:8080/api/users`
- Check frontend is configured to call correct API: See `pz-frontend/src/services/http.ts`
- Browser console shows CORS errors if backend isn't running

### AI features not working
- Verify `.env` file exists in the `PZ` directory
- Verify `OPENAI_API_KEY` is set in `.env` file with correct format
- Check OpenAI API key is valid at https://platform.openai.com/account/api-keys
- Monitor rate limiting: 10 AI requests per hour per application property
- Restart backend after updating `.env` file

### Database issues
  - PostgreSQL server must be running on localhost:5432
  - Database must exist: `CREATE DATABASE homework_pz;`
  - Credentials in `application.properties` must match PostgreSQL user
  - Default credentials: username=`postgres`, password=`postgres`

## Development Notes

### Frontend Technology Stack
- React 19.2 - UI library
- TypeScript 5.9 - Type safety
- Vite 8.0 - Fast build tool and dev server
- React Router 7.13 - Client-side routing
- ESLint - Code quality

### Backend Technology Stack
- Spring Boot 4.0.3 - Application framework
- Spring Data JPA - ORM and database access
- PostgreSQL - Optional production database
- OpenAI API - AI-powered command processing
- Bucket4j - Rate limiting for AI requests


## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Vite Documentation](https://vitejs.dev)
