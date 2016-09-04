package com.dferreira.commons.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;

/**
 * Caches the resources used in the game engine
 */
public class ResourcesCache extends LruCache<Integer, Object> {

    private static ResourcesCache instance;
    private static int KB_IN_BYTES = 1024;
    private static int MB_IN_KB = 1024;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    private ResourcesCache(int maxSize) {
        super(maxSize);
    }

    /**
     * Initializes the instance that is going to provide the cache
     * @param context
     */
    public static void initInstance(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        int memoryClassBytes = am.getMemoryClass() * MB_IN_KB * KB_IN_BYTES;
        instance = new ResourcesCache(memoryClassBytes / 8);
    }

    /**
     *
     * @return On instance of the resource cache
     */
    public static ResourcesCache getInstance() {
        return instance;
    }

}