package basicClass;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * La classe permet de gérer l'import et l'export des fichiers CSV
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
public class CSVManager {
    
    /**
     * Importe les étudiants depuis un fichier CSV
     * @param filePath Le chemin vers le fichier CSV
     * @return Liste des étudiants importés
     */
    public static List<Student> importStudentsFromCSV(String filePath) {
        List<Student> students = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Lire la première ligne (en-têtes)
            if (line == null) return students;
            
            String[] headers = line.split(";");
            
            // Lire chaque ligne de données
            while ((line = br.readLine()) != null) {
                try {
                    Student student = parseStudentFromCSVLine(line, headers);
                    if (student != null) {
                        students.add(student);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la lecture de la ligne : " + line);
                    System.err.println("Erreur : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        
        return students;
    }

    private static Student parseStudentFromCSVLine(String line, String[] headers) {
        String[] values = line.split(";");
        
        if (values.length < headers.length) {
            // Compléter avec des valeurs vides si nécessaire
            String[] newValues = new String[headers.length];
            System.arraycopy(values, 0, newValues, 0, values.length);
            for (int i = values.length; i < headers.length; i++) {
                newValues[i] = "";
            }
            values = newValues;
        }
        
        String forename = "";
        String name = "";
        String country = "";
        LocalDate birthDate = LocalDate.of(2007, 1, 1); // Valeur par défaut
        String gender = "other";
        HashMap<Constraints, String> constraintsMap = Student.constraintsMapInit();
        
        // Parser chaque colonne selon son en-tête
        for (int i = 0; i < headers.length && i < values.length; i++) {
            String header = headers[i].trim();
            String value = values[i].trim();
            
            if (header.toUpperCase().equals("FORENAME")) {
                forename = value;
            } else if (header.toUpperCase().equals("NAME")) {
                name = value;
            } else if (header.toUpperCase().equals("COUNTRY")) {
                country = value;
            } else if (header.toUpperCase().equals("BIRTH_DATE")) {
                if (!value.isEmpty()) {
                    try {
                        birthDate = LocalDate.parse(value);
                    } catch (Exception e) {
                        // Garder la valeur par défaut
                    }
                }
            } else if (header.toUpperCase().equals("GENDER")) {
                if (!value.isEmpty()) {
                    gender = value.toLowerCase();
                }
            } else if (header.toUpperCase().equals("GUEST_ANIMAL_ALLERGY")) {
                constraintsMap.put(Constraints.GUEST_ANIMAL_ALLERGY, value.toLowerCase());
            } else if (header.toUpperCase().equals("HOST_HAS_ANIMAL")) {
                constraintsMap.put(Constraints.HOST_HAS_ANIMAL, value.toLowerCase());
            } else if (header.toUpperCase().equals("GUEST_FOOD_CONSTRAINT") || header.toUpperCase().equals("GUEST_FOOD")) {
                constraintsMap.put(Constraints.GUEST_FOOD, value);
            } else if (header.toUpperCase().equals("HOST_FOOD")) {
                constraintsMap.put(Constraints.HOST_FOOD, value);
            } else if (header.toUpperCase().equals("HOBBIES")) {
                constraintsMap.put(Constraints.HOBBIES, value);
            } else if (header.toUpperCase().equals("PAIR_GENDER")) {
                constraintsMap.put(Constraints.PAIR_GENDER, value.toLowerCase());
            } else if (header.toUpperCase().equals("HISTORY")) {
                constraintsMap.put(Constraints.HISTORY, value.toLowerCase());
            }
        }
        
        return new Student(name, forename, gender, birthDate, country, constraintsMap);
    }
    
    /**
     * Exporte les appariements vers un fichier CSV
     * @param associations Liste des associations à exporter
     * @param filePath Chemin du fichier de sortie
     */
    public static void exportAssociationsToCSV(List<AssociationStudent> associations, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-tête
            writer.println("HOST_NAME;HOST_FORENAME;HOST_COUNTRY;GUEST_NAME;GUEST_FORENAME;GUEST_COUNTRY;SCORE;AFFINITY_LEVEL");
            
            // Données
            for (AssociationStudent assoc : associations) {
                if (assoc.getScoreAssociation() != null) {
                    writer.printf("%s;%s;%s;%s;%s;%s;%d;%s%n",
                        assoc.getHost().getName(),
                        assoc.getHost().getForename(),
                        assoc.getHost().getCountry(),
                        assoc.getGuest().getName(),
                        assoc.getGuest().getForename(),
                        assoc.getGuest().getCountry(),
                        assoc.getScoreAssociation(),
                        assoc.describeLevelOfAffinity()
                    );
                }
            }
            
            System.out.println("Export réussi vers : " + filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export : " + e.getMessage());
        }
    }
    
    /**
     * Exporte les étudiants vers un fichier CSV
     * @param students Liste des étudiants à exporter
     * @param filePath Chemin du fichier de sortie
     */
    public static void exportStudentsToCSV(List<Student> students, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-tête
            writer.println("FORENAME;NAME;COUNTRY;BIRTH_DATE;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;GENDER;PAIR_GENDER;HISTORY");
            
            // Données
            for (Student student : students) {
                writer.printf("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
                    student.getForename(),
                    student.getName(),
                    student.getCountry(),
                    student.getBirthday().toString(),
                    student.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY),
                    student.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL),
                    student.getConstraintsMap().get(Constraints.GUEST_FOOD),
                    student.getConstraintsMap().get(Constraints.HOST_FOOD),
                    student.getConstraintsMap().get(Constraints.HOBBIES),
                    student.getGender(),
                    student.getConstraintsMap().get(Constraints.PAIR_GENDER),
                    student.getConstraintsMap().get(Constraints.HISTORY)
                );
            }
            
            System.out.println("Export des étudiants réussi vers : " + filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export des étudiants : " + e.getMessage());
        }
    }
}