import SpringBoot.Main;
import SpringBoot.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest(classes= {Main.class})
public class AppTest {
   @Resource
   private UserService userService;
   @Test
    public void show(){
       String name="peng";
       String password = "123456";
       boolean login = userService.isLogin(name, password);
       System.out.println(login);

   }
}
