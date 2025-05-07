package basicclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;

public class testAssociationStudent {
    public Student s1,s2,s3,s4,s5,s6;
    //pair<hote_guest>
    public AssociationStudent pairS1_S2,pairS2_S1,pairS3_S6,pairS4_S5;
    public HashMap<Constraints,String> mapContraintes1,mapContraintes2,mapContraintes3,mapContraintes4,mapContraintes5,mapContraintes6;

    @BeforeEach
    public void initialization() {
        this.mapContraintes1 = new HashMap<>();mapContraintes1.put(Constraints.GUEST_ANIMAL_ALLERGY, "yes");mapContraintes1.put(Constraints.HOST_HAS_ANIMAL, "no");mapContraintes1.put(Constraints.GUEST_FOOD, "");mapContraintes1.put(Constraints.HOST_FOOD, "");mapContraintes1.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes1.put(Constraints.PAIR_GENDER, "male");mapContraintes1.put(Constraints.HISTORY, "");
        this.mapContraintes2 = new HashMap<>();mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL, "yes");mapContraintes2.put(Constraints.GUEST_FOOD, "vegan");mapContraintes2.put(Constraints.HOST_FOOD, "vegan,vegetarian");mapContraintes2.put(Constraints.HOBBIES, "musique,lecture");mapContraintes2.put(Constraints.PAIR_GENDER, "female");mapContraintes2.put(Constraints.HISTORY, "");
        this.mapContraintes3 = new HashMap<>();mapContraintes3.put(Constraints.GUEST_ANIMAL_ALLERGY, "yes");mapContraintes3.put(Constraints.HOST_HAS_ANIMAL, "yes");mapContraintes3.put(Constraints.GUEST_FOOD, "sans-gluten");mapContraintes3.put(Constraints.HOST_FOOD, "sans-gluten,vegan");mapContraintes3.put(Constraints.HOBBIES, "sport,cinéma");mapContraintes3.put(Constraints.PAIR_GENDER, "male");mapContraintes3.put(Constraints.HISTORY, "");
        this.mapContraintes4 = new HashMap<>();mapContraintes4.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");mapContraintes4.put(Constraints.HOST_HAS_ANIMAL, "no");mapContraintes4.put(Constraints.GUEST_FOOD, "");mapContraintes4.put(Constraints.HOST_FOOD, "halal");mapContraintes4.put(Constraints.HOBBIES, "cuisine,dessin");mapContraintes4.put(Constraints.PAIR_GENDER, "female");mapContraintes4.put(Constraints.HISTORY, "");
        this.mapContraintes5 = new HashMap<>();mapContraintes5.put(Constraints.GUEST_ANIMAL_ALLERGY, "yes");mapContraintes5.put(Constraints.HOST_HAS_ANIMAL, "no");mapContraintes5.put(Constraints.GUEST_FOOD, "vegetarian");mapContraintes5.put(Constraints.HOST_FOOD, "vegetarian");mapContraintes5.put(Constraints.HOBBIES, "jeux-vidéo,musique");mapContraintes5.put(Constraints.PAIR_GENDER, "other");mapContraintes5.put(Constraints.HISTORY, "");
        this.mapContraintes6 = new HashMap<>();mapContraintes6.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");mapContraintes6.put(Constraints.HOST_HAS_ANIMAL, "yes");mapContraintes6.put(Constraints.GUEST_FOOD, "kosher");mapContraintes6.put(Constraints.HOST_FOOD, "kosher,vegan");mapContraintes6.put(Constraints.HOBBIES, "lecture,théâtre");mapContraintes6.put(Constraints.PAIR_GENDER, "female");mapContraintes6.put(Constraints.HISTORY, "");

        this.s1= new Student("Sergheraert", "Timothée", "male", LocalDate.of(2006,4,18), "France");
        this.s2= new Student("Lemaire", "Mano", "male", LocalDate.of(2006,10,8), "France");
        this.s3= new Student("Roty", "Clément", "male", LocalDate.of(2006,6,2), "France",mapContraintes3);
        this.s4= new Student("PasD'idée", "Béatrice", "female", LocalDate.of(2001,1,31), "Germany",mapContraintes4);
        this.s5= new Student("ToujoursRien", "Alice", "female", LocalDate.of(1999,7,24), "Italy",mapContraintes5);
        this.s6= new Student("Désesssspoire", "Léonardo", "other", LocalDate.of(2006,6,2), "Listenbourg");
        s1.setConstraintMap(mapContraintes1);
        s2.setConstraintMap(mapContraintes2);
        s6.setConstraintMap(mapContraintes6);

        pairS1_S2=new AssociationStudent(s1, s2);
        pairS2_S1=new AssociationStudent(s2, s1);
        pairS3_S6=new AssociationStudent(s3, s6);
        pairS4_S5=new AssociationStudent(s4, s5);
    }
    
    @Test
    public void testStudentGetAge(){
        
    }
}
