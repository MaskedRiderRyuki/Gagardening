public class FarmPlot {

    private boolean tilled;
    private Crop crop;

    public FarmPlot() {
        this.tilled = false;
        this.crop = null;
    }

    // พรวนดิน
    public void till() {
        tilled = true;
    }

    // ปลูก
    public boolean plant(Crop crop) {
        if (!tilled || this.crop != null) {
            return false;
        }

        this.crop = crop;
        return true;
    }

    // รดน้ำ
    public boolean water() {
        if (crop == null) {
            return false;
        }

        crop.water();
        return true;
    }

    // ดูว่ามีผักในช่องไหม
    public boolean hasCrop() {
        return crop != null;
    }

    // ดูผักในช่อง
    public Crop getCrop() {
        return crop;
    }

    // เก็บเกี่ยว
    public String harvest(int currentDay) {
        if (crop == null || !crop.isReadyToHarvest(currentDay)) {
            return null;
        }

        String cropName = crop.getName();

        // เก็บแล้วผักหายออกจากช่อง
        crop = null;

        return cropName;
    }
    public boolean isTilled() {
        return tilled;
    }
}
