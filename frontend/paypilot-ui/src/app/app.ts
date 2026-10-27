import { Component } from '@angular/core';
import { ChatComponent } from './components/chat/chat.component';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [ChatComponent],
    templateUrl: './app.html',
    styleUrls: ['./app.css']
})
export class App {
    title = 'PayPilot AI';
}