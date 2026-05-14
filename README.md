# 🤖 AI Job Application Tracker API

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-green?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=flat-square&logo=jsonwebtokens)
![Groq AI](https://img.shields.io/badge/Groq-AI_Powered-purple?style=flat-square)
![Railway](https://img.shields.io/badge/Deployed-Railway-blueviolet?style=flat-square&logo=railway)

A production-ready **AI-powered Job Application Tracker REST API** built with Spring Boot. Track your job applications and use AI to analyze how well your resume matches a job description — returning a match score, matched skills, missing skills, and actionable suggestions.

> 🚀 **Live API:** `https://jobtracker-production-fe8c.up.railway.app`

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure register/login with BCrypt password hashing
- 📋 **Job Application CRUD** — Add, view, update status, and delete applications
- 🤖 **AI Resume Analysis** — Groq LLaMA 3.3 analyzes resume vs job description fit
- 📊 **Dashboard Stats** — Match scores, interview rate, status breakdown
- 🔄 **Status Workflow** — Full audit log of every status change
- ☁️ **Cloud Deployed** — Live on Railway with MySQL database

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 7 + JWT (JJWT 0.11.5) |
| Database | MySQL 8.0 + JPA/Hibernate 7 |
| AI Integration | Groq API (LLaMA 3.3 70B) |
| HTTP Client | Spring WebFlux (WebClient) |
| Build Tool | Maven |
| Deployment | Railway + Docker |

---

## 🏗️ Project Structure

```
src/main/java/com/jobtrackerapp/jobtracker/
├── config/
│   ├── JwtUtil.java              # JWT token generation & validation
│   ├── JwtFilter.java            # JWT request filter
│   ├── SecurityConfig.java       # Spring Security configuration
│   ├── CustomUserDetailsService.java
│   └── WebClientConfig.java      # WebClient & ObjectMapper beans
├── controller/
│   ├── AuthController.java       # Register & Login endpoints
│   ├── JobApplicationController.java
│   ├── AIAnalysisController.java
│   └── DashboardController.java
├── service/
│   ├── AuthService.java
│   ├── JobApplicationService.java
│   ├── AIAnalysisService.java
│   ├── GeminiService.java        # Groq AI API integration
│   └── DashboardService.java
├── repository/
│   ├── UserRepository.java
│   ├── JobApplicationRepository.java
│   ├── AIAnalysisRepository.java
│   └── StatusHistoryRepository.java
├── entity/
│   ├── User.java
│   ├── JobApplication.java
│   ├── ApplicationStatus.java    # Enum: APPLIED → ACCEPTED/REJECTED
│   ├── AIAnalysis.java
│   └── StatusHistory.java
└── dto/
    ├── RegisterRequest.java / LoginRequest.java / AuthResponse.java
    ├── JobApplicationRequest.java / JobApplicationResponse.java
    ├── StatusUpdateRequest.java
    ├── AIAnalysisResponse.java
    ├── UpdateResumeRequest.java
    └── DashboardResponse.java
```

---

## 🗄️ Database Schema

```
users
├── id, email (unique), password (BCrypt), full_name, resume_text, created_at

job_applications
├── id, user_id (FK), company_name, role_name, job_description
├── status (APPLIED/SHORTLISTED/INTERVIEW/TECHNICAL_ROUND/HR_ROUND/OFFER/ACCEPTED/REJECTED/WITHDRAWN)
├── applied_date, created_at

ai_analysis
├── id, application_id (FK), match_score, matched_skills, missing_skills
├── suggestions, summary, analyzed_at

status_history
├── id, application_id (FK), from_status, to_status, changed_at
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL 8.0
- Groq API key (free at [console.groq.com](https://console.groq.com))

### Local Setup

**1. Clone the repository:**
```bash
git clone https://github.com/yourusername/jobtracker.git
cd jobtracker
```

**2. Create MySQL database:**
```sql
CREATE DATABASE jobtracker_db;
```

**3. Set environment variables:**

Create a `.env` file in the root or set these in your IDE run configuration:
```
GROQ_API_KEY=your_groq_api_key
JWT_SECRET=jobtracker_super_secret_key_2024_min32chars
```

**4. Update `application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jobtracker_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

**5. Run the application:**
```bash
mvn spring-boot:run
```

App starts at `http://localhost:8080`

---

## 📡 API Endpoints

### 🔐 Authentication
> Public endpoints — no token required

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/health` | Health check |

**Register:**
```json
POST /api/auth/register
{
  "fullName": "Shiva Kumar",
  "email": "shiva@test.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "shiva@test.com",
  "fullName": "Shiva Kumar"
}
```

---

### 📋 Job Applications
> All endpoints require `Authorization: Bearer <token>` header

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/applications` | Add new application |
| GET | `/api/applications` | Get all your applications |
| GET | `/api/applications/{id}` | Get single application |
| PUT | `/api/applications/{id}/status` | Update application status |
| DELETE | `/api/applications/{id}` | Delete application |

**Add Application:**
```json
POST /api/applications
{
  "companyName": "Google",
  "roleName": "Java Backend Developer",
  "jobDescription": "Looking for Java developer with Spring Boot, Microservices, REST APIs, MySQL, Docker, Azure..."
}
```

**Update Status:**
```json
PUT /api/applications/1/status
{
  "status": "SHORTLISTED"
}
```

**Available statuses:**
`APPLIED` → `SHORTLISTED` → `INTERVIEW` → `TECHNICAL_ROUND` → `HR_ROUND` → `OFFER` → `ACCEPTED` / `REJECTED` / `WITHDRAWN`

---

### 🤖 AI Analysis
> The unique feature — AI-powered resume matching

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/api/user/resume` | Save your resume text |
| POST | `/api/applications/{id}/analyze` | Run AI analysis |
| GET | `/api/applications/{id}/analysis` | Get saved analysis |

**Save Resume:**
```json
PUT /api/user/resume
{
  "resumeText": "Java Developer with 2.6 years at TechMahindra. Skills: Java, Spring Boot, REST APIs, Microservices, JPA/Hibernate, Spring Security, JWT, MySQL, Azure, Jenkins CI/CD, Docker..."
}
```

**Trigger Analysis:**
```
POST /api/applications/1/analyze
(No request body needed — uses saved resume + job description)
```

**Analysis Response:**
```json
{
  "id": 1,
  "matchScore": 82,
  "matchedSkills": ["Java", "Spring Boot", "REST APIs", "MySQL", "Docker", "JWT"],
  "missingSkills": ["Kubernetes", "Kafka", "Redis"],
  "suggestions": [
    "Add Kubernetes basics to complement your Docker knowledge",
    "Implement a Kafka producer/consumer in a side project",
    "Add Redis caching to your Spring Boot project"
  ],
  "summary": "Strong match with core Java backend skills. Upskilling in Kubernetes and Kafka would make you an ideal candidate.",
  "analyzedAt": "2026-05-14T10:30:00"
}
```

---

### 📊 Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Get application statistics |

**Response:**
```json
{
  "totalApplications": 10,
  "statusBreakdown": {
    "APPLIED": 5,
    "SHORTLISTED": 3,
    "INTERVIEW": 2
  },
  "averageMatchScore": 74.5,
  "analyzedApplications": 6,
  "interviewCount": 2,
  "interviewRate": 20.0
}
```

---

## 🤖 How the AI Integration Works

```
User → POST /analyze
         ↓
Fetch resume from users table
         ↓
Fetch job description from job_applications table
         ↓
Send both to Groq API (LLaMA 3.3 70B)
         ↓
Parse JSON response (matchScore, skills, suggestions)
         ↓
Save to ai_analysis table
         ↓
Return structured response to user
```

The AI prompt asks the model to return a structured JSON object comparing the resume against the job description — making it easy to parse and store.

---

## 🐳 Docker

Build and run locally with Docker:

```bash
# Build image
docker build -t jobtracker .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/jobtracker_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  -e GROQ_API_KEY=your_key \
  -e JWT_SECRET=your_secret \
  jobtracker
```

---

## ☁️ Deployment

Deployed on **Railway** with:
- Spring Boot app service (Docker)
- MySQL 9.4 database service
- Environment variables for all secrets
- Auto-deploy on every GitHub push

---

## 👨‍💻 Author

**Shiva Kumar Goud V**
- LinkedIn: [linkedin.com/in/shivakumargoud-v](https://linkedin.com/in/shivakumargoud-v)
- Email: vajagounishiva316@gmail.com

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
