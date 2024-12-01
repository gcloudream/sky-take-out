package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户分页查询的数据模型")
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    @ApiModelProperty("员工姓名")
    private String name;

    //页码
    @ApiModelProperty("页码")
    private int page;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
