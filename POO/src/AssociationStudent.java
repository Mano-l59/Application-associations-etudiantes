import java.util.Arrays;
import java.util.List;

public class AssociationStudent {
    private final Student HOST;
    private final Student GUEST;
    private Integer score_association;

    public AssociationStudent(Student hote, Student guest) {
        this.HOST = hote;
        this.GUEST = guest;
        this.score_association = 0;
    }
    public void setScore_association(Integer score_association) {
        if(score_association==null){
            this.score_association=null; 
        }else{ 
            this.score_association = this.score_association+score_association;
        }
    }
    public Integer getScore_association() {
        return this.score_association;
    }
    public boolean foodCompatibility() {
        List<String> hoteFood = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOST_FOOD).split(";"));
        List<String> guestFood = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.GUEST_FOOD).split(";"));
       
        return hoteFood.containsAll(guestFood);
    }
    public Integer scoreHobbie(){
        final int COST_OF_HAVING_DIFF_HOBBIE=3;
        int cpt=0;
        int higherSize;
        List<String> hoteHobbies = Arrays.asList(HOST.getConstraintsMap().get(Constraints.HOBBIES).split(";"));
        List<String> guestHobbies = Arrays.asList(GUEST.getConstraintsMap().get(Constraints.HOBBIES).split(";"));
        
        for(String h_hobbie : hoteHobbies){
            for(String g_hobbie : guestHobbies){
                if (h_hobbie.equals(g_hobbie)) cpt++;
            }
        }
        if(hoteHobbies.size()>=guestHobbies.size()) higherSize=hoteHobbies.size();
        else higherSize=hoteHobbies.size();

        return (higherSize-cpt)*COST_OF_HAVING_DIFF_HOBBIE;
    }

    public void score_affinity(AssociationStudent association) {
        if(HOST.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL).equals("yes") && GUEST.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY).equals("yes")){
           this.setScore_association(null);
        }else if (!this.foodCompatibility()){
            this.setScore_association(null);
        }else{
            if(HOST.getAge()==GUEST.getAge()){
                this.setScore_association(-1);
            }else if((HOST.getAge()-GUEST.getAge())>2 &&(HOST.getAge()-GUEST.getAge())<5){
                this.setScore_association(2);
            }else{
                this.setScore_association(5);
            }
            if(!HOST.getGender().equals(GUEST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScore_association(1);
            }
            if(!GUEST.getGender().equals(HOST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScore_association(1);
            }
            this.setScore_association(this.scoreHobbie());
        }
    
    }
}