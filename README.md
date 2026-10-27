# 🤖 PayPilot AI — Autonomous Shopping Agent with PayPal

> Turn a simple conversation into a complete purchase. Powered by AI, secured by PayPal.

![PayPal](https://img.shields.io/badge/PayPal-Sandbox-blue)
![Angular](https://img.shields.io/badge/Angular-22-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green)
![Python](https://img.shields.io/badge/Python-3.13-yellow)

## 🎯 What is PayPilot AI?

PayPilot AI is an **autonomous shopping agent** integrated with PayPal. Instead of manually browsing e-commerce sites, you simply talk to an AI that:
1. Understands your needs in natural language
2. Searches a real product database
3. Recommends the best option
4. Adds it to your cart
5. Creates an order
6. Generates a secure PayPal payment link

## 🏗️ Architecture

\`\`\`
┌─────────────┐      ┌──────────────┐      ┌─────────────────┐
│   Angular   │─────▶│  Spring Boot │─────▶│      MySQL      │
│  (Frontend) │      │   (Backend)  │      │   (Database)    │
└─────────────┘      └──────┬───────┘      └─────────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │  Python AI   │
                     │  (LangGraph) │
                     │    + Groq    │
                     └──────┬───────┘
                            │
                            ▼
                     ┌──────────────┐
                     │    PayPal    │
                     │   Sandbox    │
                     └──────────────┘
\`\`\`

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Python 3.13+
- Node.js 20+
- MySQL 8.0+

### 1. Clone the repo
\`\`\`bash
git clone https://github.com/VOTRE_USERNAME/paypilot-ai.git
cd paypilot-ai
\`\`\`

### 2. Backend (Spring Boot)
\`\`\`bash
cd backend/agentic-checkout
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties with your MySQL + PayPal credentials
./mvnw.cmd spring-boot:run
\`\`\`

### 3. AI Service (Python)
\`\`\`bash
cd ai-service
python -m venv venv
.\\venv\\Scripts\\Activate.ps1   # Windows
pip install -r requirements.txt
cp .env.example .env
# Edit .env with your GROQ_API_KEY
python main.py
\`\`\`

### 4. Frontend (Angular)
\`\`\`bash
cd frontend/paypilot-ui
npm install
npm start
\`\`\`

### 5. Open the app
👉 http://localhost:4200

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 22, TypeScript |
| Backend | Spring Boot 4.1, Java 17, JPA/Hibernate |
| Database | MySQL 8.0 |
| AI Agent | Python 3.13, LangGraph, LangChain, Groq (LLM) |
| Payments | PayPal Orders API v2 (Sandbox) |

## 🏆 Hackathon

Built for the **PayPal AI Hackathon 2026**.

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

## 👨‍💻 Author

**Mehdi** — [@mehdinl9999](https://github.com/mehdinl9999)