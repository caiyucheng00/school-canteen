package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.entity.Orders;
import com.cyc.schoolcanteen.mapper.OrdersMapper;
import com.cyc.schoolcanteen.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-06-05 20:22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
