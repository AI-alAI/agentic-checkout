import requests
from typing import Optional, Dict, List
from config import Config


class SpringBootClient:
    """
    Client HTTP pour appeler l'API Spring Boot.
    C'est le pont entre l'AI Agent (Python) et le Backend (Java).
    """

    def __init__(self, base_url: str = None):
        self.base_url = base_url or Config.SPRING_BOOT_URL

    # =====================================================
    # PRODUITS
    # =====================================================
    def search_products(self, category: Optional[str] = None,
                        max_price: Optional[float] = None) -> List[Dict]:
        """Cherche des produits dans la base de données."""
        params = {}
        if category:
            params["category"] = category
        if max_price:
            params["maxPrice"] = max_price

        response = requests.get(
            f"{self.base_url}/api/products/search",
            params=params,
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    def get_all_products(self) -> List[Dict]:
        """Récupère tous les produits."""
        response = requests.get(f"{self.base_url}/api/products", timeout=10)
        response.raise_for_status()
        return response.json()

    # =====================================================
    # PANIER
    # =====================================================
    def get_cart(self, user_email: str) -> Dict:
        """Récupère le panier d'un utilisateur."""
        response = requests.get(
            f"{self.base_url}/api/cart/{user_email}",
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    def add_to_cart(self, user_email: str, product_id: int,
                    quantity: int = 1) -> Dict:
        """Ajoute un produit au panier."""
        response = requests.post(
            f"{self.base_url}/api/cart/{user_email}/add",
            params={"productId": product_id, "quantity": quantity},
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    # =====================================================
    # COMMANDES
    # =====================================================
    def checkout(self, user_email: str) -> Dict:
        """Crée une commande depuis le panier."""
        response = requests.post(
            f"{self.base_url}/api/orders/{user_email}/checkout",
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    def get_order(self, order_id: int) -> Dict:
        """Récupère une commande par ID."""
        response = requests.get(
            f"{self.base_url}/api/orders/{order_id}",
            timeout=10
        )
        response.raise_for_status()
        return response.json()

    # =====================================================
    # PAYPAL (Modifié pour la sécurité)
    # =====================================================
    def create_paypal_order(self, order_id: int) -> Dict:
        """Crée un ordre PayPal (le montant est géré par le backend)."""
        response = requests.post(
            f"{self.base_url}/api/paypal/create",
            json={"orderId": order_id},  # On n'envoie plus le montant
            timeout=15
        )
        response.raise_for_status()
        return response.json()


# Instance globale (singleton)
spring_client = SpringBootClient()