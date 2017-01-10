package io.rama.vehicle;

public class App {
    public static void main(String[] args) {
        Vehicle v = new VehicleBuilder()
            .make("Toyota")
            .model("Corolla")
            .year(1995)
            .build();
        System.out.println(v);
    }
}
