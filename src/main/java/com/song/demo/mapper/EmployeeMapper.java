package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
