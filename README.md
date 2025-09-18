# JobFinder 🔎💼

[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-6DB33F?logo=spring&logoColor=white)](https://docs.spring.io/spring-ai/reference/)
[![Twilio](https://img.shields.io/badge/Twilio-Voice%20%2F%20SMS%20%2F%20WhatsApp-F22F46?logo=twilio&logoColor=white)](https://www.twilio.com/)
[![WhatsApp](https://img.shields.io/badge/WhatsApp-Business%20API-25D366?logo=whatsapp&logoColor=white)](https://www.whatsapp.com/business/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-Build%20Tool-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)

---

## 📖 Introduction

**JobFinder** is a Spring Boot application designed to automatically fetch new developer job postings from online job boards and deliver them directly to your WhatsApp.  

This project was built to:
- **Save time** ⏱️ — no more manually checking job boards every day.  
- **Stay updated** 📲 — receive job alerts instantly via WhatsApp.  
- **Experiment with AI** 🤖 — integrate OpenAI through Spring AI for summarizing job posts or extracting key info.  
- **Practice enterprise integration** 🛠️ — connecting multiple technologies (Spring, Twilio, WhatsApp API, Postgres).  

This project also serves as a real-world example of combining **AI + messaging + databases** in a clean, production-grade setup.

---

## ⚙️ Tech Stack

- **[Java 17](https://www.oracle.com/java/)** → Core language  
- **[Spring Boot](https://spring.io/projects/spring-boot)** → Application framework  
- **[Spring AI](https://docs.spring.io/spring-ai/reference/)** → OpenAI integration  
- **[Twilio WhatsApp API](https://www.twilio.com/whatsapp)** → WhatsApp messaging  
- **[PostgreSQL](https://www.postgresql.org/)** → Persistent database for storing job posts  
- **[Maven](https://maven.apache.org/)** → Dependency and build management  

---

## 🚀 Installation & Setup

### 1. Clone the repository
```bash
git clone https://github.com/your-username/jobfinder.git
cd jobfinder
```



### 2. Configure environment variables

Create a .env file (or export variables in your shell):

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/jobfinder
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_pass

# OpenAI
OPENAI_API_KEY=sk-xxxx

# Twilio / WhatsApp
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=xxxxxxxxxxxxxxxxxxxxxx
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
TWILIO_WHATSAPP_TO=whatsapp:+1234567890
```

### 3. Build the application

```bash
mvn clean install
```

### 4. Run locally

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080
.

### 5. Database migration

Make sure Postgres is running and you have a database created:

```
CREATE DATABASE jobfinder;
```

Flyway or Hibernate will validate schema on startup.




### 🛡️ Security Notes

Secrets are not hardcoded, only injected via environment variables.

.gitignore excludes IDE, build, and secret files (.idea/, target/, .env).

Use ddl-auto=validate in production with migrations via Flyway/Liquibase.

### 🤝 Contributing

Contributions are welcome! Fork the repo, create a feature branch, and open a pull request.
