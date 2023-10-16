package mjiricek.spring.models.entities;

/**
 * class represents data transfer object for the food database entity
 * Because I don't know bean validation api hybernate something,
 * I need to take all inputs as strings in order to be able to validate them
 * the standard java way (parse -> handle parsing exception)
 */
public class FoodDTO {
    /**
     * name of the food
     */
    private String foodName;

    /**
     * kiloCalories per 100 grams of the food
     */
    private String kcalContent;

    /**
     * grams of protein per 100 grams of the food
     */
    private String proteinContent;

    /**
     * grams of carbohydrates per 100 grams of the food
     */
    private String carbContent;

    /**
     * grams of fat per 100 grams of the food
     */
    private String fatContent;

    /**
     * constructor
     *
     * @param name name of the food
     * @param kcal kilocalories per 100 grams of the food
     * @param protein grams of protein per 100 grams of the food
     * @param carb grams of carbohydrates per 100 grams of the food
     * @param fat grams of fat per 100 grams of the food
     */
    public FoodDTO(String name, String kcal, String protein, String carb, String fat) {
        setFoodName(name);
        setKcalContent(kcal);
        setProteinContent(protein);
        setCarbContent(carb);
        setFatContent(fat);
    }

    /**
     * default constructor
     */
    public FoodDTO() {
        resetAllAttributes();
    }

    /**
     * set all attributes based on Food entity
     *
     * @param food food/entity
     */
    public void setAllAttributes(Food food) {
        setFoodName(food.getFoodName());
        setKcalContent(String.valueOf(food.getKcalContent()));
        setProteinContent(String.valueOf(food.getProteinContent()));
        setCarbContent(String.valueOf(food.getCarbContent()));
        setFatContent(String.valueOf(food.getFatContent()));
    }

    /**
     * reset all attributes
     */
    public void resetAllAttributes() {
        setFoodName("");
        setKcalContent("0");
        setProteinContent("0");
        setCarbContent("0");
        setFatContent("0");
    }

    /**
     * get name of the food
     *
     * @return name of the food
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * set name of the food
     *
     * @param foodName name of the food
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * get kcal content of the food
     *
     * @return kcal content of the food
     */
    public String getKcalContent() {
        return kcalContent;
    }

    /**
     * set kcal content of the food
     *
     * @param kcalContent kcal content of the food
     */
    public void setKcalContent(String kcalContent) {
        this.kcalContent = kcalContent;
    }

    /**
     * get protein content of the food
     *
     * @return protein content of the food
     */
    public String getProteinContent() {
        return proteinContent;
    }

    /**
     * set protein content of the food
     *
     * @param proteinContent protein content of the food
     */
    public void setProteinContent(String proteinContent) {
        this.proteinContent = proteinContent;
    }

    /**
     * get carbohydrate content of the food
     *
     * @return carbohydrate content of the food
     */
    public String getCarbContent() {
        return carbContent;
    }

    /**
     * set carbohydrate content of the food
     *
     * @param carbContent crabohydrate content of the food
     */
    public void setCarbContent(String carbContent) {
        this.carbContent = carbContent;
    }

    /**
     * get fat content of the food
     *
     * @return fat content of the food
     */
    public String getFatContent() {
        return fatContent;
    }

    /**
     * set fat content of the food
     *
     * @param fatContent fat content of the food
     */
    public void setFatContent(String fatContent) {
        this.fatContent = fatContent;
    }

    /**
     * custom toString method for general debugging purposes
     * - returns text representation of FoodDTO instance
     * @return text representation of FoodDTO instance
     */
    @Override
    public String toString() {
        String units = " grams per 100 grams%n";
        // String.format("%n") is portable, "\n" is not
        return String.format("Printout of FoodDTO data inside " + super.toString() + ":%n" +
                "=======================================================%n" +
                "foodName: " + getFoodName() + "%n" +
                "kcalContent: " + getKcalContent() + " kcal per 100 grams%n" +
                "proteinContent: " + getProteinContent() + units +
                "carbContent: " + getCarbContent() + units +
                "fatContent: " + getFatContent() + units);
    }

}
