package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.result.Result;
import com.sky.service.TurnoverStatisticsService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Api(tags = "营业额统计相关接口")
@Slf4j
@RequestMapping("/admin/report")
public class TurnoverStatisticsController {

    @Autowired
    private TurnoverStatisticsService turnoverStatisticsService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        TurnoverReportVO turnoverReportVO =turnoverStatisticsService.getTurnoverStatistics(begin,end);
        log.info("营业额统计结果：{}",turnoverReportVO);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计开始");
        UserReportVO userReportVO =turnoverStatisticsService.userStatistics(begin,end);
        log.info("用户统计结果：{}",userReportVO);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计开始");
        OrderReportVO orderReportVO =turnoverStatisticsService.ordersStatistics(begin,end);
        log.info("订单统计结果：{}",orderReportVO);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排名开始");
        SalesTop10ReportVO salesTop10ReportVO =turnoverStatisticsService.top10(begin,end);
        log.info("销量排名结果：{}",salesTop10ReportVO);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据报表")
    public void export(HttpServletResponse response){
        turnoverStatisticsService.exportBusinessData(response);
    }
}
