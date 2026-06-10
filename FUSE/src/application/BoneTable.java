import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @title 
 * @author 
 * @date 
 * @version
 */
public class BoneTable {

    private List<BoneData> bones = new ArrayList<>();
    final int ALLOWANCE = 30; // color range

    public BoneTable(String csvPath) {
        loadCSV(csvPath);
    }

    private void loadCSV(String csvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            br.readLine(); 

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 
                
                String[] parts = line.split(","); 
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String group = parts[2].trim();
                int numbers = Integer.parseInt(parts[3].trim());

                int[] color = {
                    Integer.parseInt(parts[4].trim()), // R
                    Integer.parseInt(parts[5].trim()), // G
                    Integer.parseInt(parts[6].trim())  // B
                };

                bones.add(new BoneData(id, name, group, numbers, color));
            }
            System.out.println("Loaded " + bones.size() + " bones");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    public String findBoneByColor(int b, int g, int r) {
        for (BoneData bone : bones) {
            int[] c = bone.getColor(); 
            if (Math.abs(c[0] - r) <= ALLOWANCE &&
                Math.abs(c[1] - g) <= ALLOWANCE &&
                Math.abs(c[2] - b) <= ALLOWANCE) {
                return bone.getName();
            }
        }
        return "Unknown";
    }

    public List<BoneData> getAllBones() {
        return bones;
    }
}