package com.sky.mapper;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface TurnoverStatisticsMapper {
    /**
     * 查询营业额
     * @param begin end
     * @return
     */
    List<Orders> getTurnoverStatistics(LocalDate begin,LocalDate end);

    /**
     * 查询新增用户
     * @param begin
     * @param end
     * @return
     */
    Integer getNewUser(LocalDate begin, LocalDate end);

    /**
     * 查询总用户
     * @param end
     * @return
     */
    Integer getTotalUser(LocalDate end);


    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 查询销量排名
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> top10(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据动态条件查询营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);
}
