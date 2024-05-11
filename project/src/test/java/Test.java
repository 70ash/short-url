/**
 * @Author 70ash
 * @Date 2024/4/11 21:44
 * @Description:
 */
public class Test {
    public static final String s = "CREATE UNIQUE INDEX idx_unique_username_name  ON t_group_%d (name, username);";

    public static final String s2 = "ALTER TABLE t_user_%d drop COLUMN real_name;";
    @org.junit.Test
    public void createShardingTable() {
        for (int i = 0; i <= 15; i++) {
            System.out.printf(s2,i);
        }
    }
}
