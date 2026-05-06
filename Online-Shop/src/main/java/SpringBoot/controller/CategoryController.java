package SpringBoot.controller;

import SpringBoot.entity.CategoryEntity;
import io.renren.common.utils.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import SpringBoot.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/getAllCategory")
    public Map<String,Object> get(){
        List<CategoryEntity> categoryEntities = categoryService.oneCategory();
        Map<String,Object> map = new HashMap<>();
        Result<Object> result = new Result<>().ok("请求成功");
        result.setCode(200);
        map.put("message",result);
        map.put("category",categoryEntities);
        return map;
    }
    @GetMapping("/categorySecond/{id}")
    public Map<String,Object> get05(@PathVariable(value = "id") String id){
        List<CategoryEntity> second=null;
        if(id.equals("-1")){
            second = categoryService.getAll();
        }else {
            second = categoryService.second(id);
        }
        Map<String,Object> map = new HashMap<>();
        Result<Object> result = new Result<>().ok("请求成功");
        result.setCode(200);
        map.put("message",result);
        map.put("second",second);
        return map;
    }
    @PostMapping("/search")
    public Map<String,Object> get06(@RequestBody String keyWord){

        List<CategoryEntity> search = categoryService.search(keyWord);

        System.out.println(search);

        Result<Object> objectResult = new Result<>();
        Map<String,Object> map = new HashMap<>();
        objectResult.setMsg("success");
        objectResult.setCode(200);
        map.put("category",search);
        map.put("result",objectResult);
        return map;
    }





}
