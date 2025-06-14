package basicclass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.HashMap;

public class testAssociationStudent {
    Student s1, s2, s3, s4;
    HashMap<Constraints, String> map1, map2;

    @BeforeEach
    public void setup() {
        map1 = Student.constraintsMapInit();
        map2 = Student.constraintsMapInit();
        map1.put(Constraints.HOBBIES, "musique,cinema");
        map2.put(Constraints.HOBBIES, "cinema,lecture");
        map1.put(Constraints.GUEST_ANIMAL_ALLERGY, "no");
        map2.put(Constraints.HOST_HAS_ANIMAL, "no");
        s1 = new Student("A", "B", "male", LocalDate.of(2000,1,1), Country.FR, map1);
        s2 = new Student("C", "D", "female", LocalDate.of(2000,1,1), Country.IT, map2);
    }

    @Test
    public void testConstructorAndGetters() {
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        assertEquals(s1, assoc.getHost());
        assertEquals(s2, assoc.getGuest());
    }

    @Test
    public void testScoreAssociationAndAffinity() {
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        Integer score = assoc.getScoreAssociation();
        assertNotNull(score);
        String desc = assoc.describeLevelOfAffinity();
        assertNotNull(desc);
    }

    @Test
    public void testFoodCompatibility() {
        map1.put(Constraints.HOST_FOOD, "vegan");
        map2.put(Constraints.GUEST_FOOD, "vegan");
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        assertTrue(assoc.foodCompatibility());
        map2.put(Constraints.GUEST_FOOD, "halal");
        AssociationStudent assoc2 = new AssociationStudent(s1, s2);
        assertFalse(assoc2.foodCompatibility());
    }

    @Test
    public void testScoreHobbie() {
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        assertTrue(assoc.scoreHobbie() >= 0);
    }

    @Test
    public void testInvalidReason() {
        map1.put(Constraints.HOST_FOOD, "vegan");
        map2.put(Constraints.GUEST_FOOD, "halal");
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        assoc.getScoreAssociation();
        assertNotNull(assoc.getInvalidReason());
    }
}