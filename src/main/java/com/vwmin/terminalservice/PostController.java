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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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

    private Map<String, Object> commandCache;

    public PostController(ApplicationContext applicationContext, CQClientApi cqClientApi) {
        this.applicationContext = applicationContext;
        this.commandCache = new ConcurrentHashMap<>(4);
        this.cqClientApi = cqClientApi;
    }

    @PostMapping("")
    public ResponseEntity<?> handRequest(@RequestBody PostEntity entity) {
        String[] args = entity.getMessage().split("\\s+");
        log.info("args >>> " + Arrays.toString(args));
        Object commandController = commandCache.get(args[0]);

        //开始设置命令内容
        Field[] fields = commandController.getClass().getDeclaredFields();
        for (Field field : fields) {
            CommandLine.Command annotation = field.getType().getAnnotation(CommandLine.Command.class);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    Object commandInstance = field.getType().newInstance();
                    new CommandLine(commandInstance).parseArgs(subArgs(args));
                    field.set(commandController, commandInstance);
                } catch (IllegalAccessException e) {
                    throw Utils.classError(field.getType(), "构造函数无法访问，详细: %s", e.getMessage());
                } catch (InstantiationException e) {
                    throw Utils.classError(field.getType(), "缺少无参构造函数，无法实例化对象");
                }
            }
        }

        // 开始执行命令
        try{
            if (commandController instanceof Send){
                ((Send) commandController).call(cqClientApi, entity);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else if (commandController instanceof Reply){
                return new ResponseEntity<>(((Reply) commandController).call(entity), HttpStatus.OK);
            }
        } catch (Exception e){
            ReplyEntity replyEntity = quickErrorResponse(entity.getUser_id(), e.getMessage());
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

    }


    public static ReplyEntity quickErrorResponse(Long userId, String cause){
        return new ReplyEntity(
                new MessageSegmentBuilder()
                    .addAtSegment(userId)
                    .addTextSegment(cause)
                    .build()
        );
    }
}
