package basicclass;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.HashMap;

public class StudentTest {

    @Test
    public void testConstructorAndGetters() {
        HashMap<Constraints, String> map = Student.constraintsMapInit();
        Student s = new Student("Dupont", "Jean", "male", LocalDate.of(2000,1,1), Country.FR, map);
        assertEquals("Dupont", s.getName());
        assertEquals("Jean", s.getForename());
        assertEquals("male", s.getGender());
        assertEquals(LocalDate.of(2000,1,1), s.getBirthday());
        assertEquals(Country.FR, s.getCountry());
        assertEquals(map, s.getConstraintsMap());
        assertNotNull(s.getId());
    }

    @Test
    public void testGetAge() {
        Student s = new Student("A", "B", "male", LocalDate.now().minusYears(20), Country.FR, Student.constraintsMapInit());
        assertEquals(20, s.getAge());
    }

    @Test
    public void testSetConstraintAndView() {
        Student s = new Student("A", "B", "male", LocalDate.now(), Country.FR, Student.constraintsMapInit());
        s.setConstraint(Constraints.HOBBIES, "musique");
        assertEquals("musique", s.getConstraintsMap().get(Constraints.HOBBIES));
        assertTrue(s.viewConstraintsMap().contains("musique"));
    }

    @Test
    public void testEqualsAndHashCode() {
        Student s1 = new Student("A", "B", "male", LocalDate.now(), Country.FR, Student.constraintsMapInit());
        Student s2 = new Student("A", "B", "male", LocalDate.now(), Country.FR, Student.constraintsMapInit());
        assertNotEquals(s1, s2); // IDs différents
        assertEquals(s1, s1);
        assertEquals(s1.hashCode(), Integer.hashCode(s1.getId()));
    }

    @Test
    public void testToStringAndToStringComplete() {
        Student s = new Student("A", "B", "male", LocalDate.now(), Country.FR, Student.constraintsMapInit());
        assertTrue(s.toString().contains("A"));
        assertTrue(s.toStringComplete().contains("A"));
    }

    @Test
    public void testConstraintsMapInit() {
        HashMap<Constraints, String> map = Student.constraintsMapInit();
        assertTrue(map.containsKey(Constraints.HOBBIES));
        assertTrue(map.containsKey(Constraints.HISTORY));
    }
}