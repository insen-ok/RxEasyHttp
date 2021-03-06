/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhouyou.http.cache.stategy;


import com.zhouyou.http.cache.RxCache;
import com.zhouyou.http.cache.model.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;


/**
 * <p>描述：先请求网络，网络请求失败，再加载缓存</p>
 *<-------此类加载用的是反射 所以类名是灰色的 没有直接引用  不要误删----------------><br>
 * 作者： zhouyou<br>
 * 日期： 2016/12/24 10:35<br>
 * 版本： v2.0<br>
 */
public final class FirstRemoteStrategy extends  BaseStrategy{
    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache,type,key,time);
        Observable<CacheResult<T>> remote = loadRemote(rxCache,key, source)
                .onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(Throwable throwable) {
                        return null;
                    }
                });
        return Observable.concat(remote, cache)
                .firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> tResultData) {
                        return tResultData != null && tResultData.data != null;
                    }
                });

    }
}
