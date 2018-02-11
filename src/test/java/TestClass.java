import com.mydubbo.servier.UserServer;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;


/**
 * Created by xiangsong on 2018/2/3.
 */
public class TestClass {
    public void run() {

    }

    public void run(String param) {

    }

    public void run(int param) {
        System.out.println("-----run(int param)--------"+param);
    }

    public static void main(String[] args) throws Exception {

        TestClass testClass=new TestClass();
        Method[] methods=testClass.getClass().getDeclaredMethods();
//        for (Method method : methods) {
//            System.out.println(method.getName()+"----方法---");
//            Class<?>[]  types= method.getParameterTypes();
//            for (int i = 0; i < types.length; i++) {
//                System.out.println(method.getName()+"----参数----"+i+"--------"+types[i].getName());
//            }
//        }


        Method method=testClass.getClass().getMethod("run",new Class<?>[]{int.class});
        System.out.println(method.getParameterTypes());
        method.invoke(testClass,1);

    }
}
