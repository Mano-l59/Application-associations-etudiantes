package basicclass;
import java.util.Arrays;
import java.util.List;

public class AssociationStudent {
    private final Student HOST;
    private final Student GUEST;
    private Integer scoreAssociation;

    public AssociationStudent(Student hote, Student guest) {
        this.HOST = hote;
        this.GUEST = guest;
        this.scoreAssociation = 0;
    }
    public void setScoreAffinity(Integer scoreAssociation) {
        if(scoreAssociation==null){
            this.scoreAssociation=null; 
        }else{ 
            this.scoreAssociation = this.scoreAssociation+scoreAssociation;
        }
    }
    public Integer getScore_association() {
        return this.scoreAssociation;
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

    public void scoreAffinity() {
        if(HOST.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL).equals("yes") && GUEST.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY).equals("yes")){
           this.setScoreAffinity(null);
        }else if (!this.foodCompatibility()){
            this.setScoreAffinity(null);
        }else{
            if(HOST.getAge()==GUEST.getAge()){
                this.setScoreAffinity(-1);
            }else if((HOST.getAge()-GUEST.getAge())>2 &&(HOST.getAge()-GUEST.getAge())<5){
                this.setScoreAffinity(2);
            }else{
                this.setScoreAffinity(5);
            }
            if(!HOST.getGender().equals(GUEST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(1);
            }
            if(!GUEST.getGender().equals(HOST.getConstraintsMap().get(Constraints.PAIR_GENDER))){
                this.setScoreAffinity(1);
            }
            this.setScoreAffinity(this.scoreHobbie());
        }
    }
}