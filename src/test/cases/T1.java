public class MutationTest {
    public int foo(int param) {
        int local = 42;
        int local2 = 42;
        local = 64 / param;
        return local + param;
    }
}
