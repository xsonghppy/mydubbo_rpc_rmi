import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by xiangsong on 2018/2/3.
 */
public class TestServer {
    public static void main(String[] args) throws Exception {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("local/server.xml");
    }
}
