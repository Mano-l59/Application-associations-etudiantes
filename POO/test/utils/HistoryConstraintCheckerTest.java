package utils;

import basicclass.*;
import manager.HistoryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

public class HistoryConstraintCheckerTest {

    @Test
    public void testCheckHistoryConstraint() {
        Student host = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student guest = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        host.setConstraint(Constraints.HISTORY, "same");
        guest.setConstraint(Constraints.HISTORY, "same");

        AssociationStudent assoc = new AssociationStudent(host, guest);
        List<AssociationStudent> list = new ArrayList<>();
        list.add(assoc);

        HistoryManager hm = new HistoryManager();
        hm.addOrReplaceMatching(host.getCountry(), guest.getCountry(), list);

        assertTrue(HistoryConstraintChecker.checkHistoryConstraint(host, guest, hm));

        // Cas "other"
        host.setConstraint(Constraints.HISTORY, "other");
        guest.setConstraint(Constraints.HISTORY, "other");
        assertFalse(HistoryConstraintChecker.checkHistoryConstraint(host, guest, hm));

        // Cas jamais appariés
        Student host2 = new Student("C", "C", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student guest2 = new Student("D", "D", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        assertFalse(HistoryConstraintChecker.checkHistoryConstraint(host2, guest2, hm));
    }
}