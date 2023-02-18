package com.song.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.song.demo.Service.EmployeeService;
import com.song.demo.Service.impl.EmployeeServiceImpl;
import com.song.demo.common.R;
import com.song.demo.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
     *员工登录
     * @param request 这里的request对象是为了以后获取一个session对象存储这个用户，到时候可以直接的的调用
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        LambdaQueryWrapper<Employee> lqw  = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp  = employeeService.getOne(lqw);
        Long id = emp.getId();
        String password = DigestUtils.md5Hex(employee.getPassword().getBytes());

        if(emp == null){
            return R.error("登录失败");
        }
        //密码的比对
        if(!emp.getPassword().equals(password)){
            return R.error("密码不正确");
        }

        //查看员工状态
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        HttpSession session = request.getSession();
        session.setAttribute("employee",id);
//        log.info("================{}",request.getSession().getAttribute("employee").toString());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工：{}",employee.toString());
        employee.setPassword(DigestUtils.md5Hex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        HttpSession session = request.getSession();
        Object id = session.getAttribute("employee");
        employee.setCreateUser((Long) id);
        employee.setUpdateUser((Long) id);
        employeeService.save(employee);
        return  R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page<Employee>> getPage( Integer page, Integer pageSize,String name){
        LambdaQueryWrapper<Employee> lqw  = new LambdaQueryWrapper<>();
        lqw.like(name!=null,Employee::getName,name);
        Page<Employee> employeePage = new Page<>(page, pageSize);
        Page<Employee> page1 = employeeService.page(employeePage, lqw);
        return R.success(page1);
    }

    @PutMapping
    public R<String> updateEmp(HttpServletRequest request,@RequestBody Employee employee){
        HttpSession session = request.getSession();
        Long empId = (Long)session.getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工更新成功");
    }

}
