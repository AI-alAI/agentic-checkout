from langchain_groq import ChatGroq
from langgraph.prebuilt import create_react_agent
from agent.tools import ALL_TOOLS
from config import Config


# =========================================================
# PROMPT SYSTÈME : Définit le comportement de l'agent
# =========================================================
SYSTEM_PROMPT = """Tu es PayPilot AI, un assistant shopping autonome.

RÈGLES :
1. Agis directement. Sois concis (1-2 phrases).
2. Réponds dans la langue de l'utilisateur.
3. Utilise search_products pour chercher, add_to_cart pour ajouter, create_order et create_paypal_order pour payer.

FLOW :
- Cherche le produit.
- Recommande-le en 1 phrase.
- Si l'utilisateur valide (ou donne un budget), ajoute au panier, crée la commande et le lien PayPal.
- Affiche le lien PayPal seul sur une nouvelle ligne.
"""



def create_paypilot_agent():
    """Crée l'agent AI avec tous les tools disponibles."""
    
    # 1. Initialisation du LLM (Groq)
    llm = ChatGroq(
        groq_api_key=Config.GROQ_API_KEY,
        model_name=Config.MODEL_NAME,
        temperature=Config.TEMPERATURE
    )
    
    # 2. Création de l'agent avec LangGraph (ReAct Agent)
    agent = create_react_agent(
        model=llm,
        tools=ALL_TOOLS,
        prompt=SYSTEM_PROMPT
    )
    
    return agent


# Instance globale (utilisée par FastAPI dans main.py)
agent = create_paypilot_agent()