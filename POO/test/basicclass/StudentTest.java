package basicclass;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;

public class StudentTest {

    @Test
    public void testGetAge() {
        Student s = new Student("Test", "Test", "male", LocalDate.now(), Country.FR, Student.constraintsMapInit());
        assertEquals(0, s.getAge());
        Student s2 = new Student("Test", "Test", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        assertEquals(LocalDate.now().getYear() - 2000, s2.getAge());
    }

    @Test
    public void testConstraintsMapInit() {
        HashMap<Constraints, String> map = Student.constraintsMapInit();
        assertEquals(7, map.size());
        assertEquals("B", map.get(Constraints.GUEST_ANIMAL_ALLERGY));
        assertEquals("T", map.get(Constraints.HOBBIES));
    }

    @Test
    public void testEquals() {
        Student s1 = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student s2 = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        assertFalse(s1.equals(s2));
        assertTrue(s1.equals(s1));
    }

    @Test
    public void testSetConstraint() {
        Student s = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        s.setConstraint(Constraints.HOBBIES, "musique");
        assertEquals("musique", s.getConstraintsMap().get(Constraints.HOBBIES));
    }
}
