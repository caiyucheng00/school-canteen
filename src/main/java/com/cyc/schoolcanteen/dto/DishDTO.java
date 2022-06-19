package com.cyc.schoolcanteen.dto;

import com.cyc.schoolcanteen.entity.Dish;
import com.cyc.schoolcanteen.entity.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-24 16:11
 */
@Data
public class DishDTO extends Dish implements Serializable {
    private static final long serialVersionUID = -1;

    private List<DishFlavor> flavors;
    private String categoryName;
}
