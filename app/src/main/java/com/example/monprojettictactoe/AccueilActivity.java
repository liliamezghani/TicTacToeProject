package com.example.monprojettictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class AccueilActivity extends AppCompatActivity {

    private RadioButton rbX; // Bouton radio pour le choix du symbole X
    private Spinner spinnerNbParties; // Sélecteur du nombre de parties

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lie cette activité au layout activity_accueil.xml
        setContentView(R.layout.activity_accueil);

        // Initialisation des composants UI
        Button btnJouer = findViewById(R.id.btn_jouer);
        Button btnPrincipe = findViewById(R.id.btn_principe);
        Button btnScores = findViewById(R.id.btn_scores);
        rbX = findViewById(R.id.rb_x);
        spinnerNbParties = findViewById(R.id.spinner_nb_parties);

        // 1. Action Bouton Jouer : Lance l'activité de jeu (MainActivity)
        btnJouer.setOnClickListener(v -> {
            // Récupérer les choix de l'utilisateur
            String symbol = rbX.isChecked() ? "X" : "O";
            String nbPartiesStr = spinnerNbParties.getSelectedItem().toString();
            int nbParties = Integer.parseInt(nbPartiesStr);

            // Création de l'Intent explicite pour la navigation
            Intent intent = new Intent(AccueilActivity.this, MainActivity.class);

            // Passage des données au MainActivity
            intent.putExtra("PLAYER_SYMBOL", symbol);
            intent.putExtra("MAX_GAMES", nbParties);

            startActivity(intent);
        });

        // 2. Action Bouton Principe : Affiche une boîte de dialogue d'information
        btnPrincipe.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Principe du jeu X-O")
                    .setMessage("Alignez trois symboles identiques (X ou O) sur une ligne, une colonne ou une diagonale pour gagner une partie. Le tournoi se termine après le nombre de parties sélectionné.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // 3. Action Bouton Retrouver Scores : Lit le fichier interne sérialisé
        btnScores.setOnClickListener(v -> lireDernierTournoi());
    }

    /**
     * Lit et affiche le dernier résultat de tournoi sauvegardé (par désérialisation).
     */
    private void lireDernierTournoi() {
        try {
            // Ouvrir le fichier en lecture
            FileInputStream fis = openFileInput("last_tournament.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Désérialiser l'objet TournamentResult
            TournamentResult result = (TournamentResult) ois.readObject();

            // Fermeture des flux
            ois.close();
            fis.close();

            // Construction du message à afficher
            String message = "Score Joueur X : " + result.getScoreX() + "\n" +
                    "Score Joueur O : " + result.getScoreO() + "\n" +
                    "Parties nulles : " + result.getDrawCount() + "\n" +
                    "Total parties : " + result.getTotalGames() + "\n\n" +
                    "Vainqueur du tournoi : " + result.getWinner();

            // Affichage des scores dans une AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("Dernier Tournoi Sauvegardé")
                    .setMessage(message)
                    .setPositiveButton("Fermer", null)
                    .show();

        } catch (Exception e) {
            // Gestion de l'erreur si le fichier n'existe pas ou ne peut pas être lu
            Toast.makeText(this, "Aucun tournoi sauvegardé", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}