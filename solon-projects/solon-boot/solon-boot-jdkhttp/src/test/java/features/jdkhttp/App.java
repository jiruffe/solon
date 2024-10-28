package features.jdkhttp;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("hello")
    public String hello(String name) {
        return "hello " + name;
    }

    @Mapping("body")
    public String body(String tag, @Body String body) {
        return tag + ":" + body;
    }

    @Mapping("async")
    public void async(Context ctx) {
        try {
            ctx.asyncStart();
            ctx.output("async");
        } finally {
            ctx.asyncComplete();
        }
    }

    @Mapping("async_timeout")
    public void async_timeout(Context ctx) {
        ctx.asyncStart(100L, null);
    }
}
