package com.cyc.schoolcanteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.schoolcanteen.dto.SetmealDto;
import com.cyc.schoolcanteen.entity.Setmeal;

import java.util.List;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-22 11:45
 */
public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);
    void updateWithDish(SetmealDto setmealDto);
    SetmealDto showWithDish(Long id);
    void deleteByStatus(List<Long> idList);

}
