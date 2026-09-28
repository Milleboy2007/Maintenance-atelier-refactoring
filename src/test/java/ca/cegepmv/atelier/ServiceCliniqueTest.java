package ca.cegepmv.atelier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 *
 * Cette suite de tests est VOLONTAIREMENT INCOMPLÈTE. Les 4 tests ci-dessous passent
 * déjà et couvrent seulement une partie du comportement de {@link ServiceClinique}.
 *
 * Avant de réusiner quoi que ce soit (voir README, Partie 3), complétez cette classe
 * (Partie 2) avec des tests de CARACTÉRISATION qui documentent le comportement ACTUEL
 * du code (pas celui que vous jugeriez "idéal") pour :
 *
 *   1. genererFacture() — le cas VACCIN, le cas où le rabais de fidélité s'applique
 *      (2 animaux ou plus), et le cas de pénalité de solde négatif.
 *   2. calculerRemiseFidelite() — les 3 branches non couvertes (0.15, 0.10, 0.05).
 *   3. estimerCoutAnnuel() — au moins un cas avec plusieurs visites de types différents.
 *   4. calculerDureeRendezVous() — au moins les 4 types (CONSULTATION, VACCIN,
 *      CHIRURGIE, type inconnu).
 *   5. genererRapportPourVeterinaire() — au moins un cas normal.
 *   6. planifierVisite() — le cas avec rappel par courriel activé ET désactivé
 *      (vérifiez le contenu de getJournalRappels()).
 *
 * N'ajoutez PAS de test pour un comportement que vous jugez incorrect (ex. le tarif
 * silencieux à 0$ pour un type inconnu) sans d'abord relire l'avertissement dans
 * ServiceClinique — ce comportement est un piège intentionnel de l'atelier.
 */
class ServiceCliniqueTest {

    private ServiceClinique service;
    private Proprietaire julie;

    @BeforeEach
    void setUp() {
        service = new ServiceClinique();
        // Arrange commun : propriétaire avec un seul animal, solde à jour, cliente depuis 1 an
        julie = new Proprietaire("Julie", "Tremblay", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 1);
        julie.ajouterAnimal(new Animal(1, "Rex", "CHIEN", LocalDate.of(2020, 3, 15)));
    }

    // ------------------------------------------------------------------
    // Tests fournis (déjà complets, à titre d'exemple — NE PAS MODIFIER)
    // ------------------------------------------------------------------

    @Test
    void genererFacture_uneConsultationSansUrgence_calculeLeBonSousTotal() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $60.00 — Total: $60.00", facture);
    }

    @Test
    void genererFacture_chirurgieUrgente_appliqueLeMultiplicateurDurgence() {
        // Arrange
        Visite chirurgieUrgente = new Visite(LocalDate.now(), "CHIRURGIE", true, "Dr Gagnon", "Urgence");

        // Act
        String facture = service.genererFacture(julie, List.of(chirurgieUrgente));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $375.00 — Total: $375.00", facture);
    }

    @Test
    void genererFacture_sansVisite_lanceUneException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.genererFacture(julie, List.of()));
    }

    @Test
    void calculerRemiseFidelite_unSeulAnimalEtPeuAnciennete_retourneZero() {
        // Act
        double remise = service.calculerRemiseFidelite(julie);

        // Assert
        assertEquals(0.0, remise);
    }

    // ------------------------------------------------------------------
    // À COMPLÉTER — voir README, Partie 2 (tests de caractérisation)
    // ------------------------------------------------------------------

    // TODO 1 — genererFacture() : cas VACCIN (tarif de base 35$, sans urgence)
    @Test
    void genererFacture_Vaccin_SansUrgence(){
        Visite visite = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence");

        String facture = service.genererFacture(julie, List.of(visite));

        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Total: $35.00", facture);
    }

    // TODO 2 — genererFacture() : propriétaire avec 2 animaux ou plus (rabais de 10%
    //          appliqué, et présent dans le texte de la facture — vérifiez le format
    //          exact affiché par le code actuel avant d'écrire votre assertion)
    @Test
    void genererFacture_Rabais_Pour_Deux_Animaux(){
        julie.ajouterAnimal(new Animal(2, "Ta Anh", "CHIEN", LocalDate.of(2026, 9, 28)));

        Visite visite = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence");

        String facture = service.genererFacture(julie, List.of(visite));

        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Remise fidélité: -10% — Total: $31.50", facture);
    }

    // TODO 3 — genererFacture() : propriétaire avec un solde négatif (pénalité de 5%)
    @Test
    void genererFacture_SoldeNegatif(){
        Proprietaire aicha = new Proprietaire("Aicha", "Rym", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", -10.0, 1);
        aicha.ajouterAnimal(new Animal(2, "Ta Anh", "CHIEN", LocalDate.of(2026, 9, 28)));

        Visite visite = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence");

        String facture = service.genererFacture(aicha, List.of(visite));

        assertEquals("Facture pour Aicha Rym — Sous-total: $35.00 — Total: $36.75", facture);
    }

    // TODO 4 — calculerRemiseFidelite() : plus d'un animal ET anneesClient >= 3 (0.15)
    @Test
    void calculerRemiseFidelite_PlusUnAnimal_PlusTroisAnnee(){
        Proprietaire TaAnh = new Proprietaire("Ta", "Anh", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 4);
        TaAnh.ajouterAnimal(new Animal(2, "Ta Anh", "CHIEN", LocalDate.of(2026, 9, 28)));
        TaAnh.ajouterAnimal(new Animal(2, "Geronymo", "CHIEN", LocalDate.of(2026, 9, 28)));

        Double remise = service.calculerRemiseFidelite(TaAnh);

        assertEquals(0.15, remise);

    }

    // TODO 5 — calculerRemiseFidelite() : plus d'un animal ET anneesClient < 3 (0.10)
    @Test
    void calculerRemiseFidelite_PlusUnAnimal_Moin3Anne(){
        Proprietaire Angel = new Proprietaire("Angle", "Monteze", "123 rue des Lilas", "Longueuil",
        "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 2);
        Angel.ajouterAnimal(new Animal(2, "Ta Anh", "CHIEN", LocalDate.of(2026, 9, 28)));
        Angel.ajouterAnimal(new Animal(2, "Geronymo", "CHIEN", LocalDate.of(2026, 9, 28)));

        Double remise = service.calculerRemiseFidelite(Angel);

        assertEquals(0.10, remise);
    }

    // TODO 6 — calculerRemiseFidelite() : un seul animal ET anneesClient >= 5 (0.05)
    @Test
    void calculerRemiseFidelite_UnAnimal_PlusCinqAnne(){
        Proprietaire Caroline = new Proprietaire("Caroline", "Preston", "123 rue des Lilas", "Longueuil",
        "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 6);
        Caroline.ajouterAnimal(new Animal(2, "Ta Anh", "CHIEN", LocalDate.of(2026, 9, 28)));

        Double remise = service.calculerRemiseFidelite(Caroline);

        assertEquals(0.05, remise);
    }

    // TODO 7 — estimerCoutAnnuel() : plusieurs visites de types différents
    @Test 
    void estimerCoutAnnuel_PlusieurVisiteDifferentTypes(){
        Visite visite1 = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence");
        Visite visite2 = new Visite(LocalDate.now(), "CHIRURGIE", false, "Dr Gagnon", "Sans Urgence");

        Double total = service.estimerCoutAnnuel(List.of(visite1, visite2));

        assertEquals(3420.0, total);        
    }

    // TODO 8 — calculerDureeRendezVous() : CONSULTATION, VACCIN, CHIRURGIE, et un type
    //          inconnu (documentez le comportement actuel, ne le "corrigez" pas)
    @Test
    void calculerDureeRendezVous(){
        
        assertAll("",
            () -> assertEquals(20, service.calculerDureeRendezVous(new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Sans Urgence"))),
            () -> assertEquals(10, service.calculerDureeRendezVous(new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence"))),
            () -> assertEquals(90, service.calculerDureeRendezVous(new Visite(LocalDate.now(), "CHIRURGIE", false, "Dr Gagnon", "Sans Urgence"))),
            () -> assertEquals(30, service.calculerDureeRendezVous(new Visite(LocalDate.now(), "INVALIDE", false, "Dr Gagnon", "Sans Urgence")))
        );
    }
    // TODO 9 — genererRapportPourVeterinaire() : un cas normal avec un Veterinaire,
    //          un Proprietaire et une liste de visites
    @Test 
    void genererRapportVeterinaire_Normal(){
        Veterinaire vet = new Veterinaire("Jack", "CHIRURGIE");
        Visite visite = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Sans Urgence");

        String rapport = service.genererRapportPourVeterinaire(vet, julie, List.of(visite));
        
        assertEquals("Rapport du Dr Jack (CHIRURGIE)\nClient: Julie Tremblay\nAdresse: 123 rue des Lilas, Longueuil J4G 1A1\nContact: julie.tremblay@example.com / 450-555-0100\nNombre de visites: 1", rapport);
    }
    // TODO 10 — planifierVisite() : un appel avec envoyerRappelEmail=true (vérifiez le
    //           contenu de service.getJournalRappels()) ET un appel avec
    //           envoyerRappelEmail=false (le journal doit rester vide)
    @Test
    void planifierVisiteTest(){
        LocalDate date = LocalDate.now();

        service.planifierVisite(1, date, "CONSULTATION", false, "Dr Gagnon", "Notes", false);
        
        assertTrue(service.getJournalRappels().isEmpty());
        
        service.planifierVisite(1, date, "CONSULTATION", false, "Dr Gagnon", "Notes", true);
 
        assertEquals(1, service.getJournalRappels().size());
        assertEquals("Rappel envoyé pour la visite du " + date + " (animal #1)", service.getJournalRappels().get(0));
    }
}
