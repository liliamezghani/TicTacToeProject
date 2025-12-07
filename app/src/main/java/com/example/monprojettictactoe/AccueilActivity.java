package com.example.monprojettictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable; // Import nécessaire pour TournamentResult

// La classe utilitaire TournamentResult a été retirée de ce fichier
// car elle existe déjà dans votre projet sous le nom TournamentResult.java.
// Assurez-vous que la classe TournamentResult est définie comme suit dans son propre fichier :
/*
class TournamentResult implements Serializable {
    public int scoreX;
    public int scoreO;
    public int draws;
    public int totalGames;
    public String winner;

    public TournamentResult(int scoreX, int scoreO, int draws, int totalGames, String winner) {
        this.scoreX = scoreX;
        this.scoreO = scoreO;
        this.draws = draws;
        this.totalGames = totalGames;
        this.winner = winner;
    }
}
*/

public class AccueilActivity extends AppCompatActivity {

    // Constante pour le nom du fichier de sauvegarde
    private static final String FILENAME = "tournament_result.ser";

    // Clés pour passer les données via Intent à MainActivity
    public static final String EXTRA_SYMBOLE = "com.example.tictactoe.SYMBOLE";
    public static final String EXTRA_NB_PARTIES = "com.example.tictactoe.NB_PARTIES";

    // Éléments UI
    private RadioGroup rgSymbole;
    private RadioGroup rgNbParties;
    private Button btnJouer;
    private Button btnPrincipe;
    private Button btnAfficherScores;
    private TextView tvScoreStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Utilise le layout XML activity_accueil (assurez-vous qu'il existe dans res/layout)
        setContentView(R.layout.activity_accueil);

        // 1. Initialisation des éléments UI (Utilisation des ID de votre image/projet)
        // NOTE: Les ID ci-dessous sont hypothétiques et doivent correspondre à ceux de votre fichier activity_accueil.xml.
        rgSymbole = findViewById(R.id.rg_symbole);
        rgNbParties = findViewById(R.id.rg_nb_parties);
        btnJouer = findViewById(R.id.btn_jouer);
        btnPrincipe = findViewById(R.id.btn_principe);
        btnAfficherScores = findViewById(R.id.btn_afficher_scores);
        tvScoreStatus = findViewById(R.id.tv_score_status);

        // 2. Vérifier l'existence d'un score sauvegardé au démarrage
        updateScoreStatus();

        // 3. Gestion des clics
        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lancerNouvellePartie();
            }
        });

        btnPrincipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherPrincipeJeu();
            }
        });

        btnAfficherScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherScoresSauvegardes();
            }
        });
    }

    /**
     * Lit les sélections de l'utilisateur et lance le MainActivity.
     */
    private void lancerNouvellePartie() {
        // --- Récupération du symbole (X ou O) ---
        int selectedSymboleId = rgSymbole.getCheckedRadioButtonId();
        String symbole = "X"; // X par défaut
        // R.id.rb_symbole_o est l'ID supposé du bouton O dans rg_symbole
        if (selectedSymboleId == R.id.rb_symbole_o) {
            symbole = "O";
        }

        // --- Récupération du nombre de parties ---
        int selectedPartiesId = rgNbParties.getCheckedRadioButtonId();
        int nbParties = 5; // 5 par défaut

        // R.id.rb_10_parties et R.id.rb_15_parties sont les ID supposés des boutons
        if (selectedPartiesId == R.id.rb_10_parties) {
            nbParties = 10;
        } else if (selectedPartiesId == R.id.rb_15_parties) {
            nbParties = 15;
        }

        // --- Lancement de l'activité de jeu ---
        Intent intent = new Intent(AccueilActivity.this, MainActivity.class);
        intent.putExtra(EXTRA_SYMBOLE, symbole);
        intent.putExtra(EXTRA_NB_PARTIES, nbParties);
        startActivity(intent);
    }

    /**
     * Affiche une boîte de dialogue avec le principe du jeu.
     */
    private void afficherPrincipeJeu() {
        // Utilisation du constructeur simple pour éviter le conflit de style
        new AlertDialog.Builder(this)
                .setTitle("Principe du Jeu X-O (Tic-Tac-Toe)")
                .setMessage("Le jeu se joue sur une grille 3x3. Deux joueurs s'affrontent, l'un choisissant X et l'autre O. Le but est d'aligner trois symboles identiques (ligne, colonne ou diagonale). Le tournoi cumule les scores sur le nombre de parties sélectionné (5, 10 ou 15).")
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Lit l'objet TournamentResult depuis le fichier et affiche les scores.
     */
    private void afficherScoresSauvegardes() {
        TournamentResult result = lireDernierTournoi(this);

        if (result != null) {
            String message = String.format(
                    "Résultats du Dernier Tournoi (sur %d parties):\n\n" +
                            "Score X: %d\n" +
                            "Score O: %d\n" +
                            "Parties Nulles: %d\n\n" +
                            "Vainqueur du Tournoi: %s",
                    result.getTotalGames(),
                    result.getScoreX(),
                    result.getScoreO(),
                    result.draws,
                    result.getWinner() != null && !result.getWinner().isEmpty() ? result.getWinner() : "Égalité"
            );

            // Utilisation du constructeur simple pour éviter le conflit de style
            new AlertDialog.Builder(this)
                    .setTitle("Scores Sauvegardés")
                    .setMessage(message)
                    .setPositiveButton("Fermer", null)
                    .show();
        } else {
            Toast.makeText(this, "Aucun tournoi sauvegardé trouvé.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Met à jour le TextView pour indiquer si un score existe.
     */
    private void updateScoreStatus() {
        TournamentResult result = lireDernierTournoi(this);
        if (result != null) {
            tvScoreStatus.setText("DERNIER TOURNOI: " + result.getWinner());
            // Assurez-vous que les couleurs sont définies si vous les utilisez (ou utilisez des codes hexadécimaux)
            tvScoreStatus.setTextColor(Color.parseColor("#39FF14")); // Néon Vert
        } else {
            tvScoreStatus.setText("AUCUN TOURNOI SAUVEGARDÉ");
            tvScoreStatus.setTextColor(Color.parseColor("#C77DFF")); // Néon Violet
        }
    }

    /**
     * Fonction utilitaire pour lire l'objet sérialisé.
     */
    public static TournamentResult lireDernierTournoi(Context context) {
        TournamentResult result = null;
        try (FileInputStream fis = context.openFileInput(FILENAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            result = (TournamentResult) ois.readObject();

        } catch (Exception e) {
            // Fichier non trouvé ou erreur de lecture (c'est normal si c'est la première exécution)
            System.err.println("Erreur de lecture du fichier de tournoi : " + e.getMessage());
            // e.printStackTrace(); // Utile pour le debug, moins pour le code final
        }
        return result;
    }
}