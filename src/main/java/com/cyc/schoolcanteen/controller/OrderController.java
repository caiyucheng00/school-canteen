package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.schoolcanteen.common.BaseContext;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.AddressBook;
import com.cyc.schoolcanteen.entity.OrderDetail;
import com.cyc.schoolcanteen.entity.Orders;
import com.cyc.schoolcanteen.entity.ShoppingCart;
import com.cyc.schoolcanteen.service.AddressBookService;
import com.cyc.schoolcanteen.service.OrderDetailService;
import com.cyc.schoolcanteen.service.OrdersService;
import com.cyc.schoolcanteen.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-06-06 20:10
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService detailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        //1. 获取用户ID
        Long userId = BaseContext.getId();
        //2. 查询用户购物车信息
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, userId));
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //3. 插入Orders表
        long number = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(new SFunction<ShoppingCart, OrderDetail>() {
            @Override
            public OrderDetail apply(ShoppingCart shoppingCart) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setName(shoppingCart.getName());
                orderDetail.setImage(shoppingCart.getImage());
                orderDetail.setOrderId(number);
                orderDetail.setDishId(shoppingCart.getDishId());
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
                orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
                orderDetail.setNumber(shoppingCart.getNumber());
                orderDetail.setAmount(shoppingCart.getAmount());

                // 总价格
                amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());

                return orderDetail;
            }
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(number));
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        ordersService.save(orders);
        //4. 插入detail表
        detailService.saveBatch(orderDetailList);
        //5. 清空购物车
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,userId));

        return Result.success("");
    }


    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page<Orders>> page(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);
    }

}
