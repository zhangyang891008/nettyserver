package s05;

public class TankMessage {
    public int x;
    public int y;
    public TankMessage(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMessage{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
