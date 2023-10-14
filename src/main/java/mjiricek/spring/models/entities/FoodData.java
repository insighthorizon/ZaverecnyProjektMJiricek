package mjiricek.spring.models.entities;

/**
 * Defines the "part" of the data in DBEntity (DBEntity extends EntityDTO) which is fully accessible to the client
 * - that this the "table row" without id
 * - modifiable by the client
 * (user may see the id# in some cases, but can't change it, nor choose id when creating new entity)
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
     * @param foodName name of the food
     * @param kcalContent kilocalories per 100 g of the food
     * @param proteinContent grams of protein per 100 g of the food
     * @param carbContent grams of carbohydrates per 100 g of the food
     * @param fatContent grams of fat per 100 g of the food
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
     * @param foodData foodData being copied
     */
    public FoodData(FoodData foodData) {
        this(foodData.getFoodName(),
                foodData.getKcalContent(),
                foodData.getProteinContent(),
                foodData.getCarbContent(),
                foodData.getFatContent());
    }

    /**
     * copy method - returns new copy of the same FoodData
     */
    public FoodData copy() {
        return new FoodData(this);
    }


    /**
     * return name of the food
     * @return name of the food
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * set name of the food
     * @param foodName name of the food
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * return kcal content of the food
     * @return kcal content of the food
     */
    public double getKcalContent() {
        return kcalContent;
    }

    /**
     * set kcal content of the food
     * @param kcalContent kcal content of the food
     */
    public void setKcalContent(double kcalContent) {
        this.kcalContent = kcalContent;
    }

    /**
     * return protein content of the food
     * @return protein content of the food
     */
    public double getProteinContent() {
        return proteinContent;
    }

    /**
     * set protein content of the food
     * @param proteinContent protein content of the food
     */
    public void setProteinContent(double proteinContent) {
        this.proteinContent = proteinContent;
    }

    /**
     * return carbohydrate content of the food
     * @return carbohydrate content of the food
     */
    public double getCarbContent() {
        return carbContent;
    }

    /**
     * set carbohydrate content of the food
     * @param carbContent carbohydrate content of the food
     */
    public void setCarbContent(double carbContent) {
        this.carbContent = carbContent;
    }

    /**
     * return fat content of the food
     * @return fat content of the food
     */
    public double getFatContent() {
        return fatContent;
    }

    /**
     * set fat content of the food
     * @param fatContent fat content of the food
     */
    public void setFatContent(double fatContent) {
        this.fatContent = fatContent;
    }

    /**
     * set's all attributes by passing just one FoodData parameter
     * @param foodData FoodData containing the new parameters
     */
    public void setAllAttributes(FoodData foodData) {
        setFoodName(foodData.getFoodName());
        setKcalContent(foodData.getKcalContent());
        setProteinContent(foodData.getProteinContent());
        setCarbContent(foodData.getCarbContent());
        setFatContent(foodData.getFatContent());
    }

}
