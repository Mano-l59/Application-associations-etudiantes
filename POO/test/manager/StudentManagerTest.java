package manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class StudentManagerTest {

    @Test
    public void testLoadStudentsFromCsv() throws IOException {
        String csv = "FORENAME;NAME;COUNTRY;BIRTH_DATE;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;GENDER;PAIR_GENDER;HISTORY\n"
                + "Timothée;Sergheraert;FR;2006-04-18;yes;no;;;jeux-vidéo,jardinage;male;male;\n"
                + "Mano;Lemaire;FR;2006-10-08;no;yes;vegan,halal;vegan,vegetarian;musique,lecture,kayak;male;female;\n"
                + "Clément;Roty;FR;2006-06-02;yes;yes;sans-gluten;sans-gluten,vegan;sport,cinéma;male;male;\n"
                + "Béatrice;PasD'idée;GE;2001-01-31;no;no;;vegan;cuisine,dessin;female;female;\n"
                + "Alice;ToujoursRien;IT;1999-07-24;yes;no;vegetarian;vegetarian;jeux-vidéo,musique;female;other;\n"
                + "Léonardo;Désesssspoire;ES;2006-06-02;no;yes;kosher;kosher,vegan;lecture,musique;other;female;\n";
        File file = new File("test_students.csv");
        PrintWriter pw = new PrintWriter(file);
        pw.print(csv);
        pw.close();

        StudentManager sm = new StudentManager();
        sm.loadStudentsFromCsv(file.getAbsolutePath());
        assertEquals(6, sm.getStudents().size());
        ;

        String csv2 = "FORENAME;NAME;COUNTRY;BIRTH_DATE;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;GENDER;PAIR_GENDER;HISTORY\n"
                + "Timothée;Sergheraert;FR;2006-04-18;yes;no;;;jeux-vidéo,jardinage;male;male;\n"
                + "BadLine;MissingField;FR;2006-04-18;yes;no;;;jeux-vidéo,jardinage;male;\n";
        pw = new PrintWriter(file);
        pw.print(csv2);
        pw.close();

        sm.loadStudentsFromCsv(file.getAbsolutePath());
        assertEquals(1, sm.getStudents().size());

        file.delete();
    }
}