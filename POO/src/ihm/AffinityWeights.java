package ihm;

public class AffinityWeights {

    private static AffinityWeights instance = new AffinityWeights();

    private Integer HISTORY_OTHER_DETECTED = null;
    private Integer ANIMAL_ALLERGY = null;
    private Integer REGIME_RESTRICTION = null;
    private Integer FRANCE_RULE =null;
    private Integer BONUS_HISTORY = -2;
    private Integer SAME_AGE=-1;
    private Integer AGE_BETWEEN_2_AND_5 = 2;
    private Integer AGE_SUPERIOR_5 = 5;
    private Integer DIFFERENT_GENDER = 1;
    private Integer COST_OF_HAVING_DIFF_HOBBIE = 3;
    private Integer COST_OF_HAVING_LESS_HOBBIE = 1;

    public static AffinityWeights getInstance() { return instance; }

    // Getters/setters
    public Integer getSameAge() { return SAME_AGE; }
    public void setSameAge(Integer v) { SAME_AGE = v; }
    public Integer getAgeBetween2And5() { return AGE_BETWEEN_2_AND_5; }
    public void setAgeBetween2And5(Integer v) { AGE_BETWEEN_2_AND_5 = v; }
    public Integer getAgeSuperior5() { return AGE_SUPERIOR_5; }
    public void setAgeSuperior5(Integer v) { AGE_SUPERIOR_5 = v; }
    public Integer getDifferentGender() { return DIFFERENT_GENDER; }
    public void setDifferentGender(Integer v) { DIFFERENT_GENDER = v; }
    public Integer getCostOfHavingDiffHobbie() { return COST_OF_HAVING_DIFF_HOBBIE; }
    public void setCostOfHavingDiffHobbie(Integer v) { COST_OF_HAVING_DIFF_HOBBIE = v; }
    public Integer getCostOfHavingLessHobbie() { return COST_OF_HAVING_LESS_HOBBIE; }
    public void setCostOfHavingLessHobbie(Integer v) { COST_OF_HAVING_LESS_HOBBIE = v; }
    public Integer getHistoryOtherDetected() { return HISTORY_OTHER_DETECTED; }
    public void setHistoryOtherDetected(Integer v) { HISTORY_OTHER_DETECTED = v; }
    public Integer getAnimalAllergy() { return ANIMAL_ALLERGY; }
    public void setAnimalAllergy(Integer v) { ANIMAL_ALLERGY = v; }
    public Integer getRegimeRestriction() { return REGIME_RESTRICTION; }
    public void setRegimeRestriction(Integer v) { REGIME_RESTRICTION = v; }
    public Integer getFranceRule() { return FRANCE_RULE; }
    public void setFranceRule(Integer v) { FRANCE_RULE = v;}
    public Integer getBonusHistory() { return BONUS_HISTORY; }
    public void setBonusHistory(Integer v) { BONUS_HISTORY = v; }

    public void reset() {
        HISTORY_OTHER_DETECTED = null;
        ANIMAL_ALLERGY = null;
        REGIME_RESTRICTION = null;
        FRANCE_RULE = null;
        SAME_AGE = -1;
        AGE_BETWEEN_2_AND_5 = 2;
        AGE_SUPERIOR_5 = 5;
        DIFFERENT_GENDER = 1;
        COST_OF_HAVING_DIFF_HOBBIE = 3;
        COST_OF_HAVING_LESS_HOBBIE = 1;
        BONUS_HISTORY = -2;
    }
    
    
}
