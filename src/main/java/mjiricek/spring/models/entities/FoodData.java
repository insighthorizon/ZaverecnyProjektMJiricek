package mjiricek.spring.models.entities;

/**
 * Defines the "part" of the data in DBEntity (DBEntity extends EntityDTO) which is fully accessible to the client
 * - that this the "table row" without id
 * - modifiable by the client
 * (user may see the id#, but can't change it, nor choose id when creating new entity)
 */
public class FoodData {

    /**
     * name of the food
     */
    private String foodName;

    /**
     * kiloCalories per 100 grams of the food
     */
    private double kcalContent;
    /**
     * grams of protein per 100 grams of the food
     */
    private double proteinContent;
    /**
     * grams of carbohydrates per 100 grams of the food
     */
    private double carbContent;
    /**
     * grams of fat per 100 grams of the food
     */
    private double fatContent;

    /**
     * default constructor
     * - spring needs parameter-less constructor to instantiate the DTO
     */
    public FoodData() {
        this("", 0, 0, 0, 0);
    }

    /**
     * full constructor
     *
     * @param foodName
     * @param kcalContent
     * @param proteinContent
     * @param carbContent
     * @param fatContent
     */
    public FoodData(String foodName, double kcalContent, double proteinContent, double carbContent, double fatContent) {
        setFoodName(foodName);
        setKcalContent(kcalContent);
        setProteinContent(proteinContent);
        setCarbContent(carbContent);
        setFatContent(fatContent);
    }

    /**
     * copy constructor
     *
     * @param foodData entity being copied
     */
    public FoodData(FoodData foodData) {
        this(foodData.getFoodName(),
                foodData.getKcalContent(),
                foodData.getProteinContent(),
                foodData.getCarbContent(),
                foodData.getFatContent());
    }

    /**
     * copy method
     */
    public FoodData copy() {
        return new FoodData(this);
    }


    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getKcalContent() {
        return kcalContent;
    }

    public void setKcalContent(double kcalContent) {
        this.kcalContent = kcalContent;
    }

    public double getProteinContent() {
        return proteinContent;
    }

    public void setProteinContent(double proteinContent) {
        this.proteinContent = proteinContent;
    }

    public double getCarbContent() {
        return carbContent;
    }

    public void setCarbContent(double carbContent) {
        this.carbContent = carbContent;
    }

    public double getFatContent() {
        return fatContent;
    }

    public void setFatContent(double fatContent) {
        this.fatContent = fatContent;
    }


    /**
     * set's all DTO attributes by passing just one EntityDTO parameter
     *
     * @param foodData entity containing the new parameters
     */
    public void setAllAttributes(FoodData foodData) {
        setFoodName(foodData.getFoodName());
        setKcalContent(foodData.getKcalContent());
        setProteinContent(foodData.getProteinContent());
        setCarbContent(foodData.getCarbContent());
        setFatContent(foodData.getFatContent());
    }

}
