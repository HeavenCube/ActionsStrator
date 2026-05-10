import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) {
        for (Method m : JavaPlugin.class.getDeclaredMethods()) {
            if (m.getName().equals("registerCommand")) {
                System.out.println(m);
            }
        }
    }
}
