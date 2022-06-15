package com.cyc.schoolcanteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.schoolcanteen.dto.SetmealDto;
import com.cyc.schoolcanteen.entity.Setmeal;
import com.cyc.schoolcanteen.entity.SetmealDish;
import com.cyc.schoolcanteen.exception.CustomException;
import com.cyc.schoolcanteen.mapper.SetmealMapper;
import com.cyc.schoolcanteen.service.SetmealDishService;
import com.cyc.schoolcanteen.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-22 11:47
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        // 填充 setmealId
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(new SFunction<SetmealDish, SetmealDish>() {
            @Override
            public SetmealDish apply(SetmealDish setmealDish) {
                setmealDish.setSetmealId(setmealId);
                return setmealDish;
            }
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        // 关联表 先全部删除，再插入
        Long setmealId = setmealDto.getId();
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId,setmealId));

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(new SFunction<SetmealDish, SetmealDish>() {
            @Override
            public SetmealDish apply(SetmealDish setmealDish) {
                setmealDish.setSetmealId(setmealId);
                return setmealDish;
            }
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public SetmealDto showWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        // setmealdishes
        List<SetmealDish> setmealDishes = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmeal.getId()));
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Transactional
    @Override
    public void deleteByStatus(List<Long> idList) {
        // 若停售才能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,idList)
                .eq(Setmeal::getStatus,1);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new CustomException("起售中,无法删除");
        }

        this.removeBatchByIds(idList);

        // 删除关联菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,idList);
        setmealDishService.remove(queryWrapper1);
    }
}
