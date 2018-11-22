public class MutationTest {
    public int foo() {
        int local1 = 42;
        int b = 23;

        return 43 + local1 * (b + 1) - b / 33;
    }

    public int bar(int param1, String param2) {
        int local1 = 42;
        int local2 = 2018;
        float fl = 4.3;

        functionCall(param2, fl, 5.3, 67, "string");

        if (param1 > 1234 && param1 <= 4567)
            var temp = returnObject(local1).thenInvokeMethod().andAnotherOne(local2);

        local1 = 64 / param1;
        return local1 + param1;
    }
}
