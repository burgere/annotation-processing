package io.rama.vehicle;

public class VehicleBuilder {
  private Vehicle object = new Vehicle();

  public VehicleBuilder year(int value) {
    object.setYear(value);
    return this;
  }

  public VehicleBuilder model(java.lang.String value) {
    object.setModel(value);
    return this;
  }

  public VehicleBuilder make(java.lang.String value) {
    object.setMake(value);
    return this;
  }

  public Vehicle build() {
    return object;
  }
}