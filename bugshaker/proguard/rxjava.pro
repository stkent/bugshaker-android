# Configuration from https://github.com/cypressious/RxJavaProGuardRules/blob/2fcb72b/rxjava-proguard-rules/proguard-rules.txt
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# FIXME: remove this when Android Gradle plugin is fixed.
# See https://github.com/artem-zinnatullin/RxJavaProGuardRules/pull/23#issuecomment-181529232
# https://github.com/artem-zinnatullin/RxJavaProGuardRules/issues/22
# https://code.google.com/p/android/issues/detail?id=200518
-keepclassmembers class rx.schedulers.CachedThreadScheduler$EventLoopWorker {
    volatile int once;
}
