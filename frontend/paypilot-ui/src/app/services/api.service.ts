import { Injectable } from '@angular/core';
import axios from 'axios';
import { ChatRequest, ChatResponse } from '../models/models';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private readonly AI_SERVICE_URL = 'http://localhost:8000';
    private readonly SPRING_URL = 'http://localhost:8080';

    /**
     * Envoie un message à l'agent AI (FastAPI)
     */
    async sendMessage(message: string, userEmail: string): Promise<string> {
        const payload: ChatRequest = {
            message: message,
            user_email: userEmail
        };

        console.log('🌐 [ApiService] POST vers:', `${this.AI_SERVICE_URL}/api/chat`);
        console.log('🌐 [ApiService] Payload:', payload);

        try {
            const response = await axios.post<ChatResponse>(
                `${this.AI_SERVICE_URL}/api/chat`,
                payload,
                { timeout: 60000 }
            );

            console.log('🌐 [ApiService] Réponse reçue:', response.data);
            return response.data.response;
        } catch (error: any) {
            console.error('🌐 [ApiService] Erreur:', error);
            if (error.response) {
                console.error('   Status:', error.response.status);
                console.error('   Data:', error.response.data);
            } else if (error.request) {
                console.error('   Pas de réponse du serveur (FastAPI est-il lancé ?)');
            } else {
                console.error('   Erreur de configuration:', error.message);
            }
            throw error;
        }
    }

    /**
     * Récupère le panier d'un utilisateur (Spring Boot)
     */
    async getCart(userEmail: string) {
        try {
            const response = await axios.get(
                `${this.SPRING_URL}/api/cart/${userEmail}`
            );
            return response.data;
        } catch (error) {
            console.error('Erreur getCart:', error);
            throw error;
        }
    }

    /**
     * Récupère les commandes d'un utilisateur (Spring Boot)
     */
    async getOrders(userEmail: string) {
        try {
            const response = await axios.get(
                `${this.SPRING_URL}/api/orders/user/${userEmail}`
            );
            return response.data;
        } catch (error) {
            console.error('Erreur getOrders:', error);
            throw error;
        }
    }
}