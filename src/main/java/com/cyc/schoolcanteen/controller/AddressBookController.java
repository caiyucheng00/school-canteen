package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cyc.schoolcanteen.common.BaseContext;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.AddressBook;
import com.cyc.schoolcanteen.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-31 14:52
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 展示列表
     * @param session
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(HttpSession session){
        Long userID = (Long) session.getAttribute("userPhone");

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userID)
                .orderByDesc(AddressBook::getIsDefault)
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);
        return Result.success(addressBookList);
    }


    /**
     * 新建
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<String> add(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getId();
        addressBook.setUserId(userId);

        addressBookService.save(addressBook);
        return Result.success("");
    }


    /**
     * 更新
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return Result.success("");
    }


    /**
     * 回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> show(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);

        return Result.success(addressBook);
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook){
        // 先全部设置为0，后单独设置为1
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getIsDefault,0)
                .eq(AddressBook::getUserId, BaseContext.getId());
        addressBookService.update(updateWrapper);

        LambdaUpdateWrapper<AddressBook> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.set(AddressBook::getIsDefault,1)
                .eq(AddressBook::getId,addressBook.getId());
        addressBookService.update(updateWrapper1);

        return Result.success("");
    }


    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
        addressBookService.removeById(ids);
        return Result.success("");
    }


    @GetMapping("/default")
    public Result<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,1)
                .eq(AddressBook::getUserId,BaseContext.getId());
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        return Result.success(addressBook);
    }
}
