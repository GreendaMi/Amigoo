package me.ele.amigo;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ele.amigo.reflect.FieldUtils;
import me.ele.amigo.stub.ActivityStub;
import me.ele.amigo.utils.component.ActivityFinder;

import static me.ele.amigo.utils.component.ActivityFinder.getActivityInfoInNewApp;

public class AmigoInstrumentation extends Instrumentation implements IInstrumentation {

    public static final String EXTRA_TARGET_INTENT = "me.ele.amigo.OldIntent";
    public static final String EXTRA_TARGET_INFO = "me.ele.amigo.OldInfo";
    public static final String EXTRA_STUB_NAME = "me.ele.amigo.stub";

    private static final String TAG = AmigoInstrumentation.class.getSimpleName();

    private Instrumentation oldInstrumentation;
    private Method methodExecStart1;
    private Method methodExecStart2;
    private Method methodExecStart3;
    private Method methodExecStart4;
    private Method methodExecStart5;
    private Method methodExecStart6;

    @Override
    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        return oldInstrumentation.newActivity(cl, className, intent);
    }

    public AmigoInstrumentation(Instrumentation oldInstrumentation) {
        this.oldInstrumentation = oldInstrumentation;
    }

    private Intent wrapIntent(Context who, Intent intent) {
        Amigo.rollAmigoBack(who);

        Pair<Boolean, Intent> result = getRealIntent(who, intent);
        if (result == null) {
            return null;
        }
        if (result.first) {
            return intent;
        }

        ComponentName componentName = intent.getComponent();
        ActivityStub.recycleActivityStub(getActivityInfo(who, componentName.getClassName()));
        Class stubClazz = getDelegateActivityName(who, componentName.getClassName());
        if (stubClazz == null) {
            Log.e(TAG, "wrapIntent: weird, no stubs available for now.");
            return intent;
        }

        Intent stubIntent = new Intent();
        stubIntent.setComponent(new ComponentName(componentName.getPackageName(), stubClazz.getName()));
        stubIntent.putExtra(EXTRA_TARGET_INTENT, intent);
        stubIntent.setFlags(intent.getFlags());
        intent.putExtra(EXTRA_STUB_NAME, stubClazz);
        ActivityStub.onActivityCreated(stubClazz, null, componentName.getClassName());
        return stubIntent;
    }

    private Pair<Boolean, Intent> getRealIntent(Context who, Intent intent) {
        if (intent == null) {
            return null;
        }
        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            componentName = intent.resolveActivity(who.getPackageManager());
            intent.setComponent(componentName);
        }

        if (componentName != null) {
            //search from host
            ActivityInfo[] activityInfos = ActivityFinder.getAppActivities(who);
            for (ActivityInfo activityInfo : activityInfos) {
                if (activityInfo.name.equals(componentName.getClassName())) {
                    boolean isAlias = !TextUtils.isEmpty(activityInfo.targetActivity);
                    if (!isAlias) {
                        return new Pair<>(true, intent);
                    } else {
                        intent.setComponent(new ComponentName(componentName.getPackageName(), activityInfo.targetActivity));
                        return getRealIntent(who, intent);
                    }
                }
            }
        }

        //search from patch
        ActivityInfo info = getActivityInfoInNewApp(who, intent);
        if (info == null) {
            return new Pair<>(true, intent);
        }
        intent.setComponent(new ComponentName(who.getPackageName(), info.name));
        return new Pair<>(false, intent);
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart1 == null) {
                methodExecStart1 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",
                                Context.class, IBinder.class, IBinder.class, Activity.class,
                                Intent.class,
                                int.class, Bundle.class);
            }
            return (ActivityResult) methodExecStart1.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Activity target,
                                            Intent intent, int requestCode) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart2 == null) {
                methodExecStart2 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",
                                Context.class, IBinder.class, IBinder.class, Activity.class,
                                Intent.class,
                                int.class);
            }
            return (ActivityResult) methodExecStart2.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Fragment target, Intent intent, int requestCode) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart3 == null) {
                methodExecStart3 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",
                                Context.class, IBinder.class, IBinder.class, Fragment.class,
                                Intent.class,

                                int.class, Bundle.class);
            }
            return (ActivityResult) methodExecStart3.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Fragment target, Intent intent, int requestCode,
                                            Bundle options) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart4 == null) {
                methodExecStart4 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",

                                Context.class, IBinder.class, IBinder.class, Fragment.class,
                                Intent.class,
                                int.class, Bundle.class);
            }
            return (ActivityResult) methodExecStart4.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Activity target, Intent intent, int requestCode,
                                            Bundle options, UserHandle user) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart5 == null)
                methodExecStart5 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",
                                Context.class, IBinder.class, IBinder.class, Activity.class,
                                Intent.class,
                                int.class, Bundle.class, UserHandle.class);
            return (ActivityResult) methodExecStart5.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode, options, user);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            String target, Intent intent, int requestCode, Bundle
                                                    options) {
        try {
            intent = wrapIntent(who, intent);
            if (methodExecStart6 == null)
                methodExecStart6 = oldInstrumentation.getClass().getDeclaredMethod
                        ("execStartActivity",
                                Context.class, IBinder.class, IBinder.class, String.class, Intent
                                        .class, int
                                        .class, Bundle.class);
            return (ActivityResult) methodExecStart6.invoke(oldInstrumentation, who,
                    contextThread, token,
                    target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class getDelegateActivityName(Context context, String targetClassName) {
        return ActivityStub.selectActivityStubClazz(getActivityInfo(context, targetClassName));
    }

    private ActivityInfo getActivityInfo(Context context, String targetClassName) {
        if (targetClassName.startsWith(".")) {
            targetClassName = context.getPackageName() + targetClassName;
        }

        ActivityInfo info = getActivityInfoInNewApp(context, targetClassName);
        if (info == null) {
            throw new RuntimeException(String.format("cannot find %s in apk", targetClassName));
        }
        return info;
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        super.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try {
            boolean result = Amigo.rollAmigoBack(activity);
            Intent targetIntent = activity.getIntent();
            if (targetIntent != null) {

                if (result) {
                    targetIntent.setExtrasClassLoader(activity.getClassLoader());
                }

                ActivityInfo targetInfo = targetIntent.getParcelableExtra(EXTRA_TARGET_INFO);
                if (targetInfo != null) {
                    activity.setRequestedOrientation(targetInfo.screenOrientation);

                    ComponentName componentName = new ComponentName(activity,
                            getDelegateActivityName(activity, activity.getClass().getName()));
                    FieldUtils.writeField(activity, "mComponent", componentName);

                    Class stubClazz = (Class) targetIntent.getSerializableExtra(EXTRA_STUB_NAME);
                    if (stubClazz != null)
                        ActivityStub.onActivityCreated(stubClazz, activity, "");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (oldInstrumentation != null) {
            oldInstrumentation.callActivityOnCreate(activity, icicle);
        } else {
            super.callActivityOnCreate(activity, icicle);
        }

    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if (oldInstrumentation != null) {
            oldInstrumentation.callActivityOnDestroy(activity);
        } else {
            super.callActivityOnDestroy(activity);
        }

        Intent intent = activity.getIntent();
        Class stubClazz;
        if (intent != null && (stubClazz = (Class) intent.getSerializableExtra(EXTRA_STUB_NAME))
                != null)
            ActivityStub.onActivityDestroyed(stubClazz, activity);
    }


}
