package top.yifan.rpc;

/**
 * DemoServiceImpl
 *
 * @author Star Zheng
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public void print() {
        System.out.println("Hello demo");
    }

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
