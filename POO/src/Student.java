import java.time.LocalDate;
import java.util.Map;

public class Student{
    private String name;
    private String forename;
    private String gender;
    private LocalDate birthday;
    private String country;
    private Map<String, String> constraintsMap;

    public Student(String name,String forename,String gender,LocalDate birthday,String country, Map<String, String> constraintsMap){
        this.name=name;
        this.forename=forename;
        this.gender=gender;
        this.country=country;
        this.constraintsMap=constraintsMap;
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
    public String toString(){
        return ""+this.name+" "+this.forename;
    }
    public Map<String, String> returnConstraintsMap(){
        return this.constraintsMap;
    }
    public String viewConstraintsMap(){
        return "Liste des contraintes / préférences : ";
    }
}
