package basicclass;

import java.time.LocalDate;
import java.util.HashMap;
import java.io.Serializable;

/**
 * La classe représente un étudiant et comprend des méthodes permettant d'obtenir son âge, obtenir son prénom, ses préférences...
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.1 
 */
public class Student implements Serializable {
    private final int ID; // Nouvel ID unique
    private static int IdCpt = 0;
    private String name;
    private String forename;
    private String gender;
    private LocalDate birthday;
    private Country country;
    private HashMap<Constraints, String> constraintsMap;

    /**
     * Constructeur principal de la classe Student
     * @param ID L'ID unique de l'étudiant
     * @param name Le nom de l'étudiant
     * @param forename Le prénom de l'étudiant
     * @param gender Le genre de l'étudiant
     * @param birthday La date de naissance de l'étudiant
     * @param country Le pays d'origine de l'étudiant
     * @param constraintsMap Les contraintes et préférences de l'étudiant
     */
    public Student(String name, String forename, String gender, LocalDate birthday, Country country, HashMap<Constraints, String> constraintsMap) {
        this.ID = IdCpt++;
        this.name = name;
        this.forename = forename;
        this.gender = gender;
        this.birthday = birthday;
        this.country = country;
        this.constraintsMap = constraintsMap;
    }
    /**
     * Constructeur pour créer un étudiant fictif avec un ID négatif et un nom générique.
     * @param nb Un numéro pour différencier les étudiants fictifs.
     * @param country Le pays d'origine de l'étudiant fictif.
     * @since 3.0
     */
    public Student(int nb,Country country) {
        this.country = country;
        this.ID=-999;
        this.name="Unkown "+nb;
        this.forename="Fictive Student";
        this.constraintsMap = constraintsMapInit();
        this.birthday = LocalDate.of(0, 1, 1);
    }

   /**
    * Permet d'obtenir l'ID de l'étudiant.
    * Cet ID est unique pour chaque étudiant et est utilisé pour les identifier dans le système.
    * @return int L'ID de l'étudiant.
    * @since 2.0
    */
    public int getId() {
        return ID;
    }

    /**
     * Permet d'obtenir le nom de l'étudiant.
     * @return String Le nom de l'étudiant.
     */
    public String getName() {
        return name;
    }

    /**
     * Permet d'obtenir le prénom de l'étudiant.
     * @return String Le prénom de l'étudiant.
     */
    public String getForename() {
        return forename;
    }

    /**
     * Permet d'obtenir le genre de l'étudiant.
     * @return String Le genre de l'étudiant.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Permet d'obtenir la date de naissance de l'étudiant.
     * @return LocalDate La date de naissance de l'étudiant.
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * Permet d'obtenir le pays d'origine de l'étudiant.
     * @return Country Le pays d'origine de l'étudiant.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Permet d'obtenir la map des contraintes et préférences de l'étudiant.
     * @return HashMap<Constraints, String> La map des contraintes et préférences de l'étudiant.
     */
    public HashMap<Constraints, String> getConstraintsMap() {
        return constraintsMap;
    }
    /**
     * Méthode statique pour initialiser la map des contraintes avec des valeurs par défaut.
     * Cette méthode crée une nouvelle HashMap et y ajoute les contraintes avec leurs valeurs par défaut.
     * Utilisée pour initialiser les étudiants fictifs ou pour réinitialiser les contraintes d'un étudiant.
     * @return HashMap<Constraints, String> La map des contraintes initialisée avec les valeurs par défaut.
     */
    public static HashMap<Constraints,String> constraintsMapInit(){
        HashMap<Constraints,String> mapInit= new HashMap<>();
        mapInit.putIfAbsent(Constraints.GUEST_ANIMAL_ALLERGY, "B");
        mapInit.putIfAbsent(Constraints.HOST_HAS_ANIMAL, "B");
        mapInit.putIfAbsent(Constraints.GUEST_FOOD, "T");
        mapInit.putIfAbsent(Constraints.HOST_FOOD, "T");
        mapInit.putIfAbsent(Constraints.HOBBIES, "T");
        mapInit.putIfAbsent(Constraints.PAIR_GENDER, "T");
        mapInit.putIfAbsent(Constraints.HISTORY, "T");
        return mapInit;
    }

    /**
     * Permet de mettre à jour une contrainte de l'étudiant.
     * @param contrainte La contrainte à mettre à jour.
     * @param valeur La nouvelle valeur de la contrainte.
     */
    public void setConstraint(Constraints contrainte, String valeur) {
        this.constraintsMap.put(contrainte, valeur);
    }

    @Override
    /**
     * Utilisée pour montrer les informations sur un étudiant
     * @return Retourne une chaîne de caractères de la forme : Nom + Prénom
     * @since 1.0
     */
    public String toString() {
        return this.name + " " + this.forename;
    }

    /**
     * Utilisée pour montrer toutes les informations possibles sur un étudiant
     * @return Retourne une chaîne de caractères plus complète que la fonction toString() de base, en utilisant la forme suivante : le ToString() de base + Sexe + Date de naissance + Pays + Liste des contraintes et préférences
     * @since 1.0
     */
    public String toStringComplete() {
        return "-----\nID : " + this.ID + "\nNom : " + this.name +", Prénom : "+this.forename + "\nSexe : " + this.gender + ", Date de naissance : " + this.birthday + ", Pays : " + this.country.getFullName() + "\n-> Liste des contraintes / préférences : " + this.constraintsMap.toString()+"\n-----";
    }

    /**
     * Montre la liste des contraintes
     * @return Retourne la liste des contraintes et préférences en utilisant toString()
     * @since 1.0
     */
    public String viewConstraintsMap() {
        return "Liste des contraintes / préférences : " + this.constraintsMap.toString();
    }

    /**
     * Permet d'obtenir l'âge d'un étudiant grâce à la date actuelle.
     * @return L'âge de l'étudiant
     * @since 1.0
     */
    public Integer getAge() {
        return LocalDate.now().getYear() - this.birthday.getYear();
    }
    /**
     * Méthode equals pour comparer deux étudiants.
     * Deux étudiants sont considérés égaux s'ils ont le même ID.
     * Pour l'instant cette méthode n'est pas utilisée dans le programme, mais elle peut être utile pour des comparaisons futures.
     * @param o L'objet à comparer avec l'étudiant actuel.
     * @return true si les deux étudiants ont le même ID, false sinon.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return this.ID == student.ID;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(this.getId());
    }
}
