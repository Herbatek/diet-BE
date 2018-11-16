package com.piotrek.diet.user;

import com.piotrek.diet.helpers.BaseEntity;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Role;
import com.piotrek.diet.user.enums.Sex;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
public class User extends BaseEntity {

    @NotNull
    @Indexed(unique = true)
    private long facebookId;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @Email
    @NotNull
    @Indexed(unique = true)
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String pictureUrl;

    private Sex sex;

    private Activity activity;

    private int age;

    private int height;

    private int weight;

    private int caloriesPerDay;

    private int proteinPerDay;

    private int carbohydratePerDay;

    private int fatPerDay;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime lastVisit;

    @NotNull
    private Role role;

    private HashSet<Meal> favouriteMeals = new HashSet<>();

    public User(long facebookId, String email, String firstName, String lastName) {
        this.facebookId = facebookId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = firstName + " " + lastName;
        this.role = Role.ROLE_USER;
        this.createdAt = LocalDateTime.now();
    }

    public User(String id, Long facebookId, String username, String email, String firstName, String lastName,
                String pictureUrl, Sex sex, Activity activity, int age, int height, int weight, int caloriesPerDay,
                LocalDateTime createdAt, LocalDateTime lastVisit, Role role) {
        super(id);
        this.facebookId = facebookId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.sex = sex;
        this.activity = activity;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.caloriesPerDay = caloriesPerDay;
        this.createdAt = createdAt;
        this.lastVisit = lastVisit;
        this.role = role;
    }
}