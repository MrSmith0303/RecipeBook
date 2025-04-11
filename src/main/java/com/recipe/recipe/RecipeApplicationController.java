package com.recipe.recipe;

import com.recipe.recipe.users.UsersEntity;
import com.recipe.recipe.users.UsersRepository;

import jakarta.servlet.http.HttpSession;

import com.recipe.recipe.recipebook.RecipeBookEntity;
import com.recipe.recipe.recipebook.RecipeBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/auth")
public class RecipeApplicationController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RecipeBookRepository recipeBookRepository;

    @PostMapping("/login")
    public RedirectView login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        UsersEntity user = usersRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", username);
            return new RedirectView("/recipebook.html");
        } else {
            return new RedirectView("/index.html?error=Invalid username or password!");
        }
    }

    @PostMapping("/register")
    public RedirectView register(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        // Ellenőrizzük, hogy a felhasználónév vagy email már létezik-e
        if (usersRepository.findByUsername(username) != null || usersRepository.findByEmail(email) != null) {
            return new RedirectView("/register.html?error=User already exists!");
        }

        // Új felhasználó mentése az adatbázisba
        UsersEntity newUser = new UsersEntity(username, password, email);
        usersRepository.save(newUser);

        // Sikeres regisztráció után átirányítás a login oldalra
        return new RedirectView("/index.html?success=Registration successful!");
    }

    @PostMapping("/recipes/add")
public RedirectView addRecipe(@RequestParam String name, @RequestParam String ingredients,
                               @RequestParam String imageUrl, @RequestParam String description,
                               @RequestParam String cook, HttpSession session) {
    // Bejelentkezett felhasználó felhasználónevének lekérése a munkamenetből
    String creator = (String) session.getAttribute("loggedInUser");
    if (creator == null) {
        // Ha nincs bejelentkezett felhasználó, irányítsd vissza a login oldalra
        return new RedirectView("/index.html?error=You must be logged in to add a recipe!");
    }

    // Új recept létrehozása és mentése
    RecipeBookEntity newRecipe = new RecipeBookEntity(name, ingredients, imageUrl, description, cook, creator);
    recipeBookRepository.save(newRecipe);

    // Sikeres mentés után átirányítás
    return new RedirectView("/recipes.html?success=Recipe added successfully!");
}

    @GetMapping("/username")
    public String getLoggedInUsername(HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "Guest"; // Ha nincs bejelentkezve, "Guest"-et ad vissza
        }
        return loggedInUser;
}
}