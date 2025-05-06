import java.time.LocalDate;
import java.util.HashMap;

public class Student{
    private String name;
    private String forename;
    private String gender;
    private LocalDate birthday;
    private String country;
    private HashMap<Constraints, String> constraintsMap;

    public Student(String name,String forename,String gender,LocalDate birthday,String country, HashMap<Constraints, String> constraintsMap){
        this.name=name;
        this.forename=forename;
        this.gender=gender;
        this.country=country;
        this.constraintsMap=constraintsMap;
    }
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

    public String getName(){
        return this.name;
    }
    public String getForename(){
        return this.forename;
    }
    public String getGender(){
        return this.gender;
    }
    public LocalDate getBirthday(){
        return this.birthday;
    }
    public String getCountry(){
        return this.country;
    }
    public HashMap<Constraints, String> getConstraintsMap(){
        return this.constraintsMap;
    }
    @Override
    public String toString(){
        return "Nom : "+this.name+" Prénom : "+this.forename;
    }
    public String toStringComplete(){
        return "[ "+this.toString()+" Sexe : "+this.gender+" Date de naissance : "+this.birthday+" Pays : "+this.country+" ]\n-> Liste des contraintes / préférences : "+this.constraintsMap.toString();
    }
    public String viewConstraintsMap(){
        return "Liste des contraintes / préférences : "+this.constraintsMap.toString();
    }
    public int getAge(){
        int age = LocalDate.now().getYear() - this.birthday.getYear();
        if(LocalDate.now().isBefore(this.birthday.withYear(LocalDate.now().getYear()))){
            age = age - 1;
        }
        return age;
    }

}
