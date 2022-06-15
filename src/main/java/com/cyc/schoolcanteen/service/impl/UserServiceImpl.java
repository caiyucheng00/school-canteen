package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.entity.User;
import com.cyc.schoolcanteen.mapper.UserMapper;
import com.cyc.schoolcanteen.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-30 11:17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
