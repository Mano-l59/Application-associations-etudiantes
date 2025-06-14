package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import basicclass.*;
import manager.HistoryManager;
import java.time.LocalDate;
import java.util.*;

public class HistoryConstraintCheckerTest {
    
    private Student host;
    private Student guest;
    private HistoryManager hm;

    @BeforeEach
    public void setUp() {
        host = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        guest = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        host.setConstraint(Constraints.HISTORY, "same");
        guest.setConstraint(Constraints.HISTORY, "same");

        AssociationStudent assoc = new AssociationStudent(host, guest);
        List<AssociationStudent> list = new ArrayList<>();
        list.add(assoc);

        hm = new HistoryManager();
        hm.addOrReplaceMatching(host.getCountry(), guest.getCountry(), list);
    }

    @Test
    public void testCheckHistoryConstraint() {
        // Cas déjà appariés
        assertEquals(HistoryConstraintChecker.checkHistoryConstraint(host, guest, hm),HistoryConstraintChecker.result.SAME);

        // Cas "other"
        host.setConstraint(Constraints.HISTORY, "other");
        guest.setConstraint(Constraints.HISTORY, "other");
        assertEquals(HistoryConstraintChecker.checkHistoryConstraint(host, guest, hm),HistoryConstraintChecker.result.OTHER);

    }
}