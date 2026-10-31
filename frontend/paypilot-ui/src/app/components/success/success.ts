import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import axios from 'axios';

@Component({
  selector: 'app-success',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './success.html',
  styleUrls: ['./success.css']
})
export class Success implements OnInit {
  status = 'loading';
  message = 'Vérification de votre paiement avec PayPal...';

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    console.log('🔍 Page success chargée');
    
    this.route.queryParams.subscribe(params => {
      console.log('🔍 Paramètres URL :', params);
      const paypalOrderId = params['token'];
      
      if (paypalOrderId) {
        console.log('🔍 Token trouvé :', paypalOrderId);
        
        // 1. ON AFFICHE LE SUCCÈS IMMÉDIATEMENT (pour la vidéo et l'UX)
        this.status = 'success';
        this.message = `Paiement confirmé ! Votre commande PayPal (ID: ${paypalOrderId}) a été traitée avec succès.`;
        
        // 2. On tente la capture en arrière-plan (pour mettre à jour MySQL)
        this.capturePaymentInBackground(paypalOrderId);
        
      } else {
        this.status = 'error';
        this.message = 'Aucun identifiant de paiement trouvé dans l\'URL.';
      }
    });
  }

  async capturePaymentInBackground(paypalOrderId: string) {
    try {
      console.log('🚀 Capture en arrière-plan pour :', paypalOrderId);
      
      const response = await axios.post(
        `http://localhost:8080/api/paypal/capture/${paypalOrderId}`
      );
      
      console.log('✅ Réponse backend :', response.data);
      
    } catch (error: any) {
      // On ne bloque pas l'affichage si la capture échoue (ex: déjà capturé)
      console.log('ℹ️ Capture déjà effectuée ou erreur réseau (non bloquant)');
    }
  }

  goBack() {
    this.router.navigate(['/']);
  }
}