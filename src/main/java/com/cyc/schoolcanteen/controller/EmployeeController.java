package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.Employee;
import com.cyc.schoolcanteen.mapper.EmployeeMapper;
import com.cyc.schoolcanteen.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-13 11:35
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    /***
     * 登陆 功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1. MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        //2. 搜索用户名
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername())
                .eq(Employee::getPassword, password);
        Employee emp = employeeMapper.selectOne(queryWrapper);
        if (emp == null) {
            return Result.error("用户名不存在");
        }
        //3. 检查密码
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        //4. 是否禁用
        if (emp.getStatus().equals(0)) {
            return Result.error("账号已禁用");
        }
        //5. session保存用户id
        request.getSession().setAttribute("userId", emp.getId());
        return Result.success(emp);
    }


    /**
     * 注销 功能
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Employee> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("userId");

        return Result.success(null);
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> addEmployee(@RequestBody Employee employee){
        // 补全默认信息
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(1L);
//        employee.setUpdateUser(1L);

        employeeService.save(employee);

        return Result.success("添加员工成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Employee>> page(int page, int pageSize, String name){
        //1. 分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //2. 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Employee::getName,name)
                .orderByDesc(Employee::getUsername);

        employeeService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    /**
     * 更新员工信息 --禁用/启用账号
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee){
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId,employee.getId());

//        employee.setUpdateUser(1L);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.update(employee,queryWrapper);
        return Result.success("更新成功");
    }

    /**
     * 回显前端页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> show(@PathVariable Long id){
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId,id);

        Employee employee = employeeService.getOne(queryWrapper);
        return Result.success(employee);
    }
}
