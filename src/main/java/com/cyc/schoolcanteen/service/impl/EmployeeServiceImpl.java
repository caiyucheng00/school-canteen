package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.entity.Employee;
import com.cyc.schoolcanteen.mapper.EmployeeMapper;
import com.cyc.schoolcanteen.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-12 17:36
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
