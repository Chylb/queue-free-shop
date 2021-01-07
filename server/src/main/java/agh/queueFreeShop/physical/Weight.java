package agh.queueFreeShop.physical;

public class Weight {
    private int weight = 0;

    public synchronized int readWeight() {
        return weight;
    }

    public synchronized void updateReading(int weight){
        this.weight = weight;
    }
}
