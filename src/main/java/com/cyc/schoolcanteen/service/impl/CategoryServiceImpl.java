package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.entity.Category;
import com.cyc.schoolcanteen.entity.Dish;
import com.cyc.schoolcanteen.entity.Setmeal;
import com.cyc.schoolcanteen.exception.CustomException;
import com.cyc.schoolcanteen.mapper.CategoryMapper;
import com.cyc.schoolcanteen.service.CategoryService;
import com.cyc.schoolcanteen.service.DishService;
import com.cyc.schoolcanteen.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-20 20:37
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void removeByRelated(Long id) {
        LambdaQueryWrapper<Dish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.eq(Dish::getCategoryId,id);
        LambdaQueryWrapper<Setmeal> queryWrapperSetmeal = new LambdaQueryWrapper<>();
        queryWrapperSetmeal.eq(Setmeal::getCategoryId,id);
        LambdaQueryWrapper<Category> queryWrapperCategory = new LambdaQueryWrapper<>();
        queryWrapperCategory.eq(Category::getId,id);

        long countDish = dishService.count(queryWrapperDish);
        if(countDish > 0){
            throw new CustomException("已关联菜品，无法删除");
        }

        long countSetmeal = setmealService.count(queryWrapperSetmeal);
        if(countSetmeal > 0){
            throw new CustomException("已关联套餐，无法删除");
        }

        // 可以删除
        super.remove(queryWrapperCategory);
    }
}
