from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from agent.agent import agent
from config import Config


# 1. Validation de la configuration au démarrage
Config.validate()

# 2. Création de l'application FastAPI
app = FastAPI(
    title="PayPilot AI Agent Service",
    description="AI Agent for autonomous shopping with PayPal",
    version="1.0.0"
)

# 3. Configuration CORS (INDISPENSABLE pour Angular)
# ⚠️ Attention : allow_origins=["*"] exige allow_credentials=False
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",    # Frontend Angular
        "http://127.0.0.1:4200",
        "http://localhost:8000",    # Swagger UI
        "http://127.0.0.1:8000",
    ],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


# 4. Modèles de données (Pydantic)
class ChatRequest(BaseModel):
    message: str
    user_email: str = "test@example.com"


class ChatResponse(BaseModel):
    response: str
    success: bool = True


# 5. Endpoints
@app.get("/")
def root():
    """Page d'accueil pour vérifier que le service tourne."""
    return {
        "service": "PayPilot AI Agent",
        "status": "running",
        "model": Config.MODEL_NAME,
        "endpoints": {
            "chat": "POST /api/chat",
            "docs": "GET /docs",
            "health": "GET /health"
        }
    }


@app.get("/health")
def health():
    """Healthcheck (utilisé pour vérifier que le serveur répond)."""
    return {"status": "healthy"}


@app.post("/api/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    """
    Endpoint principal : l'utilisateur envoie un message,
    l'agent répond (avec actions si nécessaire).
    """
    try:
        # Appel de l'agent LangGraph
        result = agent.invoke({
            "messages": [
                {"role": "user", "content": request.message}
            ]
        })
        
        # Extraction de la dernière réponse
        agent_response = result["messages"][-1].content
        
        return ChatResponse(
            response=agent_response,
            success=True
        )
        
    except Exception as e:
        # Log dans le terminal pour debug
        import traceback
        traceback.print_exc()
        
        raise HTTPException(
            status_code=500,
            detail=f"Erreur de l'agent : {str(e)}"
        )


# 6. Point d'entrée (si lancé via `python main.py`)
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )