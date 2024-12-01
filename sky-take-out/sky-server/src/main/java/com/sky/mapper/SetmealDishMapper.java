package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishId(List<Long> ids);

    /**
     * 根据套餐id查询套餐菜品信息
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据套餐id删除套餐菜品数据
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 批量插入套餐和菜品的关联数据
     * @param setmealDishes
     */
    void insertDish(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id批量删除套餐和菜品的关联数据
     * @param ids
     */
    void deleteBySetmealIds(List<Long> ids);

    /**
     * 根据套餐id查询套餐起售状态和停售状态的菜品数量
     * @param id
     * @return
     */
    List<Integer> getDishStatusBySetmealId(Long id);
}
