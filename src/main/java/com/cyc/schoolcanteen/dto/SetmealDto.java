package com.cyc.schoolcanteen.dto;


import com.cyc.schoolcanteen.entity.Setmeal;
import com.cyc.schoolcanteen.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
