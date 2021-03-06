/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: ViewHandler.java
 *
 * Created by Vladimir Yakushev at 5/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.widget;

import android.os.Handler;
import android.os.Message;

import com.BibleQuote.listeners.IReaderViewListener;

import java.lang.ref.WeakReference;

class ViewHandler extends Handler {

    static final int MSG_OTHER = 1;
    static final int MSG_ON_CLICK_IMAGE = 2;

    private WeakReference<IReaderViewListener> weakListener;

    public void setListener(IReaderViewListener listener) {
        this.weakListener = new WeakReference<>(listener);
    }

    @Override
    public void handleMessage(Message msg) {
        IReaderViewListener listener = weakListener.get();
        if (listener == null) {
            return;
        }

        switch (msg.what) {
            case MSG_ON_CLICK_IMAGE:
                listener.onReaderClickImage((String) msg.obj);
                break;
            default:
                listener.onReaderViewChange((IReaderViewListener.ChangeCode) msg.obj);
        }
        super.handleMessage(msg);
    }
}
