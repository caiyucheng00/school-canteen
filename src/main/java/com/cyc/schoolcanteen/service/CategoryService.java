package com.cyc.schoolcanteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.schoolcanteen.entity.Category;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-20 17:46
 */
public interface CategoryService extends IService<Category> {

    void removeByRelated(Long id);
}
