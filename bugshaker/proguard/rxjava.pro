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

-keepclassmembers class rx.schedulers.CachedThreadScheduler$EventLoopWorker {
    volatile int once;
}
