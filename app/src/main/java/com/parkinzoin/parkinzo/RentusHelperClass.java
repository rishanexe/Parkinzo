package com.parkinzoin.parkinzo;

public class RentusHelperClass {

    String address,count,PlaceName;

    public RentusHelperClass() {
    }

    public RentusHelperClass(String address,String count,String PlaceName) {
        this.address = address;
        this.count = count;
        this.PlaceName=PlaceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleCount() {
        return count;
    }

    public void setVehicleCount(String count) {
        this.count = count;
    }

    public String getPlaceName() { return PlaceName; }

    public void setPlaceName(String PlaceName) { this.PlaceName = PlaceName; }
}
