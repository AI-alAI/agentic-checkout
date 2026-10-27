import { Component, ViewChild, ElementRef, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Message } from '../../models/models';

@Component({
    selector: 'app-chat',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.css']
})
export class ChatComponent implements AfterViewChecked {
    @ViewChild('messagesContainer') messagesContainer!: ElementRef;

    messages: Message[] = [];
    inputMessage = '';
    isLoading = false;
    userEmail = 'test@example.com';

    // Suggestions pour l'écran d'accueil
    suggestions = [
        { icon: '💻', text: 'I need a laptop for programming, max $1500. Pick the best one and prepare my order.' },
        { icon: '📱', text: 'Find me the best smartphone under $500.' },
        { icon: '🎧', text: 'I need accessories for my home office, budget $200.' },
    ];

    // Regex pour détecter les images et les liens PayPal dans le texte de l'IA
    private readonly IMG_RE = /https?:\/\/[^\s*)"]+\.(?:webp|png|jpe?g|gif)(?:\?[^\s*)"]*)?/gi;
    private readonly PAY_RE = /https?:\/\/(?:www\.)?(?:sandbox\.)?paypal\.com\/[^\s*)"]+/gi;

    constructor(
        private apiService: ApiService,
        private cdr: ChangeDetectorRef
    ) {
        // On ne met pas de message ici pour laisser l'écran d'accueil (Hero) s'afficher
    }

    ngAfterViewChecked() {
        this.scrollToBottom();
    }

    private scrollToBottom() {
        if (this.messagesContainer) {
            try {
                this.messagesContainer.nativeElement.scrollTop =
                    this.messagesContainer.nativeElement.scrollHeight;
            } catch (err) {}
        }
    }

    private generateId(): string {
        return Math.random().toString(36).substring(2, 11);
    }

    async sendMessage() {
        const text = this.inputMessage.trim();
        if (!text || this.isLoading) return;

        // 1. Ajouter le message de l'utilisateur
        this.messages.push({
            id: this.generateId(),
            sender: 'user',
            content: text,
            timestamp: new Date()
        });

        this.inputMessage = '';
        this.isLoading = true;
        this.cdr.detectChanges(); // Force le rafraîchissement

        try {
            // 2. Appel API
            const response = await this.apiService.sendMessage(text, this.userEmail);

            // 3. Ajouter la réponse de l'agent
            this.messages.push({
                id: this.generateId(),
                sender: 'agent',
                content: response,
                timestamp: new Date()
            });
        } catch (error) {
            this.messages.push({
                id: this.generateId(),
                sender: 'agent',
                content: '❌ Désolé, une erreur est survenue. Vérifiez que les serveurs Backend et AI Service sont lancés.',
                timestamp: new Date()
            });
        } finally {
            this.isLoading = false;
            this.cdr.detectChanges(); // Force le rafraîchissement pour enlever le loading
        }
    }

    onKeyPress(event: KeyboardEvent) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            this.sendMessage();
        }
    }

    // ==========================================
    // MÉTHODES POUR LE NOUVEAU DESIGN
    // ==========================================

    isUser(m: Message): boolean {
        return m.sender === 'user';
    }

    time(m: Message): string {
        return new Date(m.timestamp ?? Date.now())
            .toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

    images(text: string): string[] {
        return text?.match(this.IMG_RE) ?? [];
    }

    payUrl(text: string): string | null {
        return text?.match(/https?:\/\/(?:www\.)?(?:sandbox\.)?paypal\.com\/[^\s*)"]+/i)?.[0] ?? null;
    }

    formatMessage(text: string): string {
        if (!text) return '';
        
        // On nettoie le texte (on retire les liens d'images et PayPal bruts pour ne pas les afficher en double)
        const clean = text
            .replace(this.IMG_RE, '')
            .replace(this.PAY_RE, '')
            .replace(/\*{2}\s*\*{2}/g, '')
            .replace(/🔗/g, '')
            .trim();
            
        // On convertit le Markdown en HTML (gras, sauts de ligne)
        return clean
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
            .replace(/\n/g, '<br>');
    }

    useSuggestion(text: string) {
        this.inputMessage = text;
        this.sendMessage();
    }
}