package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface TurnoverStatisticsService {
    /**
     * 营业额统计
     * @param begin begin
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin , LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 销售top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    /**
     * 导出报表数据
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
