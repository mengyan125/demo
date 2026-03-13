package com.feedback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feedback.common.BusinessException;
import com.feedback.dto.LoginRequest;
import com.feedback.dto.LoginResponse;
import com.feedback.entity.User;
import com.feedback.mapper.UserMapper;
import com.feedback.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getClassId(),
                user.getClassName()
        );

        log.info("用户登录成功：{}", user.getUsername());
        return new LoginResponse(token, userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 清空密码
        user.setPassword(null);
        return user;
    }

    /**
     * 获取教师列表
     */
    public List<TeacherDTO> getTeacherList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "teacher")
               .eq(User::getStatus, 1);
        List<User> teachers = userMapper.selectList(wrapper);
        return teachers.stream()
                .map(t -> new TeacherDTO(t.getId(), t.getRealName()))
                .collect(Collectors.toList());
    }

    /**
     * 获取班级列表
     */
    public List<ClassDTO> getClassList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "student")
               .eq(User::getStatus, 1)
               .isNotNull(User::getClassId);
        List<User> students = userMapper.selectList(wrapper);
        return students.stream()
                .filter(s -> s.getClassId() != null)
                .map(s -> new ClassDTO(s.getClassId(), s.getClassName(), 0))
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 教师DTO
     */
    public static class TeacherDTO {
        public Long id;
        public String name;

        public TeacherDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /**
     * 班级DTO
     */
    public static class ClassDTO {
        public Long id;
        public String name;
        public Integer studentCount;

        public ClassDTO(Long id, String name, Integer studentCount) {
            this.id = id;
            this.name = name;
            this.studentCount = studentCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassDTO classDTO = (ClassDTO) o;
            return id != null && id.equals(classDTO.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

}
