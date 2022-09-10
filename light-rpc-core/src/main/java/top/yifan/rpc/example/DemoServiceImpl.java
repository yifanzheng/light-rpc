package top.yifan.rpc.example;

/**
 * @author Star Zheng
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public void print() {
        System.out.println("Hello");
    }

    @Override
    public String getName(String name) {
        return name;
    }
}
