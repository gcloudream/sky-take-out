package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.TurnoverStatisticsMapper;
import com.sky.service.TurnoverStatisticsService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Admin
 */
@Service
@Slf4j
public class TurnoverStatisticsServiceImpl implements TurnoverStatisticsService {

    @Autowired
    private TurnoverStatisticsMapper turnoverStatisticsMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin,LocalDate end) {
        List<Orders> orders = turnoverStatisticsMapper.getTurnoverStatistics(begin,end.plusDays(1));
        log.info("营业额统计结果：{}",orders);
        //从begin到end进行循环，统计每日盈利
        Map<LocalDate, BigDecimal> map=new HashMap<>();
        while (begin.isBefore(end)){
            map.put(begin, BigDecimal.valueOf(0.0));
            begin=begin.plusDays(1);
        }
        map.put(end, BigDecimal.valueOf(0.0));
        for (Orders order : orders) {
            LocalDateTime orderTime = order.getOrderTime();
            BigDecimal amount = order.getAmount();
            LocalDate time = LocalDate.from(orderTime);
            map.put(time, map.get(time).add(amount));
        }
        //日期列表，日期之间以逗号分隔. 营业额列表，营业额之间以逗号分隔
        String dateList=map.keySet().stream().map(LocalDate::toString).reduce("",(s1,s2)->s2+","+s1);
        String turnoverList=map.values().stream().map(BigDecimal::toString).reduce("",(s1,s2)->s2+","+s1);
        return TurnoverReportVO.builder()
                .dateList(dateList)
                .turnoverList(turnoverList)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list=new ArrayList<>();
        list.add(begin);
        while(begin.isBefore(end)){
            list.add(begin);
            begin=begin.plusDays(1);
        }
        list.add(end);
        List<Integer> newUserList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();
        for (int i=1;i<list.size();i++){
            newUserList.add(turnoverStatisticsMapper.getNewUser(list.get(i-1),list.get(i)));
            totalUserList.add(turnoverStatisticsMapper.getTotalUser(list.get(i)));
        }
        list.remove(0);
        return UserReportVO.builder()
                .dateList(StringUtils.join(list,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历dateList集合，查询每天的有效订单数和订单总数
        for (LocalDate date : dateList) {
            //查询每天的订单总数 select count(id) from orders where order_time > ? and order_time < ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            //计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return  OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = turnoverStatisticsMapper.top10(beginTime,endTime);
        log.info("top10：{}",top10);
        List<String> nameList = top10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = top10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出营业数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //获取营业数据，最近30天
        LocalDate datebegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        //查询概述数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(datebegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
        //将poi数据导入到excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");


        try {
            //基于模板文件创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取sheet页
            XSSFSheet sheet = excel.getSheetAt(0);
            //填充数据时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + datebegin + "至" + dateEnd);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row=sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = datebegin.plusDays(i);
                //查询某一天的营业数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //通过输入流将excel文件通过浏览器下载到本地
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件统计订单数量
     * @param begin1
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin1, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin",begin1);
        map.put("end",end);
        map.put("status",status);

        return turnoverStatisticsMapper.countByMap(map);
    }
}
