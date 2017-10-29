@Entity
public class Food{
	@Id
	long foodID;
	String name;
	String category;
	int calories;
	int sodium;
	int saturatedFat;
	int protein;
	int carbohydrate;

	public Food(long id, String name, String cat, int cal, int sod, int fat, int protein, int carb) {
		foodID = id;
		this.name = name;
		this.category = cat;
		this.calories = cal;
		this.sodium = sod;
		this.saturatedFat = fat;
		this.protein = protein;
		this.carbohydrate = carb;
	}

	public long foodID() {
		return foodID;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public int getCalories() {
		return calories;
	}

	public int getSodium() {
		return sodium;
	}

	public int getSaturatedFat() {
		return saturatedFat;
	}

	public int getProtein() {
		return protein;
	}

	public int getCarbohydrate() {
		return carbohydrate;
	}

}