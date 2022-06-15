package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.dto.DishDTO;
import com.cyc.schoolcanteen.entity.Dish;
import com.cyc.schoolcanteen.entity.DishFlavor;
import com.cyc.schoolcanteen.mapper.DishMapper;
import com.cyc.schoolcanteen.service.DishFlavorService;
import com.cyc.schoolcanteen.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-22 11:46
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService flavorService;

    @Transactional
    @Override
    public void saveWithFlavors(DishDTO dishDTO) {
        this.save(dishDTO);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dishDTO.getId();

        flavors = flavors.stream().map(new SFunction<DishFlavor, DishFlavor>() {
            @Override
            public DishFlavor apply(DishFlavor dishFlavor) {
                dishFlavor.setDishId(dishId);
                return dishFlavor;
            }
        }).collect(Collectors.toList());

        flavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void updateWithFlavors(DishDTO dishDTO) {
        this.updateById(dishDTO);

        // flavor先删除
        Long dishId = dishDTO.getId();
        flavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dishId));
        // 再添加
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors = flavors.stream().map(new SFunction<DishFlavor, DishFlavor>() {
            @Override
            public DishFlavor apply(DishFlavor dishFlavor) {
                dishFlavor.setDishId(dishId);
                return dishFlavor;
            }
        }).collect(Collectors.toList());
        flavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void deleteWithFlavors(List<Long> ids) {
        this.removeBatchByIds(ids);

        // 删除flavor
        for (Long dishId : ids) {
            flavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dishId));
        }
    }
}
