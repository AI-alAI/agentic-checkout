from langchain_core.tools import tool
from services.spring_client import spring_client
from config import Config


@tool
def search_products(category: str = None, max_price: float = None) -> str:
    """
    Recherche des produits dans la base de données.
    
    Args:
        category: Catégorie du produit (ex: "Laptop", "Phone", "Tablet"). Optionnel.
        max_price: Prix maximum en USD. Optionnel.
    
    Returns:
        Liste des produits trouvés (format JSON lisible).
    """
    try:
        products = spring_client.search_products(
            category=category,
            max_price=max_price
        )
        
        if not products:
            return "Aucun produit trouvé avec ces critères."
        
        result = f"J'ai trouvé {len(products)} produit(s) :\n\n"
        for p in products:
            result += f"• ID: {p['id']} | {p['name']}\n"
            result += f"  Catégorie: {p['category']}\n"
            result += f"  Prix: ${p['price']}\n"
            result += f"  Description: {p['description']}\n"
            result += f"  Stock: {p['stockQuantity']}\n\n"
        
        return result
    except Exception as e:
        return f"Erreur lors de la recherche : {str(e)}"


@tool
def add_to_cart(product_id: int, quantity: int = 1) -> str:
    """
    Ajoute un produit au panier de l'utilisateur.
    
    Args:
        product_id: L'ID du produit à ajouter.
        quantity: La quantité à ajouter (par défaut 1).
    
    Returns:
        Confirmation avec le total du panier.
    """
    try:
        cart = spring_client.add_to_cart(
            user_email=Config.DEFAULT_USER_EMAIL,
            product_id=product_id,
            quantity=quantity
        )
        
        result = f"✅ Produit ajouté au panier !\n\n"
        result += f"🛒 Total du panier : ${cart['totalPrice']}\n"
        result += f"📦 Nombre d'articles : {len(cart['items'])}\n\n"
        
        for item in cart['items']:
            result += f"• {item['product']['name']} x{item['quantity']} = ${item['subtotal']}\n"
        
        return result
    except Exception as e:
        return f"Erreur lors de l'ajout au panier : {str(e)}"


@tool
def create_order() -> str:
    """
    Crée une commande à partir du panier actuel de l'utilisateur.
    Le panier est vidé après création de la commande.
    
    Returns:
        Détails de la commande créée avec son ID.
    """
    try:
        order = spring_client.checkout(user_email=Config.DEFAULT_USER_EMAIL)
        
        result = f"✅ Commande créée avec succès !\n\n"
        result += f"📋 ID de commande : {order['id']}\n"
        result += f"💰 Total : ${order['totalAmount']}\n"
        result += f"📅 Date : {order['createdAt']}\n"
        result += f"📊 Statut : {order['status']}\n\n"
        result += f"Articles :\n"
        for item in order['items']:
            result += f"• {item['product']['name']} x{item['quantity']} = ${item['subtotal']}\n"
        
        result += f"\n🔑 Garde l'ID {order['id']} pour créer l'ordre PayPal."
        
        return result
    except Exception as e:
        return f"Erreur lors de la création de la commande : {str(e)}"


@tool
def create_paypal_order(order_id: int) -> str:
    """
    Crée un ordre PayPal pour une commande existante.
    Le montant est calculé automatiquement par le backend pour des raisons de sécurité.
    
    Args:
        order_id: L'ID de la commande interne (créée avec create_order).
    
    Returns:
        L'URL PayPal pour approuver le paiement.
    """
    try:
        paypal_order = spring_client.create_paypal_order(order_id=order_id)
        
        result = f"💳 Ordre PayPal créé !\n\n"
        result += f"🔑 PayPal Order ID : {paypal_order['id']}\n"
        result += f"📊 Statut : {paypal_order['status']}\n\n"
        result += f"🔗 **URL de paiement :**\n{paypal_order['approveUrl']}\n\n"
        result += f"👉 Clique sur ce lien pour approuver le paiement."
        
        return result
    except Exception as e:
        return f"Erreur lors de la création de l'ordre PayPal : {str(e)}"


@tool
def get_cart_summary() -> str:
    """
    Récupère un résumé du panier actuel de l'utilisateur.
    
    Returns:
        Le contenu du panier et son total.
    """
    try:
        cart = spring_client.get_cart(Config.DEFAULT_USER_EMAIL)
        
        if not cart['items']:
            return "🛒 Ton panier est vide."
        
        result = f"🛒 Contenu de ton panier :\n\n"
        for item in cart['items']:
            result += f"• {item['product']['name']} x{item['quantity']} = ${item['subtotal']}\n"
        
        result += f"\n💰 Total : ${cart['totalPrice']}"
        
        return result
    except Exception as e:
        return f"Erreur lors de la récupération du panier : {str(e)}"


# Liste de tous les tools disponibles pour l'agent
ALL_TOOLS = [
    search_products,
    add_to_cart,
    create_order,
    create_paypal_order,
    get_cart_summary
]