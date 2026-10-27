from agent.agent import agent

print("Agent PayPilot AI initialise !\n")

# Test 1 : Recherche de produit
print("=" * 60)
print("TEST 1 : Recherche de produits")
print("=" * 60)
response = agent.invoke({
    "messages": [
        {"role": "user", "content": "Cherche-moi un laptop."}
    ]
})
print(f"\nReponse : {response['messages'][-1].content}\n")

# Test 2 : Ajouter au panier
print("=" * 60)
print("TEST 2 : Ajouter au panier")
print("=" * 60)
response = agent.invoke({
    "messages": [
        {"role": "user", "content": "Ajoute le produit avec l'ID 1 a mon panier."}
    ]
})
print(f"\nReponse : {response['messages'][-1].content}\n")

# Test 3 : Voir le panier
print("=" * 60)
print("TEST 3 : Voir le panier")
print("=" * 60)
response = agent.invoke({
    "messages": [
        {"role": "user", "content": "Montre-moi mon panier."}
    ]
})
print(f"\nReponse : {response['messages'][-1].content}")