package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.PostEntity;
import com.vwmin.terminalservice.entity.ReplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import picocli.CommandLine;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 10:42
 */
@Slf4j
@RestController
public class PostController {
    private final
    ApplicationContext applicationContext;

    private final
    CQClientApi cqClientApi;

    private final
    Map<String, Object> commandCache;

    private final
    List<CommandInterceptor> interceptors;

    public PostController(ApplicationContext applicationContext, CQClientApi cqClientApi) {
        this.applicationContext = applicationContext;
        this.commandCache = new ConcurrentHashMap<>(4);
        this.cqClientApi = cqClientApi;
        this.interceptors = new ArrayList<>();
    }

    @PostMapping("")
    public ResponseEntity<?> handRequest(@RequestBody PostEntity post) {
        //处理拦截
        AtomicReference<String> raw = new AtomicReference<>(post.getMessage());
        interceptors.forEach(interceptor->{
            if (interceptor.isMatch(post)){
                raw.set(interceptor.handle(post.getMessage()));
            }
        });

        //分割命令
        String[] args = raw.get().split("\\s+");
        log.info("args >>> " + Arrays.toString(args));
        Object commandController = commandCache.get(args[0]);
        if (commandController == null){
            log.info("无控制器匹配命令 {}, 已忽略", args[0]);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        //开始设置命令内容
        Field[] fields = commandController.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            CommandLine.Command annotation = fieldType.getAnnotation(CommandLine.Command.class);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    Constructor<?>[] constructors = fieldType.getConstructors();
                    if (constructors.length>1){
                        throw Utils.classError(fieldType, "检测到多个构造函数，无法选择自动注入");
                    }


                    Object commandInstance;
                    // 默认构造函数
                    if (constructors[0].getParameterCount() == 0){
                        commandInstance = fieldType.newInstance();

                    }
                    // 需要自动注入
                    else{
                        Object[] constructorParameters = new Object[constructors[0].getParameterCount()];
                        for (int i=0; i<constructors[0].getParameterCount(); i++){
                            final int index = i;
                            Class<?> parameter = constructors[0].getParameterTypes()[i];
                            Map<String, ?> beansOfType = applicationContext.getBeansOfType(parameter);
                            if (beansOfType.size()>1){
                                throw Utils.classError(fieldType, "注入%s时检测到%s个合适类型",
                                        parameter.getSimpleName(),
                                        beansOfType.size());
                            }
                            beansOfType.forEach((name, value) -> constructorParameters[index]=value);
                        }
                        commandInstance = constructors[0].newInstance(constructorParameters);

                    }
                    // 获得command实例后，写入参数
                    new CommandLine(commandInstance).parseArgs(subArgs(args));
                    field.set(commandController, commandInstance);



                } catch (IllegalAccessException e) {
                    throw Utils.classError(fieldType, "构造函数无法访问");
                } catch (InstantiationException e) {
                    throw Utils.classError(fieldType, "缺少无参构造函数，无法实例化对象");
                } catch (InvocationTargetException e) {
                    throw Utils.classError(fieldType, "初始化Command时发生内部异常: %s", e.getMessage());
                }
            }
        }

        // 开始执行命令
        try{
            if (commandController instanceof Send){
                ((Send) commandController).call(cqClientApi, post);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else if (commandController instanceof Reply){
                return new ResponseEntity<>(((Reply) commandController).call(post), HttpStatus.OK);
            }
        } catch (Exception e){
            ReplyEntity replyEntity = quickErrorResponse(post.getUser_id(), e.getMessage());
            return new ResponseEntity<>(replyEntity, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String[] subArgs(String[] args) {
        if (args.length == 1){
            return new String[0];
        }
        return Arrays.copyOfRange(args, 1, args.length);
    }

    @PostConstruct
    private void initHandlerMethods() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(CommandController.class);
        beansWithAnnotation.forEach((name, bean) -> {
            CommandController annotation = bean.getClass().getAnnotation(CommandController.class);
            String bind2 = annotation.bind();
            if (commandCache.containsKey(bind2)){
                String which = commandCache.get(bind2).getClass().getSimpleName();
                throw Utils.classError(bean.getClass(), "命令已有其他controller绑定(%s)，请考虑修改bind值(%s)", which, bind2);
            }
            commandCache.put(bind2, bean);
        });

        Map<String, CommandInterceptor> autoInterceptors = applicationContext.getBeansOfType(CommandInterceptor.class);
        interceptors.addAll(autoInterceptors.values());
    }


    public static ReplyEntity quickErrorResponse(Long userId, String cause){
        return new ReplyEntity(
                new MessageSegmentBuilder()
                    .at(userId)
                    .plainText(cause)
                    .build()
        );
    }
}
