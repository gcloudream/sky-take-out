package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品口味
     * @param flavors
     * @return
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);


    /**
     * 根据菜品id查询口味
     * @param DishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{DishId}")
    List<DishFlavor> getByDishId(Long DishId);



}
