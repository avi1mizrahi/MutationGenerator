public int bar(int param1, String param2) {
    int local1 = 42;
    int local = 2018;
    float fl = 4.3;
    functionCall(param2, fl, 5.3, 67, "string");
    if (param1 > 1234 && 4567 >= param1) {
        var temp = returnObject(local1).thenInvokeMethod().andAnotherOne(local);
    }
    local1 = 64 / param1;
    return local1 + param1;
}
