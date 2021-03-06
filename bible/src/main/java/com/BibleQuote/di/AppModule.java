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
 * File: AppModule.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.di;

import android.content.Context;

import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.dal.controller.CacheModuleControllerImpl;
import com.BibleQuote.dal.controller.FsLibraryController;
import com.BibleQuote.dal.controller.TSKController;
import com.BibleQuote.dal.repository.FsCacheRepository;
import com.BibleQuote.dal.repository.FsHistoryRepository;
import com.BibleQuote.dal.repository.FsLibraryRepository;
import com.BibleQuote.dal.repository.XmlTskRepository;
import com.BibleQuote.dal.repository.bookmarks.DbBookmarksRepository;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.controller.ITSKController;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.ICacheRepository;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.domain.repository.ILibraryRepository;
import com.BibleQuote.domain.repository.ITskRepository;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.history.HistoryManager;
import com.BibleQuote.managers.history.IHistoryManager;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.PreferenceHelper;

import javax.inject.Singleton;

import dagger.Provides;

@dagger.Module
public class AppModule {

    private Context appContext;

    public AppModule(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    @Provides
    Context getAppContext() {
        return appContext;
    }

    @Provides
    @Singleton
    AsyncManager getAsyncManager() {
        return new AsyncManager();
    }

    @Provides
    @Singleton
    IBookmarksRepository getBookmarksRepository() {
        return new DbBookmarksRepository();
    }

    @Provides
    IHistoryManager getHistoryManager(IHistoryRepository repository, PreferenceHelper prefHelper) {
        return new HistoryManager(repository, prefHelper.getHistorySize());
    }

    @Provides
    IHistoryRepository getHistoryRepository(Context context) {
        return new FsHistoryRepository(context);
    }

    @Provides
    @Singleton
    Librarian getLibrarian(
            ILibraryController libraryController, ITSKController tskController,
            IHistoryManager historyManager, PreferenceHelper preferenceHelper) {
        return new Librarian(libraryController, tskController, historyManager, preferenceHelper);
    }

    @Provides
    @Singleton
    ILibraryController getLibraryController(Context context, ILibraryRepository<? extends Module> repository) {
        ICacheRepository cacheRepository = new FsCacheRepository(context.getFilesDir(), DataConstants.getLibraryCache());
        return new FsLibraryController(repository, new CacheModuleControllerImpl(cacheRepository));
    }

    @Provides
    ILibraryRepository<? extends Module> getLibraryRepository() {
        return new FsLibraryRepository();
    }

    @Provides
    PreferenceHelper getPreferenceHelper(Context context) {
        return new PreferenceHelper(context);
    }

    @Provides
    ITSKController getTskController(ITskRepository repository) {
        return new TSKController(repository);
    }

    @Provides
    ITskRepository getTskRepository() {
        return new XmlTskRepository();
    }
}
