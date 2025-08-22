import android.os.Build;

public class AppOpsCompatTest {
    public static void main(String[] args) {
        try {
            // Simulate the static initialization that was causing the crash
            System.out.println("Testing AppOpsManagerCompat static initialization...");
            
            // This would trigger the static block
            Class<?> compatClass = Class.forName("io.github.muntashirakon.AppManager.compat.AppOpsManagerCompat");
            
            // Try to access the fields that were causing issues
            java.lang.reflect.Field maxPriorityField = compatClass.getDeclaredField("MAX_PRIORITY_UID_STATE");
            java.lang.reflect.Field minPriorityField = compatClass.getDeclaredField("MIN_PRIORITY_UID_STATE");
            java.lang.reflect.Field opNoneField = compatClass.getDeclaredField("OP_NONE");
            
            System.out.println("MAX_PRIORITY_UID_STATE: " + maxPriorityField.getInt(null));
            System.out.println("MIN_PRIORITY_UID_STATE: " + minPriorityField.getInt(null));
            System.out.println("OP_NONE: " + opNoneField.getInt(null));
            
            System.out.println("✅ Test passed! Static initialization completed without errors.");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}