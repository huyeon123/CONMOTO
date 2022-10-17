package com.huyeon.superspace.global.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//해당 객체가 어느 ApplicationContext에서 동작하는지 알기 위해 인터페이스를 구현
//리스너에서는 @Component를 사용하지 못해 @Autowired를 할 수 없다. 따라서 빈을 직접 주입받아 사용
@Component
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
