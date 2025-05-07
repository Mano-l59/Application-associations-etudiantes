/**
 * La classe représente une association entre deux "Students"
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
package basicclass;
import java.util.Arrays;
import java.util.List;

public class AssociationStudent {
    private final Student HOST;
    private final Student GUEST;
    
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
     * Permet d'ajouter la valeur passée en paramètres à l'attribut scoreAssociation
     * @param scoreAssociation La valeur de type Integer qui va être ajoutée au score
     * @since 1.0
     */
    private void setScoreAffinity(Integer scoreAssociation) {
        if(scoreAssociation==null){
            this.scoreAssociation=null; 
        }else{ 
            this.scoreAssociation = this.scoreAssociation+scoreAssociation;
        }
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
     * Permet de vérifier si le régime alimentaire de l'invité est compatible avec les repas servis par l'hôte
     * @return Retourne true si l'hôte repose le régime nécessaire à l'invité
     * @since 1.0
     */

    public boolean foodCompatibility() {
      
        List<String> hoteFood = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOST_FOOD).split(";"));
        List<String> guestFood = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.GUEST_FOOD).split(";"));
        if(guestFood.isEmpty()){
            return true;
        }
        return hoteFood.containsAll(guestFood);
    }

    /**
     * Permet d'avoir le malus de chaque hobby différents entre l'invité et l'hôte
     * @return Retourne la valeur de type Integer, représentant le malus entre un hôte et un invité selon leurs hobbys.
     * @since 1.0
     */

    public Integer scoreHobbie(){
        final int COST_OF_HAVING_DIFF_HOBBIE=3;
        int cpt=0;
        int higherSize;
        List<String> hoteHobbies = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOBBIES).split(";"));
        List<String> guestHobbies = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.HOBBIES).split(";"));
        
        for(String h_hobbie : hoteHobbies){
            for(String g_hobbie : guestHobbies){
                if (h_hobbie.equals(g_hobbie)) cpt++;
            }
        }
        if(hoteHobbies.size()>=guestHobbies.size()) higherSize=hoteHobbies.size();
        else higherSize=hoteHobbies.size();

        return (higherSize-cpt)*COST_OF_HAVING_DIFF_HOBBIE;
    }

    /**
     * Fonction principale du calcul du score d'affinité entre un hôte et un invité
     * @since 1.0
     */

    private void scoreAffinity() {
        if(HOST.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL).equals("yes") && GUEST.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY).equals("yes")){
           this.setScoreAffinity(null);
        }else if (!this.foodCompatibility()){
            this.setScoreAffinity(null);
        }else{
            if(HOST.getAge()==GUEST.getAge()){
                this.setScoreAffinity(-1);
            }else if((HOST.getAge()-GUEST.getAge())>2 &&(HOST.getAge()-GUEST.getAge())<5){
                this.setScoreAffinity(2);
            }else{
                this.setScoreAffinity(5);
            }
            if(!HOST.getGender().equals(GUEST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(1);
            }
            if(!GUEST.getGender().equals(HOST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(1);
            }
            this.setScoreAffinity(this.scoreHobbie());
        }
    }
    public String describeLevelOfAffinity(Integer score){
        String levelAffinityString ="";
        if(score==null){
            levelAffinityString="Association impossible contrainte non respecté";
        }else{
            score = (int) score;
            if(score >= -1 && score <3) levelAffinityString="Forte affinité";
            else if(score >=3 && score <9) levelAffinityString="Moyenne affinité";
            else levelAffinityString="Faible affinité";
        }
        return levelAffinityString;
    }
    public String toString(boolean full){
        if(full) return HOST.toStringComplete()+"\n\tEST L'INVITE SUIVANT CHEZ LUI :\n"+GUEST.toStringComplete()+"\n L'association produit un score de : "+this.getScoreAssociation().toString()+"\n On qualifie alors l'association de : "+this.describeLevelOfAffinity(this.scoreAssociation);
        else return HOST.toString()+"\n\tEST L'INVITE SUIVANT CHEZ LUI :\n"+GUEST.toString()+"\n L'association produit un score de : "+this.getScoreAssociation().toString()+"\n On qualifie alors l'association de : "+this.describeLevelOfAffinity(this.scoreAssociation);
    }

}