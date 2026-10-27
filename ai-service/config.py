import os
from dotenv import load_dotenv

# Charge les variables du fichier .env
load_dotenv()

class Config:
    # API Groq
    GROQ_API_KEY = os.getenv("GROQ_API_KEY", "")
    MODEL_NAME = "openai/gpt-oss-120b"
    TEMPERATURE = 0
    
    # Backend Spring Boot
    SPRING_BOOT_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:8080")
    
    # User email par défaut (pour les tests)
    DEFAULT_USER_EMAIL = "test@example.com"
    
    # Validation
    @classmethod
    def validate(cls):
        if not cls.GROQ_API_KEY or cls.GROQ_API_KEY == "gsk_TON_API_KEY_ICI":
            raise ValueError(
                "❌ GROQ_API_KEY n'est pas configurée dans .env\n"
                "1. Va sur https://console.groq.com\n"
                "2. Crée une API Key\n"
                "3. Copie-la dans ai-service/.env"
            )
        return True