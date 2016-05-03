package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.test.RenamingDelegatingContext;

public class TestUtil {

    public static DaggerActivityTestRule<MainActivity> TestRule(){
        return new DaggerActivityTestRule<>(MainActivity.class, new DaggerActivityTestRule.OnBeforeActivityLaunchedListener<MainActivity>() {
            @Override
            public void beforeActivityLaunched(@NonNull Application application, @NonNull MainActivity activity) {
                Context renamedContext = new RenamingDelegatingContext(application, "Testus");
                DatabaseUtil.init(renamedContext);
            }
        });
    }
}
