package com.farmproduct.service;

import com.farmproduct.entity.User;
import com.farmproduct.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    public User login(User user) {
        System.out.println("========== 登录调试 ==========");
        System.out.println("请求登录的用户名: " + user.getUsername());
        System.out.println("请求登录的密码: " + user.getPassword());

        User foundUser = userMapper.findByUsername(user.getUsername());

        if (foundUser == null) {
            System.out.println("用户不存在!");
            throw new RuntimeException("用户不存在");
        }

        System.out.println("数据库中的用户名: " + foundUser.getUsername());
        System.out.println("数据库中的密码: " + foundUser.getPassword());

        boolean passwordMatch = false;
        
        // 尝试两种方式验证密码：明文比较 和 BCrypt加密验证
        // 明文比较（用于兼容旧数据或未加密的密码）
        if (user.getPassword().equals(foundUser.getPassword())) {
            passwordMatch = true;
            System.out.println("密码匹配: 明文比较成功");
        }
        // BCrypt加密验证（用于新注册用户）
        else if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            passwordMatch = true;
            System.out.println("密码匹配: BCrypt验证成功");
        }

        if (!passwordMatch) {
            System.out.println("密码错误!");
            throw new RuntimeException("密码错误");
        }
        System.out.println("登录成功!");
        System.out.println("================================");
        foundUser.setPassword(null);
        return foundUser;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            existing.setUsername(user.getUsername());
        }
        if (user.getName() != null && !user.getName().isEmpty()) {
            existing.setName(user.getName());
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            existing.setPhone(user.getPhone());
        }
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            existing.setAddress(user.getAddress());
        }
        userMapper.updateById(existing);
        existing.setPassword(null);
        return existing;
    }
}