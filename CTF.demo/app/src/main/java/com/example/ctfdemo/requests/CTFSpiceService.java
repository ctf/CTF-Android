package com.example.ctfdemo.requests;

import android.app.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.ctfdemo.tepid.Session;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.ObjectPersister;
import com.octo.android.robospice.persistence.ObjectPersisterFactory;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;
import com.octo.android.robospice.persistence.file.InFileObjectPersisterFactory;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.okhttp.OkHttpSpiceRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import okhttp3.OkHttpClient;
import roboguice.util.temp.Ln;

public class CTFSpiceService extends SpiceService {

    private OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient = createOkHttpClient();
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cm = new CacheManager();
        cm.addPersister(new GsonObjectPersisterFactory(application));

        return cm;
    }

    protected OkHttpClient createOkHttpClient() {
        return new OkHttpClient();
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof BaseTepidRequest) {
            BaseTepidRequest okHttpSpiceRequest = (BaseTepidRequest) request.getSpiceRequest();
            okHttpSpiceRequest.setOkHttpClient(okHttpClient);
        }
        super.addRequest(request, listRequestListener);
    }

    protected OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    // standalone gson persister for robospice
    // https://gist.github.com/doridori/68a13a2dc9648b4d6fd0
    public class GsonObjectPersisterFactory extends InFileObjectPersisterFactory {

        // ----------------------------------
        // CONSTRUCTORS
        // ----------------------------------

        public GsonObjectPersisterFactory(Application application, File cacheFolder) throws CacheCreationException {
            super(application, cacheFolder);
        }

        public GsonObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses, File cacheFolder)
                throws CacheCreationException {
            super(application, listHandledClasses, cacheFolder);
        }

        public GsonObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses)
                throws CacheCreationException {
            super(application, listHandledClasses);
        }

        public GsonObjectPersisterFactory(Application application) throws CacheCreationException {
            super(application);
        }

        // ----------------------------------
        // API
        // ----------------------------------

        @Override
        public <DATA> InFileObjectPersister<DATA> createInFileObjectPersister(Class<DATA> clazz, File cacheFolder)
                throws CacheCreationException {
            return new GsonObjectPersister<DATA>(getApplication(), clazz, cacheFolder);
        }

    }

    public final class GsonObjectPersister<T> extends InFileObjectPersister<T>
    {
        // ============================================================================================
        // FIELDS
        // ============================================================================================

        private final Gson gson;

        // ============================================================================================
        // CONSTRUCTOR
        // ============================================================================================

        public GsonObjectPersister(Application application, Class<T> clazz) throws CacheCreationException {
            this(application, clazz, null);
        }

        public GsonObjectPersister(Application application, Class<T> clazz, File cacheFolder) throws CacheCreationException {
            super(application, clazz, cacheFolder);
            this.gson = new Gson();
        }

        // ============================================================================================
        // PUBLIC
        // ============================================================================================

        @Override
        protected T readCacheDataFromFile(File file) throws CacheLoadingException {
            try {
                String resultJson = null;
                synchronized (file.getAbsolutePath().intern()) {
                    resultJson = FileUtils.readFileToString(file, CharEncoding.UTF_8);
                }
                if (!StringUtils.isEmpty(resultJson)) {
                    T result = deserializeData(resultJson);
                    return result;
                }
                throw new CacheLoadingException("Unable to restore cache content : cache file is empty");
            } catch (FileNotFoundException e) {
                // Should not occur (we test before if file exists)
                // Do not throw, file is not cached
                Ln.w("file " + file.getAbsolutePath() + " does not exists", e);
                return null;
            } catch (CacheLoadingException e) {
                throw e;
            } catch (Exception e) {
                throw new CacheLoadingException(e);
            }
        }

        @Override
        public T saveDataToCacheAndReturnData(final T data, final Object cacheKey) throws CacheSavingException {

            try {
                if (isAsyncSaveEnabled()) {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                saveData(data, cacheKey);
                            } catch (IOException e) {
                                Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                            } catch (CacheSavingException e) {
                                Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                            }
                        };
                    };
                    t.start();
                } else {
                    saveData(data, cacheKey);
                }
            } catch (CacheSavingException e) {
                throw e;
            } catch (Exception e) {
                throw new CacheSavingException(e);
            }
            return data;
        }


        // ============================================================================================
        // PRIVATE
        // ============================================================================================

        private T deserializeData(String json) {
            return gson.fromJson(json, getHandledClass());
        }

        private void saveData(T data, Object cacheKey) throws IOException, CacheSavingException {
            String resultJson;
            // transform the content in json to store it in the cache
            resultJson = gson.toJson(data);

            // finally store the json in the cache
            if (!StringUtils.isEmpty(resultJson)) {
                FileUtils.writeStringToFile(getCacheFile(cacheKey), resultJson, CharEncoding.UTF_8);
            } else {
                throw new CacheSavingException("Data was null and could not be serialized in json");
            }
        }

    }
}
