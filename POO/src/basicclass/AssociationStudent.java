package basicclass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ihm.AffinityWeights;
import ihm.MainApp;
import utils.HistoryConstraintChecker;

import java.io.Serializable;
/**
 * La classe représente une association entre deux "Students"
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 2.0
 */
public class AssociationStudent implements Serializable {
    private final Student HOST;
    private final Student GUEST;
    private String invalidReason= null;
    
    /**
     * Représente le score d'une affinité entre un hôte et un invité. Plus le score est haut, moins l'affinité est forte. L'attribut peut être nul si l'association est impossible.
     * @since 1.0
     */
    private Integer scoreAssociation;

    /**
     * Constructeur principal de la classe AssociationStudent
     * @param hote L'étudiant hôte
     * @param guest L'étudiant invité
     * @since 1.0
     */
    public AssociationStudent(Student hote, Student guest) {
        this.HOST = hote;
        this.GUEST = guest;
        this.scoreAssociation = 0;
    }
    
    
    /**
     * Méthode static utilisée pour l'ajout du poids lors de la création du graphe biparti
     * @param hote l'étudiant hote
     * @param guest l'étudiant invité
     * @return Retourne un score de type Double représentant l'affinité entre l'hôte et l'invité ou -999.9 si incompatible.
     */
    public static Double doubleTypeScore(Student hote, Student guest) {
        AssociationStudent association = new AssociationStudent(hote, guest);
        Integer a = association.getScoreAssociation();
        if (a == null)
            return 999.9;
        else
            return Double.valueOf(a.doubleValue());
    } 
    
    /**
     * Permet d'ajouter la valeur passée en paramètres à l'attribut scoreAssociation
     * @param scoreAssociation La valeur de type Integer qui va être ajoutée au score
     * @since 1.0
     */
    public void setScoreAffinity(Integer score) {
        if(score==null){
            this.scoreAssociation=null; 
        }else{ 
            this.scoreAssociation +=score;
        }
    }

    /**
     * Permet de vérifier si le régime alimentaire de l'invité est compatible avec les repas servis par l'hôte
     * @return Retourne true si l'hôte repose le régime nécessaire à l'invité
     * @since 1.0
     */

    public boolean foodCompatibility() {
      
        String guestFoodString = GUEST.getConstraintsMap().get(Constraints.GUEST_FOOD);
        String hostFoodString = HOST.getConstraintsMap().get(Constraints.HOST_FOOD);
        if (guestFoodString == null || guestFoodString.isEmpty()) {
            return true;
        }
        if (hostFoodString == null || hostFoodString.isEmpty()) {
            return false;
        }
        List<String> guestFoods = Arrays.asList(guestFoodString.split(","));
        List<String> hostFoods = Arrays.asList(hostFoodString.split(","));

        for (String food : guestFoods) {
        if (hostFoods.contains(food)) {
            return true;
        }
    }

        return false;
}
    /**
     * Permet d'avoir le malus de chaque hobby différents entre l'invité et l'hôte
     * @return Retourne la valeur de type Integer, représentant le malus entre un hôte et un invité selon leurs hobbys.
     * @since 1.0
     */

    public Integer scoreHobbie() {
        AffinityWeights w= AffinityWeights.getInstance();
        final int COST_OF_HAVING_DIFF_HOBBIE = w.getCostOfHavingDiffHobbie();
        final int COST_OF_HAVING_LESS_HOBBIE = w.getCostOfHavingLessHobbie();
        List<String> hoteHobbies = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOBBIES).split(","));
        List<String> guestHobbies = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.HOBBIES).split(","));

        List<String> smaller = hoteHobbies.size() <= guestHobbies.size() ? hoteHobbies : guestHobbies;
        List<String> larger = hoteHobbies.size() > guestHobbies.size() ? hoteHobbies : guestHobbies;

        int score = 0;
        if (hoteHobbies.size() != guestHobbies.size()) {
            score += Math.abs(hoteHobbies.size()-guestHobbies.size())* COST_OF_HAVING_LESS_HOBBIE;
        }
        for (String hobbie : smaller) {
            if (!larger.contains(hobbie)) {
                score += COST_OF_HAVING_DIFF_HOBBIE;
            }
        }
        return score;
    }

    /**
     * Méthode implémentant la contrainte d'avoir au moins 1 hobbie en commun entre l'hôte et l'invité, si l'un des deux est français.
     * @return Retourne true si l'hôte et l'invité n'ont pas de hobby en commun, false sinon.
     */
    public Boolean laFranceEstReloue(){ 
        if(HOST.getCountry().equals(Country.FR)|| GUEST.getCountry().equals(Country.FR)){
            List<String> hoteHobbies = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOBBIES).split(","));
            List<String> guestHobbies = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.HOBBIES).split(","));
            List<String> commonHobbieList = new ArrayList<>(hoteHobbies);
            commonHobbieList.retainAll(guestHobbies);
            return (commonHobbieList.isEmpty());
        }else{
            return false;
        }
    }

    /**
     * Fonction principale du calcul du score d'affinité entre un hôte et un invité
     * @param historique L'historique des appariements précédents, utilisé pour vérifier les contraintes d'association.
     * @since 1.0
     */

    public void scoreAffinity() {
        HistoryConstraintChecker.result histoResult = HistoryConstraintChecker.checkHistoryConstraint(HOST, GUEST, MainApp.historyManager);
        AffinityWeights w= AffinityWeights.getInstance();
        if(histoResult.equals(HistoryConstraintChecker.result.OTHER)){
            this.setScoreAffinity(w.getHistoryOtherDetected());
            this.invalidReason = "déjà appariés dans le passé et l'un d'eux ne souhaite pas être apparié à nouveau.";
        }else if(HOST.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL).equals("yes") && GUEST.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY).equals("yes")){
            this.setScoreAffinity(w.getAnimalAllergy());
            this.invalidReason = "l'hôte a un animal et l'invité est allergique.";
        }else if (!this.foodCompatibility()){
            this.setScoreAffinity(w.getRegimeRestriction());
            this.invalidReason = "régime alimentaire incompatible.";
        }else if(this.laFranceEstReloue()){
            this.setScoreAffinity(w.getFranceRule());
            this.invalidReason = "l'un des deux est français et ont aucun hobby en commun.";
        }else{
            if(histoResult.equals(HistoryConstraintChecker.result.BONUS)){
                this.setScoreAffinity(w.getBonusHistory());
            }
            if(HOST.getAge()==GUEST.getAge()){
                this.setScoreAffinity(w.getSameAge());
            }else if((HOST.getAge()-GUEST.getAge())>2 &&(HOST.getAge()-GUEST.getAge())<5){
                this.setScoreAffinity(w.getAgeBetween2And5());
            }else{
                this.setScoreAffinity(w.getAgeSuperior5());
            }
            if(!HOST.getGender().equals(GUEST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(w.getDifferentGender());
            }
            if(!GUEST.getGender().equals(HOST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(w.getDifferentGender());
            }
            this.setScoreAffinity(this.scoreHobbie());
        }
        
    }
    
    /**
     * Méthode permettant de décrire le niveau d'affinité entre l'hôte et l'invité
     * en fonction du score d'association calculé.
     * @return Retourne une chaîne de caractères décrivant le niveau d'affinité.
     */
    public String describeLevelOfAffinity(){
        String levelAffinityString ="";
        if(this.scoreAssociation==null){
            levelAffinityString="Association impossible contrainte non respecté";
        }else{
            int score = (int) this.scoreAssociation;
            if(score >= -3 && score <3) levelAffinityString="Forte affinité";
            else if(score >=3 && score <8) levelAffinityString="Moyenne affinité";
            else levelAffinityString="Faible affinité";
        }
        return levelAffinityString;
    }

    /**
     * Méthode toString pour afficher les informations de l'association entre l'hôte et l'invité
     * @return Retourne une chaîne de caractères formatée représentant l'association sous la forme suivante :
     * `"HOST | SCORE | GUEST | LEVEL OF AFFINITY"`
     */
    @Override
    public String toString() {
        Integer score = this.getScoreAssociation();
        String scoreStr;
        if (score == null) {
            scoreStr = "null";
        } else {
            scoreStr = score.toString();
        }
        // Format tabulaire minimaliste
        return String.format("%-30s | %-5s | %-30s | %s", HOST.toString(), scoreStr, GUEST.toString(), this.describeLevelOfAffinity());
    }

    /**
     * Méthode toString pour afficher en détails les informations de l'association entre l'hôte et l'invité
     * @param full Si true, affiche les informations complètes de l'association, sinon affiche un format minimaliste.
     * @return Retourne une chaîne de caractères formatée représentant l'association.
     */
    public String toString(boolean full){
        Integer score = this.getScoreAssociation();
        String scoreStr;
        if (score == null) {
            scoreStr = "null";
        } else {
            scoreStr = score.toString();
        }
        if(full){
            return String.format("%-50s | %-5s | %-50s | %s",HOST.toStringComplete(), scoreStr, GUEST.toStringComplete(), this.describeLevelOfAffinity());
        }else{
            return this.toString();
        }
    }

    /**
     * Fonction pour obtenir l'hôte de l'association
     * @return L'hôte de type Student
     * @since 1.0
     */
    public Student getHost() {
        return this.HOST;
    }

    /**
     * Fonction pour obtenir l'invité de l'association
     * @return L'invité de type Student
     * @since 1.0
     */
    public Student getGuest() {
        return this.GUEST;
    }
    
    /**
     * Fonction pour obtenir le scoreAssociation
     * @return Le scoreAssociation de type Integer
     * @since 1.0
     */

    public Integer getScoreAssociation() {
        this.scoreAssociation=0;
        this.scoreAffinity();
        return this.scoreAssociation;
    }
    /**
     * Retourne le motif d'impossibilité si l'association est invalide.
     * @return Le motif, ou null si l'association est valide.
     */
    public String getInvalidReason() {
        return invalidReason;
    }
}