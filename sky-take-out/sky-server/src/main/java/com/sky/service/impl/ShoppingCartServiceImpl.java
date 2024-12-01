package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询购物车是否存在菜品或套餐
        Long id = shoppingCartMapper.queryByUserIdAndDishId(shoppingCartDTO,userId);
        //存在，数量+1
        if (id!=null){
            log.info("购物车存在，数量+1");
            shoppingCartMapper.updateNumber(id);
            return;
        }
        //不存在，添加菜品或套餐
        log.info("购物车不存在，添加菜品或套餐：{}",shoppingCartDTO);
        if (shoppingCartDTO.getDishId() != null&&shoppingCartDTO.getSetmealId()==null){
            //添加菜品到购物车
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        }else if (shoppingCartDTO.getDishId()==null&&shoppingCartDTO.getSetmealId()!=null){
            //添加套餐到购物车
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        return list;
    }

    /**
     * 删除购物车某个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        //查询购物车餐品数量
        Integer number = shoppingCartMapper.queryNunmber(shoppingCartDTO,userId);
        if (number>1){
            //数量-1
            shoppingCartMapper.subNumber(shoppingCartDTO,userId);
        }else {
            //数量为1直接删除
            shoppingCartMapper.delete(shoppingCartDTO,userId);
        }

    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }
}
