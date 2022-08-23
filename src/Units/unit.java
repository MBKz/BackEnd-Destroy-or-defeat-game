package Units;

abstract public class unit extends Thread {
    protected int height, width, xUnit, yUnit;
    protected int price;
     String Type,name;

    public unit(int height, int width, int xUnit, int yUnit, int price, String type, String name) {
        this.height = height;
        this.width = width;
        this.xUnit = xUnit;
        this.yUnit = yUnit;
        this.price = price;
        this.Type = type;
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return xUnit;
    }

    public int getY() {
        return yUnit;
    }

    public String getType() {
        return Type;
    }

    public String getUNitName(){ return name;}

    @Override
    public String toString() {
        return
                "name= " + name + " | " +
                "position= ("+xUnit+","+yUnit+") : " + height + " : " + width
                ;
    }
}
