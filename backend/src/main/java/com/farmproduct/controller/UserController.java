package com.farmproduct.controller;

import com.farmproduct.entity.User;
import com.farmproduct.mapper.UserMapper;
import com.farmproduct.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User registeredUser = userService.register(user);
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("data", registeredUser);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User loginUser = userService.login(user);
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loginUser);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
            } else {
                user.setPassword(passwordEncoder.encode(password));
                userMapper.updateById(user);
                result.put("success", true);
                result.put("message", "密码重置成功");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/list")
    public List<User> list() {
        return userMapper.selectList(null);
    }
    
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User updatedUser = userService.update(user);
            result.put("success", true);
            result.put("message", "更新成功");
            result.put("data", updatedUser);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userMapper.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
