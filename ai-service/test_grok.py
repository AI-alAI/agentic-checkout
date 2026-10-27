from config import Config
from langchain_groq import ChatGroq

print("Validation de la config...")
Config.validate()
print("Config OK")

print("Initialisation de Groq...")
llm = ChatGroq(
    groq_api_key=Config.GROQ_API_KEY,
    model_name=Config.MODEL_NAME,
    temperature=Config.TEMPERATURE
)

print("Envoi d'un message test...")
response = llm.invoke("Dis 'Bonjour' en un seul mot.")
print(f"Groq repond : {response.content}")
print("TOUT FONCTIONNE !")