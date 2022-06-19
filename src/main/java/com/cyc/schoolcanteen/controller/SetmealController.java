package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.dto.SetmealDto;
import com.cyc.schoolcanteen.entity.Category;
import com.cyc.schoolcanteen.entity.Setmeal;
import com.cyc.schoolcanteen.service.CategoryService;
import com.cyc.schoolcanteen.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-28 14:40
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新建套餐
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return Result.success("");
    }


    /**
     * dto分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");


        List<Setmeal> setmealList = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList = setmealList.stream().map(new SFunction<Setmeal, SetmealDto>() {
            @Override
            public SetmealDto apply(Setmeal setmeal) { // 具体的setmeal表
                SetmealDto setmealDto = new SetmealDto();
                BeanUtils.copyProperties(setmeal,setmealDto);
                Long categoryId = setmeal.getCategoryId();
                Category category = categoryService.getById(categoryId);
                setmealDto.setCategoryName(category.getName());
                return setmealDto;
            }
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList); // 之前ignore

        return Result.success(setmealDtoPage);
    }

    /**
     * 停售起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, String ids){
        String[] strings = ids.split(",");
        List<String> idList = Arrays.stream(strings).collect(Collectors.toList());

        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, status)
                .in(Setmeal::getId,idList);
        setmealService.update(updateWrapper);

        return Result.success("更改成功");
    }


    /**
     * 删除
     * @param idList
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") List<Long> idList){
        setmealService.deleteByStatus(idList);
        return Result.success("删除成功");
    }


    /**
     * 修改时回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> show(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.showWithDish(id);
        return Result.success(setmealDto);
    }


    /**
     * 修改
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return Result.success("套餐修改成功");
    }


    @Cacheable(value = "setmealCache", key = "#categoryId")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId){
        List<Setmeal> setmealList = setmealService.list(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, categoryId));
        return Result.success(setmealList);
    }
}
