package basicclass;

/*
 * La classe représente un étudiant et comprend des méthodes permettant d'obtenir son âge, obtenir 
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */

import java.time.LocalDate;
import java.util.HashMap;

public class Student{
    private String name;
    private String forename;
    private String gender;
    private LocalDate birthday;
    private String country;
    private HashMap<Constraints, String> constraintsMap;


    /*
     * Constructeur utilisé pour créer une instance d'un étudiant via toutes ses informations y compris ses préférences
     * @since 1.0
     */
    public Student(String name,String forename,String gender,LocalDate birthday,String country, HashMap<Constraints, String> constraintsMap){
        this.name=name;
        this.forename=forename;
        this.gender=gender;
        this.country=country;
        this.constraintsMap=constraintsMap;
    }

    /*
     * Constructeur qui fait appel à l'autre constructeur mais en initialisant à vide les préférences mais sans utiliser null pour limiter les erreurs
     * @since 1.0
     */
    public Student(String name,String forename,String gender,LocalDate birthday,String country){
        this(name,forename,gender,birthday,country,Student.constraintsMapInit());
        
    }
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
    /*
     * @return Retourne le nom d'un étudiant
     * @since 1.0
     */
    public String getName(){
        return this.name;
    }
    /*
     * @return Retourne le prénom d'un étudiant
     * @since 1.0
     */
    public String getForename(){
        return this.forename;
    }
    /*
     * @return Retourne le genre d'un étudiant
     * @since 1.0
     */
    public String getGender(){
        return this.gender;
    }
    /*
     * @return Retourne la date d'anniversaire d'un étudiant
     * @since 1.0
     */
    public LocalDate getBirthday(){
        return this.birthday;
    }
    /*
     * @return Retourne le pays d'un étudiant
     * @since 1.0
     */
    public String getCountry(){
        return this.country;
    }

    /*
     * @return Retourne le dictionnaire des préférences d'un étudiant
     * @since 1.0
     */
    public HashMap<Constraints, String> getConstraintsMap(){
        return this.constraintsMap;
    }
    @Override
    /*
     * Utilisée pour montrer les informations sur un étudiant
     * @return Retourne une chaîne de caractères de la forme : Nom + Prénom
     * @since 1.0
     */
    public String toString(){
        return "Nom : "+this.name+" Prénom : "+this.forename;
    }
    
    /*
     * Utilisée pour montrer toutes les informations possibles sur  un étudiant
     * @return Retourne une chaîne de caractères plus complète que la fonction toString() de base, en utilisant la forme suivante : le ToString() de base + Sexe + Date de naissance + Pays + Liste des contraintes et préférences
     * @since 1.0
     */
    public String toStringComplete(){
        return "[ "+this.toString()+" Sexe : "+this.gender+" Date de naissance : "+this.birthday+" Pays : "+this.country+" ]\n-> Liste des contraintes / préférences : "+this.constraintsMap.toString();
    }
    
    /*
     * Montre la liste des contraintes 
     * @return Retourne la liste des contraintes et préférences en utilisant toString()
     * @since 1.0
     */
    public String viewConstraintsMap(){
        return "Liste des contraintes / préférences : "+this.constraintsMap.toString();

    }

    /*
     * Permet d'obtenir l'âge d'un étudiant grâce à la date actuelle.
     * @return L'âge de l'étudiant
     * @since 1.0
     */
    public int getAge(){
        int age = LocalDate.now().getYear() - this.birthday.getYear();
        if(LocalDate.now().isBefore(this.birthday.withYear(LocalDate.now().getYear()))){
            age = age - 1;
        }
        return age;
    }

}
