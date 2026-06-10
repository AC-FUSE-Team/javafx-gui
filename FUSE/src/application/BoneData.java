/**
 * 
 * @title 
 * @author 
 * @date 
 * @version
 */
public class BoneData {
    int id;
    String name;
    String group;
    int numbers;
    int[] color; // RGB, size 3

    public BoneData(int id, String name, String group, int numbers, int[] color) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.numbers = numbers;
        this.color = color;
    }

    public String getName() { return name; }
    public int[] getColor() { return color; }
}