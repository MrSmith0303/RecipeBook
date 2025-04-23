package com.recipe.recipe;

import com.recipe.recipe.users.UsersEntity;
import com.recipe.recipe.users.UsersRepository;

import jakarta.servlet.http.HttpSession;

import com.recipe.recipe.recipebook.RecipeBookEntity;
import com.recipe.recipe.recipebook.RecipeBookRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
        try {
            // Ellenőrizzük, hogy a felhasználónév és jelszó nem üres
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                return new RedirectView("/index.html?error=Username and password cannot be empty!");
            }

            // Felhasználó lekérdezése az adatbázisból
            UsersEntity user = usersRepository.findByUsername(username);

            // Ha a felhasználó nem található
            if (user == null) {
                return new RedirectView("/index.html?error=Invalid username or password!");
            }

            // Jelszó ellenőrzése
            if (!password.equals(user.getPassword())) {
                return new RedirectView("/index.html?error=Invalid username or password!");
            }

            // Bejelentkezés sikeres, session beállítása
            session.setAttribute("loggedInUser", username);
            return new RedirectView("/recipebook.html?success=Login successful!");
        } catch (Exception e) {
            // Általános hiba kezelése, mindig az index.html-re irányít
            return new RedirectView("/index.html?error=An unexpected error occurred!");
        }
    }

    @PostMapping("/register")
    public RedirectView register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phoneNumber,
            @RequestParam String country,
            @RequestParam String postalCode,
            @RequestParam String city,
            @RequestParam String adress, // helyesen: address
            @RequestParam String dateOfBirth) {

        System.out.println("Regisztrációs kísérlet: username=" + username + ", email=" + email);

        if (usersRepository.findByUsername(username) != null || usersRepository.findByEmail(email) != null) {
            System.out.println("Hiba: Felhasználónév vagy email már létezik.");
            return new RedirectView("/register.html?error=User already exists!");
        }

        UsersEntity newUser = new UsersEntity(username, password, email);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setCountry(country);
        newUser.setPostalCode(postalCode);
        newUser.setCity(city);
        newUser.setAddress(adress); // helyesen: address
        newUser.setDateOfBirth(dateOfBirth);

        try {
            usersRepository.save(newUser);
            System.out.println("Regisztráció sikeres: " + username);
        } catch (Exception e) {
            System.out.println("Hiba történt a regisztráció során:");
            e.printStackTrace();
            return new RedirectView("/register.html?error=Failed to register user: " + e.getMessage());
        }

        return new RedirectView("/index.html?success=Registration successful!");
    }

    @PostMapping("/recipes/add")
    public RedirectView addRecipe(@RequestParam String name, @RequestParam String ingredients,
                                  @RequestParam(required = false) String imageUrl,
                                  @RequestParam String description,
                                  @RequestParam String cook, HttpSession session) {
        String creator = (String) session.getAttribute("loggedInUser");
        if (creator == null) {
            return new RedirectView("/index.html?error=You must be logged in to add a recipe!");
        }

        // Alapértelmezett kép URL, ha nincs megadva
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://st4.depositphotos.com/17828278/24401/v/450/depositphotos_244011872-stock-illustration-image-vector-symbol-missing-available.jpg";
        }

        // Új recept létrehozása
        RecipeBookEntity newRecipe = new RecipeBookEntity(name, ingredients, imageUrl, description, cook, creator);
        try {
            recipeBookRepository.save(newRecipe);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("/newrecipe.html?error=Failed to add recipe: " + e.getMessage());
        }

        return new RedirectView("/recipebook.html?success=Recipe added successfully!");
    }

    @GetMapping("/username")
    public String getLoggedInUsername(HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "Guest"; // Ha nincs bejelentkezve, "Guest"-et ad vissza
        }
        return loggedInUser;
    }

    @GetMapping("/recipes")
    public List<RecipeBookEntity> getAllRecipes() {
        return recipeBookRepository.findAll();
    }

    @PostMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate(); // Kilépteti a felhasználót
        return new RedirectView("/index.html?success=You have been logged out!");
    }

    @GetMapping("/myrecipes")
public List<RecipeBookEntity> getMyRecipes(HttpSession session) {
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        throw new IllegalStateException("You must be logged in to view your recipes!");
    }

    // Csak a bejelentkezett felhasználó által létrehozott receptek
    return recipeBookRepository.findByCreator(loggedInUser);
}

@PutMapping("/recipes/{id}")
public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody RecipeBookEntity updatedRecipe, HttpSession session) {
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    RecipeBookEntity recipe = recipeBookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

    if (!recipe.getCreator().equals(loggedInUser)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own recipes.");
    }

    recipe.setName(updatedRecipe.getName());
    recipe.setIngredients(updatedRecipe.getIngredients());
    recipe.setDescription(updatedRecipe.getDescription());
    recipe.setCook(updatedRecipe.getCook());
    recipeBookRepository.save(recipe);

    return ResponseEntity.ok("Recipe updated successfully.");
}

@GetMapping("/auth/recipes/{id}")
public ResponseEntity<RecipeBookEntity> getRecipeById(@PathVariable Long id, HttpSession session) {
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    System.out.println("Logged in user: " + loggedInUser);

    if (loggedInUser == null) {
        System.out.println("User is not logged in.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    RecipeBookEntity recipe = recipeBookRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

    System.out.println("Recipe found: " + recipe.getName());

    if (!recipe.getCreator().equals(loggedInUser)) {
        System.out.println("User is not the creator of the recipe.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(recipe);
}

@DeleteMapping("/recipes/{id}")
public ResponseEntity<?> deleteRecipe(@PathVariable Long id, HttpSession session) {
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    RecipeBookEntity recipe = recipeBookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

    if (!recipe.getCreator().equals(loggedInUser)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own recipes.");
    }

    recipeBookRepository.delete(recipe);
    return ResponseEntity.ok("Recipe deleted successfully.");
}
}
