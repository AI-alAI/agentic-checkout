from groq import Groq
from config import Config

# Initialisation du client avec ta clé
client = Groq(api_key=Config.GROQ_API_KEY)

print("📋 Liste des modèles disponibles sur ton compte Groq :\n")

try:
    models = client.models.list()
    for model in models.data:
        print(f"✅ {model.id}")
except Exception as e:
    print(f"❌ Erreur : {e}")