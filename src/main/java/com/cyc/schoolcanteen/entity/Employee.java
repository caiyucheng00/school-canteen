package com.cyc.schoolcanteen.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-12 17:28
 */
@Data
public class Employee {
    private Long id;
    private String name;
    private String username; // 唯一
    private String password;
    private String phone;
    private String sex;
    private String idCard;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
