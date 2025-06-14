package algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import basicclass.*;
import manager.HistoryManager;
import java.time.LocalDate;
import java.util.*;

public class MatchingSolverTest {
    @Test
    public void testMatchingPerfect() {
        Student h = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student g = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        Set<Student> hosts = Set.of(h);
        Set<Student> guests = Set.of(g);
        MatchingSolver solver = new MatchingSolver(hosts, guests, new HistoryManager());
        List<AssociationStudent> result = solver.algorithmMatching(MatchingEnum.HONGROIS_MATCHING);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getScoreAssociation());
    }

    @Test
    public void testMatchingWithConstraints() {
        Student h = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student g = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        h.setConstraint(Constraints.HOST_FOOD, "vegan");
        g.setConstraint(Constraints.GUEST_FOOD, "halal");
        Set<Student> hosts = Set.of(h);
        Set<Student> guests = Set.of(g);
        MatchingSolver solver = new MatchingSolver(hosts, guests, new HistoryManager());
        List<AssociationStudent> result = solver.algorithmMatching(MatchingEnum.HONGROIS_MATCHING);
        assertNull(result.get(0).getScoreAssociation());
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetters() {
        Student h = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student g = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        Set<Student> hosts = Set.of(h);
        Set<Student> guests = Set.of(g);
        MatchingSolver solver = new MatchingSolver(hosts, guests, new HistoryManager());
        solver.algorithmMatching(MatchingEnum.HONGROIS_MATCHING);
        assertEquals(hosts, solver.getHostsListe());
        assertEquals(guests, solver.getGuestsListe());
        assertNotNull(solver.getAssociations());
        assertNotNull(solver.getAssociationsInvalid());
    }
}