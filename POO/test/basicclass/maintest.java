package basicclass;

import java.time.LocalDate;
import java.util.HashMap;

public class maintest {
    public static void main(String[] args) {
        HashMap<Constraints,String> mapContraintes1 = new HashMap<>();mapContraintes1.put(Constraints.GUEST_ANIMAL_ALLERGY, "yes");mapContraintes1.put(Constraints.HOST_HAS_ANIMAL, "no");mapContraintes1.put(Constraints.GUEST_FOOD, "");mapContraintes1.put(Constraints.HOST_FOOD, "");mapContraintes1.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes1.put(Constraints.PAIR_GENDER, "male");mapContraintes1.put(Constraints.HISTORY, "");
        HashMap<Constraints,String> mapContraintes2 = new HashMap<>();mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL, "yes");mapContraintes2.put(Constraints.GUEST_FOOD, "vegan,halal");mapContraintes2.put(Constraints.HOST_FOOD, "vegan,vegetarian");mapContraintes2.put(Constraints.HOBBIES, "musique,lecture");mapContraintes2.put(Constraints.PAIR_GENDER, "female");mapContraintes2.put(Constraints.HISTORY, "");
        HashMap<Constraints,String> mapContraintes4 = new HashMap<>();mapContraintes4.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");mapContraintes4.put(Constraints.HOST_HAS_ANIMAL, "no");mapContraintes4.put(Constraints.GUEST_FOOD, "");mapContraintes4.put(Constraints.HOST_FOOD, "vegan");mapContraintes4.put(Constraints.HOBBIES, "cuisine,dessin");mapContraintes4.put(Constraints.PAIR_GENDER, "female");mapContraintes4.put(Constraints.HISTORY, "");
        Student s1,s2,s4;
        AssociationStudent pairS1_S2,pairS2_S1,pairS4_S2;
        s1= new Student("Sergheraert", "Timothée", "male", LocalDate.of(2006,4,18), "France",mapContraintes1);
        s2= new Student("Lemaire", "Mano", "male", LocalDate.of(2006,10,8), "France",mapContraintes2);
        s4= new Student("PasD'idée", "Béatrice", "female", LocalDate.of(2001,1,31), "Germany",mapContraintes4);
        pairS1_S2 = new AssociationStudent(s1, s2);
        pairS2_S1 = new AssociationStudent(s2, s1);
        pairS4_S2 = new AssociationStudent(s4, s2);
        System.out.println(pairS1_S2.getScoreAssociation());
        System.out.println(pairS1_S2.describeLevelOfAffinity());
        System.out.println(pairS4_S2.getScoreAssociation());
        System.out.println(pairS4_S2.describeLevelOfAffinity());
    }
}
