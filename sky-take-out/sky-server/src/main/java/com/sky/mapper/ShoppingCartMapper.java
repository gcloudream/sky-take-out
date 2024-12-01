package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 新增菜品或套餐
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id和菜品或套餐id查询
     * @param shoppingCartDTO userId
     * @return
     */
    Long queryByUserIdAndDishId(ShoppingCartDTO shoppingCartDTO,Long userId);


    /**
     * 增加菜品数量
     * @param id
     */
    void updateNumber(Long id);

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> list(Long userId);

    /**
     * 删除购物车
     * @param shoppingCartDTO
     * @param userId
     */
    void delete(ShoppingCartDTO shoppingCartDTO, Long userId);

    /**
     * 查询购物车菜品数量
     * @param shoppingCartDTO
     * @param userId
     * @return
     */
    Integer queryNunmber(ShoppingCartDTO shoppingCartDTO, Long userId);

    /**
     * 减少菜品数量
     * @param shoppingCartDTO
     */
    void subNumber(ShoppingCartDTO shoppingCartDTO,Long userId);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clean(Long userId);
}
