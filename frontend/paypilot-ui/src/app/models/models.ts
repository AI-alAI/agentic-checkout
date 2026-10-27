export interface ChatRequest {
    message: string;
    user_email: string;
}

export interface ChatResponse {
    response: string;
    success: boolean;
}

export interface Message {
    id: string;
    sender: 'user' | 'agent';
    content: string;
    timestamp: Date;
}