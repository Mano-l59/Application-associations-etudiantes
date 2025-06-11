package utils;

import basicclass.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CSVExportTest {

    @Test
    public void testExportMatchingToCsv() throws IOException {
        Student s1 = new Student("A", "A", "male", LocalDate.of(2000,1,1), Country.FR, Student.constraintsMapInit());
        Student s2 = new Student("B", "B", "female", LocalDate.of(2000,1,1), Country.IT, Student.constraintsMapInit());
        AssociationStudent assoc = new AssociationStudent(s1, s2);
        List<AssociationStudent> list = Arrays.asList(assoc);

        String filePath = "test_matching";
        CSVExport.exportMatchingToCsv(list, filePath);

        File file = new File(filePath + ".csv");
        assertTrue(file.exists());

        BufferedReader br = new BufferedReader(new FileReader(file));
        String header = br.readLine();
        assertEquals("NOM1;PRENOM1;SCORE;NOM2;PRENOM2;DESCRIPTION", header);
        String line = br.readLine();
        assertNotNull(line);
        br.close();
        file.delete();
    }
}