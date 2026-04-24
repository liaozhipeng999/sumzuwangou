package SpringBoot.controller;


import SpringBoot.dto.UserDTO;
import SpringBoot.excel.UserExcel;
import SpringBoot.service.UserService;

import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;

import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-17
 */
@RestController
@RequestMapping("/user")
@Tag(name="")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
        @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", in = ParameterIn.QUERY, required = true, ref="int") ,
        @Parameter(name = Constant.LIMIT, description = "每页显示记录数", in = ParameterIn.QUERY,required = true, ref="int") ,
        @Parameter(name = Constant.ORDER_FIELD, description = "排序字段", in = ParameterIn.QUERY, ref="String") ,
        @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)", in = ParameterIn.QUERY, ref="String")
    })
    @RequiresPermissions(":user:page")
    public Result<PageData<UserDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<UserDTO> page = userService.page(params);

        return new Result<PageData<UserDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @RequiresPermissions(":user:info")
    public Result<UserDTO> get(@PathVariable("id") Long id){
        UserDTO data = userService.get(id);

        return new Result<UserDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")

    @RequiresPermissions(":user:save")
    public Result save(@RequestBody UserDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        userService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")

    @RequiresPermissions(":user:update")
    public Result update(@RequestBody UserDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        userService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")

    @RequiresPermissions(":user:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        userService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")

    @RequiresPermissions(":user:export")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<UserDTO> list = userService.list(params);

    }

}
