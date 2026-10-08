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

        // คำนวณจำนวนวันที่ผ่านไปตั้งแต่ปลูก
        int elapsedDays = currentDay - plantedDay;

        // ยังไม่ถึงครึ่งหนึ่งของจำนวนวันที่ใช้โต
        if (elapsedDays < growDays / 2) {
            return 1; // ระยะที่ 1 : ต้นอ่อน

        // ถึงครึ่งหนึ่งแล้ว แต่ยังไม่โตเต็มที่
        } else if (elapsedDays < growDays) {
            return 2; // ระยะที่ 2 : กำลังเติบโต

        // ครบจำนวนวันที่กำหนดแล้ว
        } else {
            return 3; // ระยะที่ 3 : โตเต็มที่พร้อมเก็บเกี่ยว
        }
    }

    // ตรวจสอบว่าผักพร้อมเก็บเกี่ยวหรือยัง
    public boolean isReadyToHarvest(int currentDay) {
        return getStage(currentDay) == 3;
    }
}