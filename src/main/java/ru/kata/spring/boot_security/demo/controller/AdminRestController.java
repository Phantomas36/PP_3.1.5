package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> showAllUser() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Collection<Role>> getMyRoles(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.findById(id).getRoles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> addNewUser(@RequestBody User user) {
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setLastName(user.getLastName());
        existingUser.setUsername(user.getUsername());

        if (!user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setRoles((Set<Role>) existingUser.getRoles());

        userService.saveUser(existingUser);
        return ResponseEntity.ok(existingUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
