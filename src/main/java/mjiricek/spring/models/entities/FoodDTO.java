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

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getKcalContent() {
        return kcalContent;
    }

    public void setKcalContent(String kcalContent) {
        this.kcalContent = kcalContent;
    }

    public String getProteinContent() {
        return proteinContent;
    }

    public void setProteinContent(String proteinContent) {
        this.proteinContent = proteinContent;
    }

    public String getCarbContent() {
        return carbContent;
    }

    public void setCarbContent(String carbContent) {
        this.carbContent = carbContent;
    }

    public String getFatContent() {
        return fatContent;
    }

    public void setFatContent(String fatContent) {
        this.fatContent = fatContent;
    }

}
