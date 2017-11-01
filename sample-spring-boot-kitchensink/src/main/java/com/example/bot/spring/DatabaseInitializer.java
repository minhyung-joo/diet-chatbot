package com.example.bot.spring;

import com.example.bot.spring.tables.Food;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.FileReader;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        try {
            String fileName = "FOOD_DATA.txt";
            String line = null;
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

                String[] foodData = line.split("^");
                String foodName = foodData[0];
                String category = foodData[1];
                double calories = Double.parseDouble(foodData[2]);
                double sodium = Double.parseDouble(foodData[3]);
                double fat = Double.parseDouble(foodData[4]);
                double protein = Double.parseDouble(foodData[5]);
                double carbohydrate = Double.parseDouble(foodData[6]);
                Food food = new Food(foodName, category, calories, sodium, fat, protein, carbohydrate);
                session.save(food);
            }
            session.getTransaction().commit();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
