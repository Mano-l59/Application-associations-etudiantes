import java.util.ArrayList;

public class AssociationStudent {
    private final Student HOTE;
    private final Student GUEST;
    private Integer score_association;

    public AssociationStudent(Student hote, Student guest) {
        this.HOTE = hote;
        this.GUEST = guest;
        this.score_association = 0;
    }
    public void setScore_association(Integer score_association) {
        this.score_association = score_association;
    }
    public Integer getScore_association() {
        return this.score_association;
    }
    public boolean foodCompatibility() {
        int k = 0;
        ArrayList<String> hoteFood = new ArrayList<>();
        ArrayList<String> guestFood = new ArrayList<>();
        String hoteFoodString = HOTE.getConstraintsMap().get(Constraints.HOST_FOOD);
        String guestFoodString = GUEST.getConstraintsMap().get(Constraints.GUEST_FOOD);
        for(int i =0;i<hoteFoodString.length();i++){
            if(hoteFoodString.charAt(i)== ','){
                hoteFood.add(hoteFoodString.substring(k,i));
                k=i+1;
            }
        }
        for(int i =0;i<guestFoodString.length();i++){
            if(guestFoodString.charAt(i)== ','){
                guestFood.add(guestFoodString.substring(k,i));
                k=i+1;
            }
        }
        return hoteFood.containsAll(guestFood);
        

    }

    public void score_affinity(AssociationStudent association) {
        if(HOTE.getConstraintsMap().get(Constraints.HOST_HAS_ANIMAL).equals("yes") && GUEST.getConstraintsMap().get(Constraints.GUEST_ANIMAL_ALLERGY).equals("yes")){
           this.setScore_association(null);
        }else if (!this.foodCompatibility()){
            this.setScore_association(null);
        }
        //à finir

    
    }
}