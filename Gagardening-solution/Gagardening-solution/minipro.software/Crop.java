public class Crop { // คลาสสำหรับเก็บข้อมูลและจัดการพืช 1 ต้น

    private String name; // เก็บชื่อผัก
    private int plantedDay; // เก็บวันที่ปลูกในเกม
    private int growDays; // จำนวนวันที่ใช้ในการเติบโตจนเต็มที่
    private boolean watered; // เก็บสถานะว่าผักถูกรดน้ำแล้วหรือยัง

    // Constructor สำหรับสร้างผักใหม่
    // รับชื่อผัก วันที่ปลูก และจำนวนวันที่ใช้ในการเติบโต
    public Crop(String name, int plantedDay, int growDays) {

        this.name = name; // เก็บชื่อผัก
        this.plantedDay = plantedDay; // เก็บวันที่ปลูก
        this.growDays = growDays; // เก็บจำนวนวันที่ใช้ในการเติบโต
        this.watered = false; // เริ่มต้นยังไม่ได้รดน้ำ
    }

    // เมธอดสำหรับรดน้ำผัก
    public void water() {
        watered = true; // เปลี่ยนสถานะเป็นรดน้ำแล้ว
    }

    // เมธอดสำหรับเรียกดูชื่อผัก
    public String getName() {
        return name;
    }

    // เมธอดสำหรับตรวจสอบว่าผักถูกรดน้ำแล้วหรือยัง
    public boolean isWatered() {
        return watered;
    }

    // เมธอดสำหรับตรวจสอบระยะการเติบโต
    // currentDay คือวันที่ปัจจุบันของเกม
    public int getStage(int currentDay) {
    int elapsedDays = currentDay - plantedDay;

    if (elapsedDays < growDays / 4) {
        return 1;
    } else if (elapsedDays < growDays / 2) {
        return 2;
    } else if (elapsedDays < growDays) {
        return 3;
    } else {
        return 4;
    }
}

    // ตรวจสอบว่าผักพร้อมเก็บเกี่ยวหรือยัง
    public boolean isReadyToHarvest(int currentDay) {
        return getStage(currentDay) == 4;
    }
}