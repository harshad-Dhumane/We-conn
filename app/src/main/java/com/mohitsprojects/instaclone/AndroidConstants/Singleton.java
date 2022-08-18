package com.mohitsprojects.instaclone.AndroidConstants;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;


public class Singleton {
        private static Singleton mInstance = null;

        public List<Uri> bitmaps = new ArrayList<>();
        protected Singleton() {
        }

        public static synchronized Singleton getInstance() {
            if (null == mInstance) {
                mInstance = new Singleton();
            }
            return mInstance;
        }
    }
