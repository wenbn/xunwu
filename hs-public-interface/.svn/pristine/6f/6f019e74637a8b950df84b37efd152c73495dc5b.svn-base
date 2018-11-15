package www.ucforward.com.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> cls) {
        return (T) applicationContext.getBean(cls);
    }


    public static void main(String[] args) {
//        MessageConsumer servInfoService = (MessageConsumer)SpringContextUtils.getBean(MessageConsumer.class);
//        System.out.println(servInfoService);
    }
}
