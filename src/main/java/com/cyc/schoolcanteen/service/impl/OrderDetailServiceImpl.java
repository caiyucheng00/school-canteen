package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.entity.OrderDetail;
import com.cyc.schoolcanteen.mapper.OrderDetailMapper;
import com.cyc.schoolcanteen.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-06-05 20:23
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
