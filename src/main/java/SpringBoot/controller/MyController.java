package SpringBoot.controller;

import SpringBoot.dto.UserDTO;
import SpringBoot.entity.UserEntity;
import SpringBoot.service.UserService;
import io.renren.common.utils.Result;
import jakarta.annotation.Resource;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MyController {
    @Resource
    private UserService userService;

//    @GetMapping("/login/{name}/{password}")
//    @ResponseBody
//    public Result login(@PathVariable(value = "name") String name, @PathVariable(value = "password") String password){
//        boolean login = userService.isLogin(name, password);
//        Result<Object> objectResult = new Result<>();
//        if (login){
//            return objectResult.ok("登录成功");
//        }
//        return objectResult.error("登录失败");
//    }





    @PostMapping("/login")
    @ResponseBody
    public Result login2(@RequestBody UserDTO userDTO){
       UserEntity login = userService.isLogin(userDTO.getName(),userDTO.getPassword());
        Result<Object> objectResult = new Result<>();
        if (login != null){
            objectResult.setData(login);
            objectResult.setCode(0);
            return objectResult;
        }
        return objectResult.error("登录失败");
    }
    @PostMapping("/register")
    @ResponseBody
    public Result reg(@RequestBody UserDTO userDTO) {
        int register = userService.register(userDTO.getName(),userDTO.getPassword(),userDTO.getEmail());
        Result<Object> objectResult = new Result<>();
        if (register == 1){
            objectResult.setCode(1);
            objectResult.setMsg("注册失败");
            return objectResult;
        }

        objectResult.setCode(0);
        return objectResult.ok("注册成功");
    }
    @GetMapping("/getUser/{id}")
    @ResponseBody
    public Result getUser(@PathVariable("id") Integer id){
        UserEntity userEntity = userService.selectById(id);
        Result<Object> objectResult = new Result<>();
        userEntity.setPassword(null);
        objectResult.setCode(200);
        objectResult.setData(userEntity);
        System.out.println(userEntity);
        userEntity.setPassword(null);

        System.out.println('y');
        return objectResult;
    }



}
