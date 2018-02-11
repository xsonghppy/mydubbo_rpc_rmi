import com.mydubbo.servier.UserServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Set;


/**
 * Created by xiangsong on 2018/2/3.
 */
public class TestConsumer {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("consumer.xml");

//        Map<String, UserServer> map = applicationContext.getBeansOfType(UserServer.class);
//        Set<Map.Entry<String, UserServer>> entrySet = map.entrySet();
//        for(Map.Entry<String, UserServer> entry:entrySet){
//            String key=entry.getKey();
//            UserServer userServer=entry.getValue();
//            for (int i=0; i<5;i++){
//                userServer.eat(key+",meat:"+i);
//                userServer.eat(i);
//                userServer.eat();
//            }
//        }

        //轮询
//        UserServer userServer = (UserServer) applicationContext.getBean("userServer");
//        for (int i = 0; i < 5; i++) {
//            String result=userServer.eat("eat---userServer---中文没有乱码了--------meat" + i);
//            System.out.println(result);
//
//            String result1=userServer.eat(i);
//            System.out.println(result1);
//
//            String result2=userServer.eat();
//            System.out.println(result2);
//        }

        UserServer userServer1 = (UserServer) applicationContext.getBean("userServer1");
        for (int i = 0; i < 1; i++) {
            String result1 = userServer1.eat(userServer1.toString() + "---------" + "eat---userServer1---中文没有乱码了--------meat" + i);
            System.out.println(result1);
        }

    }
}
