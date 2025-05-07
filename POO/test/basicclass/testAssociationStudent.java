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
    public AssociationStudent pair1,pair2,pair3;
    public HashMap<Constraints,String> mapContraintes1,mapContraintes2,mapContraintes3,mapContraintes4,mapContraintes5,mapContraintes6;

    @BeforeEach
    public void initialization() {
        this.mapContraintes1=new HashMap<>();
        mapContraintes1.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes1.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes1.put(Constraints.GUEST_FOOD,"");
        mapContraintes1.put(Constraints.HOST_FOOD,"");mapContraintes1.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes1.put(Constraints.PAIR_GENDER, "male");mapContraintes1.put(Constraints.HISTORY, "");
        this.mapContraintes2=new HashMap<>();
        mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes2.put(Constraints.GUEST_FOOD,"");
        mapContraintes2.put(Constraints.HOST_FOOD,"");mapContraintes2.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes2.put(Constraints.PAIR_GENDER, "male");mapContraintes2.put(Constraints.HISTORY, "");
        this.mapContraintes3=new HashMap<>();
        mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes2.put(Constraints.GUEST_FOOD,"");
        mapContraintes2.put(Constraints.HOST_FOOD,"");mapContraintes2.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes2.put(Constraints.PAIR_GENDER, "male");mapContraintes2.put(Constraints.HISTORY, "");
        this.mapContraintes2=new HashMap<>();
        mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes2.put(Constraints.GUEST_FOOD,"");
        mapContraintes2.put(Constraints.HOST_FOOD,"");mapContraintes2.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes2.put(Constraints.PAIR_GENDER, "male");mapContraintes2.put(Constraints.HISTORY, "");
        this.mapContraintes2=new HashMap<>();
        mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes2.put(Constraints.GUEST_FOOD,"");
        mapContraintes2.put(Constraints.HOST_FOOD,"");mapContraintes2.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes2.put(Constraints.PAIR_GENDER, "male");mapContraintes2.put(Constraints.HISTORY, "");
        this.mapContraintes2=new HashMap<>();
        mapContraintes2.put(Constraints.GUEST_ANIMAL_ALLERGY,"yes");mapContraintes2.put(Constraints.HOST_HAS_ANIMAL,"no");mapContraintes2.put(Constraints.GUEST_FOOD,"");
        mapContraintes2.put(Constraints.HOST_FOOD,"");mapContraintes2.put(Constraints.HOBBIES, "jeux-vidéo,jardinage");mapContraintes2.put(Constraints.PAIR_GENDER, "male");mapContraintes2.put(Constraints.HISTORY, "");
        
        
        this.s1= new Student("Sergheraert", "Timothée", "male", LocalDate.of(2006,4,18), "France");
        this.s2= new Student("Lemaire", "Mano", "male", LocalDate.of(2006,10,8), "France");
        this.s3= new Student("Roty", "Clément", "male", LocalDate.of(2006,6,2), "France");
        this.s4= new Student("PasD'idée", "Béatrice", "female", LocalDate.of(2001,1,31), "Germany");
        this.s5= new Student("ToujoursRien", "Alice", "female", LocalDate.of(1999,7,24), "Italy");
        this.s6= new Student("Désesssspoire", "Léonardo", "other", LocalDate.of(2006,6,2), "Listenbourg");


    }
    
}
