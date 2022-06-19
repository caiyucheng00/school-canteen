package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.schoolcanteen.dto.DishDTO;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.Category;
import com.cyc.schoolcanteen.entity.Dish;
import com.cyc.schoolcanteen.entity.DishFlavor;
import com.cyc.schoolcanteen.service.CategoryService;
import com.cyc.schoolcanteen.service.DishFlavorService;
import com.cyc.schoolcanteen.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-24 16:09
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService flavorService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 菜品添加
     *
     * @param dishDTO
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO) {

        dishService.saveWithFlavors(dishDTO);
        return Result.success("菜品添加成功");
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDTO>> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDTO> dishDTOPage = new Page<>();  // 这里添加菜品分类的名称

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDTOPage,"records");
        List<Dish> dishList = pageInfo.getRecords();// 获得具体dish表
        List<DishDTO> dishDTOList = dishList.stream().map(new SFunction<Dish, DishDTO>() {
            @Override
            public DishDTO apply(Dish dish) {  // dish表
                DishDTO dishDTO = new DishDTO();
                BeanUtils.copyProperties(dish,dishDTO);

                //填充 private String categoryName;
                Long categoryId = dish.getCategoryId();
                Category category = categoryService.getById(categoryId);
                if (category != null) {
                    String categoryName = category.getName();
                    dishDTO.setCategoryName(categoryName);
                }

                //填充 private List<DishFlavor> flavors;
                Long dishId = dish.getId();
                List<DishFlavor> flavorList = flavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishId));
                if(flavorList != null){
                    dishDTO.setFlavors(flavorList);
                }

                return dishDTO;
            }
        }).collect(Collectors.toList());

        dishDTOPage.setRecords(dishDTOList);

        return Result.success(dishDTOPage);
    }


    /**
     * 修改时回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDTO> show(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish,dishDTO);

        List<DishFlavor> flavors = flavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        dishDTO.setFlavors(flavors);

        return Result.success(dishDTO);
    }


    /**
     * 更新
     * @param dishDTO
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavors(dishDTO);
        return Result.success("菜品修改成功");
    }


    /**
     * （批量） 删除
     * @param ids
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @DeleteMapping
    public Result<String> delete(String ids){
        String[] strs = ids.split(",");
        List<Long> idList = new ArrayList<>();

        for (String str : strs) {
            long id = Long.parseLong(str);
            idList.add(id);
        }

        dishService.deleteWithFlavors(idList);

        return Result.success("删除成功");
    }


    /**
     * 更改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, String ids){
        String[] strs = ids.split(",");

        for (String str : strs) {
            long id = Long.parseLong(str);
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Dish::getStatus,status)
                            .eq(Dish::getId,id);
            dishService.update(updateWrapper);
        }

        return Result.success("更改成功");
    }


    /**
     * 新建套餐时按分类展示 --包括flavor
     * @param categoryId
     * @return
     */
    @Cacheable(value = "dishCache", key = "#categoryId")
    @GetMapping("/list")
    public Result<List<DishDTO>> list(Long categoryId){
        List<Dish> dishList = dishService.list(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId));
        List<DishDTO> dishDTOList = dishList.stream().map(new SFunction<Dish, DishDTO>() {
            @Override
            public DishDTO apply(Dish dish) { // dish表
                DishDTO dishDTO = new DishDTO();
                BeanUtils.copyProperties(dish,dishDTO);

                // 补充flavors
                Long dishId = dish.getId();
                List<DishFlavor> flavors = flavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishId));
                dishDTO.setFlavors(flavors);
                return dishDTO;
            }
        }).collect(Collectors.toList());

        return Result.success(dishDTOList);
    }
}
