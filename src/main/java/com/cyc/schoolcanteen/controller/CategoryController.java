package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.Category;
import com.cyc.schoolcanteen.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-20 22:24
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品、套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category){
        categoryService.save(category);

        return Result.success("分类添加成功！");
    }


    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Category>> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getType)
                .orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);
    }


    /**
     * 有判断依据的删除
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long id){
        categoryService.removeByRelated(id);

        return Result.success("删除成功！");
    }


    /**
     * 分类修改
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        categoryService.updateById(category);

        return Result.success("分类修改成功");
    }

    /**
     * 新建菜品中回显所有菜品分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null,Category::getType,type);
        List<Category> categoryList = categoryService.list(queryWrapper);

        return Result.success(categoryList);
    }
}
