package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cyc.schoolcanteen.common.BaseContext;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.ShoppingCart;
import com.cyc.schoolcanteen.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-31 17:21
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return Result.success(shoppingCartList);
    }


    /**
     * 新建
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        // 判断数量
        String name = shoppingCart.getName();
        queryWrapper.eq(ShoppingCart::getName, name);
        long count = shoppingCartService.count(queryWrapper);

        if (count > 0) {
            int num = (int) (count + 1);
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ShoppingCart::getNumber, num)
                    .eq(ShoppingCart::getName,name);
            shoppingCartService.update(updateWrapper);
        } else {
            shoppingCartService.save(shoppingCart);
        }

        return Result.success(shoppingCart);
    }


    /**
     * 减
     * @param map
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody Map<String,Long> map){
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCreateTime(LocalDateTime.now());
        Long id = 0L;
        if(map.get("dishId") != null){
            id = map.get("dishId");
            queryWrapper.eq(ShoppingCart::getDishId,id);
            shoppingCart = shoppingCartService.getOne(queryWrapper);
            Integer number = shoppingCart.getNumber();
            if(number > 1){
                updateWrapper.set(ShoppingCart::getNumber,number - 1)
                        .eq(ShoppingCart::getDishId, id);
                shoppingCartService.update(updateWrapper);
            } else {
                shoppingCartService.remove(queryWrapper);
            }

            return Result.success(shoppingCart);
        } else {
            id = map.get("setmealId");
            queryWrapper.eq(ShoppingCart::getSetmealId,id);
             shoppingCart = shoppingCartService.getOne(queryWrapper);
            Integer number = shoppingCart.getNumber();
            if(number > 1){
                updateWrapper.set(ShoppingCart::getNumber,number - 1)
                        .eq(ShoppingCart::getDishId, id);
                shoppingCartService.update(updateWrapper);
            } else {
                shoppingCartService.remove(queryWrapper);
            }

            return Result.success(shoppingCart);
        }
    }


    /**
     * 清空
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean(){
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);

        return Result.success("");
    }
}
