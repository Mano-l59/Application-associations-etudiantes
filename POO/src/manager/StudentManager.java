package manager;
import basicclass.Constraints;
import basicclass.Country;
import basicclass.Student;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe StudentManager permet la gestion et le chargement personnalisé d'un fichier csv contenant une liste d'étudiant.
 * Elle produit alors une liste d'étudiants qui ont réussi à être chargés et une liste de String des lignes qui n'ont pas pu être chargées.
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 2.0
 */
public class StudentManager {
    private List<Student> students;
    private List<String> failedStudents;

    /**
     * Constructeur de la classe StudentManager.
     * Initialise la liste d'étudiants qui ont réussi à charger et la liste d'étudiants qui ont échoués déclaré comme String .
     */
    public StudentManager() {
        this.students = new ArrayList<>();
        this.failedStudents = new ArrayList<>();
    }

    /**
     * Charge les étudiants à partir d'un fichier CSV et ce, peu importe l'ordre des colonnes.
     * Seul le nom des colonnes doit etre strictement respecté.
     * Les colonnes attendues sont : FORENAME;NAME;COUNTRY;BIRTH_DATE;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;GENDER;PAIR_GENDER;HISTORY
     * @param filePath Le chemin du fichier CSV qui doit etre chargé.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void loadStudentsFromCsv(String filePath) throws IOException {
        students.clear();
        failedStudents.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); 
            if (line == null) {
                throw new IOException("Le fichier CSV est vide.");
            }
            List<String> headers = Arrays.asList(line.split(";"));
            Map<String, Integer> colIndex = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                colIndex.put(headers.get(i).trim().toUpperCase(), i);
            }

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";", -1);
                if (data.length != headers.size()) {
                    System.err.println("Ligne mal formatée (nombre de colonnes incorrect) : " + line);
                    failedStudents.add(line);
                    continue;
                }
                try {
                    String forename = data[colIndex.get("FORENAME")].trim();
                    String name = data[colIndex.get("NAME")].trim();
                    Country country = Country.fromString(data[colIndex.get("COUNTRY")].trim());
                    LocalDate birthDate = LocalDate.parse(data[colIndex.get("BIRTH_DATE")].trim()); // Format YYYY-MM-DD

                    // Vérification GUEST_ANIMAL_ALLERGY et HOST_HAS_ANIMAL
                    String guestAnimalAllergy = data[colIndex.get("GUEST_ANIMAL_ALLERGY")].trim().toLowerCase();
                    String hostHasAnimal = data[colIndex.get("HOST_HAS_ANIMAL")].trim().toLowerCase();
                    if (!(guestAnimalAllergy.equals("yes") || guestAnimalAllergy.equals("no"))) {
                        System.err.println("Valeur invalide pour GUEST_ANIMAL_ALLERGY (doit être 'yes' ou 'no') : " + guestAnimalAllergy + " dans la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }
                    if (!(hostHasAnimal.equals("yes") || hostHasAnimal.equals("no"))) {
                        System.err.println("Valeur invalide pour HOST_HAS_ANIMAL (doit être 'yes' ou 'no') : " + hostHasAnimal + " dans la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }

                    // Vérification GUEST_FOOD et HOST_FOOD (peuvent être vide, nonuts, vegetarian, ou les deux séparés par une virgule)
                    String guestFood = data[colIndex.get("GUEST_FOOD")].trim().toLowerCase();
                    String hostFood = data[colIndex.get("HOST_FOOD")].trim().toLowerCase();
                    if (!guestFood.isEmpty()) {
                        String[] guestFoods = guestFood.split(",");
                        for (String food : guestFoods) {
                            String f = food.trim();
                            if (!f.equals("nonuts") && !f.equals("vegetarian")) {
                                System.err.println("Valeur invalide pour GUEST_FOOD (doit être 'nonuts', 'vegetarian' ou les deux, séparés par une virgule) : " + guestFood + " dans la ligne : " + line);
                                failedStudents.add(line);
                                continue;
                            }
                        }
                    }
                    if (!hostFood.isEmpty()) {
                        String[] hostFoods = hostFood.split(",");
                        for (String food : hostFoods) {
                            String f = food.trim();
                            if (!f.equals("nonuts") && !f.equals("vegetarian")) {
                                System.err.println("Valeur invalide pour HOST_FOOD (doit être 'nonuts', 'vegetarian' ou les deux, séparés par une virgule) : " + hostFood + " dans la ligne : " + line);
                                failedStudents.add(line);
                                continue;
                            }
                        }
                    }

                    // Vérification GENDER et PAIR_GENDER
                    String gender = data[colIndex.get("GENDER")].trim().toLowerCase();
                    String pairGender = data[colIndex.get("PAIR_GENDER")].trim().toLowerCase();
                    if (!(gender.equals("male") || gender.equals("female") || gender.equals("other"))) {
                        System.err.println("Valeur invalide pour GENDER (doit être 'male', 'female' ou 'other') : " + gender + " dans la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }
                    if (!(pairGender.equals("male") || pairGender.equals("female") || pairGender.equals("other") || pairGender.isEmpty())) {
                        System.err.println("Valeur invalide pour PAIR_GENDER (doit être 'male', 'female', 'other' ou vide) : " + pairGender + " dans la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }

                    // Vérification HISTORY
                    String history = data[colIndex.get("HISTORY")].trim().toLowerCase();
                    if (!(history.equals("same") || history.equals("other") || history.isEmpty())) {
                        System.err.println("Valeur invalide pour HISTORY (doit être 'same', 'other' ou vide) : " + history + " dans la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }
                    // Construction de la map des contraintes
                    HashMap<Constraints, String> constraintsMap = new HashMap<>();
                    constraintsMap.put(Constraints.GUEST_ANIMAL_ALLERGY, guestAnimalAllergy);
                    constraintsMap.put(Constraints.HOST_HAS_ANIMAL, hostHasAnimal);
                    constraintsMap.put(Constraints.GUEST_FOOD, guestFood);
                    constraintsMap.put(Constraints.HOST_FOOD, hostFood);
                    constraintsMap.put(Constraints.HOBBIES, data[colIndex.get("HOBBIES")].trim());
                    constraintsMap.put(Constraints.PAIR_GENDER, pairGender);
                    constraintsMap.put(Constraints.HISTORY, history);
                    if (constraintsMap.size() != Constraints.values().length) {
                        System.err.println("Nombre de contraintes incorrect pour la ligne : " + line);
                        failedStudents.add(line);
                        continue;
                    }
                    // Création de l'étudiant et ajout à la liste
                    Student student = new Student(name, forename, gender, birthDate, country, constraintsMap);
                    students.add(student);

                } catch (DateTimeParseException e) {
                    System.err.println("Erreur de format de date pour la ligne : " + line + " - " + e.getMessage());
                    failedStudents.add(line);
                } catch (IllegalArgumentException e) {
                    System.err.println("Donnée de contrainte invalide pour la ligne : " + line + " - " + e.getMessage());
                    failedStudents.add(line);
                } catch (Exception e) {
                    System.err.println("Erreur inattendue lors du traitement de la ligne : " + line + " - " + e.getMessage());
                    failedStudents.add(line);
                }
            }
        }
    }

    
    /**
     * Retourne la liste de tous les étudiants chargés.
     * @return Une liste de tous les étudiants.
     */
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
    /**
     * Retourne la liste de toute les lignes qui n'ont pas su être chargées lors du traitement.
     * @return Une liste de ce qui doit etre normalement des étudiants qui ont été mal formatés dans le fichier CSV.
     */
    public List<String> getFailedStudents() {
        return new ArrayList<>(failedStudents);
    }
}