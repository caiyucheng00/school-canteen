package com.cyc.schoolcanteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.schoolcanteen.dto.DishDTO;
import com.cyc.schoolcanteen.entity.Dish;

import java.util.List;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-22 11:45
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavors(DishDTO dishDTO);
    void updateWithFlavors(DishDTO dishDTO);
    void deleteWithFlavors(List<Long> ids);
}
