/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp;


import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;

/**
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}.
 */
public class UseCaseHandler {

    private static volatile UseCaseHandler instance;

    private final UseCaseScheduler mUseCaseScheduler;

    public UseCaseHandler(UseCaseScheduler useCaseScheduler) {
        mUseCaseScheduler = useCaseScheduler;
    }

    public <Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> void execute(
            final UseCase<Q, P, UseCase.Void, UseCase.Void> useCase,
            Q values,
            UseCase.Callback<P> successCallback) {

        execute(useCase, values, successCallback, UseCase.emptyCallback(), UseCase.emptyCallback());
    }

    public <Q extends UseCase.RequestValues, P extends UseCase.ResponseValue, E extends UseCase.ErrorMessage> void execute(
            final UseCase<Q, P, E, UseCase.Void> useCase,
            Q values,
            UseCase.Callback<P> successCallback,
            UseCase.Callback<E> errorCallback) {

        execute(useCase, values, successCallback, errorCallback, UseCase.emptyCallback());
    }

    public <Q extends UseCase.RequestValues, P extends UseCase.ResponseValue, E extends UseCase.ErrorMessage, G extends UseCase.ProgressValue> void execute(
            final UseCase<Q, P, E, G> useCase,
            Q values,
            UseCase.Callback<P> successCallback,
            UseCase.Callback<E> errorCallback,
            UseCase.Callback<G> progressCallback) {

        useCase.setRequestValues(values);
        useCase.setSuccessCallback(new UiSuccessCallbackWrapper<>(successCallback, this));
        useCase.setErrorCallback(new UiErrorCallbackWrapper<>(errorCallback, this));
        useCase.setProgressCallback(new UiProgressCallbackWrapper<>(progressCallback, this));

        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mUseCaseScheduler.execute(new Runnable() {
            @Override
            public void run() {

                useCase.run();
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }
            }
        });
    }

    public <P extends UseCase.ResponseValue> void notifyResponse(
            final P response, final UseCase.Callback<P> successCallback) {
        mUseCaseScheduler.notifyResponse(response, successCallback);
    }

    public <E extends UseCase.ErrorMessage> void notifyError(
            final E errorMessage, final UseCase.Callback<E> errorCallback) {
        mUseCaseScheduler.onError(errorMessage, errorCallback);
    }

    public <G extends UseCase.ProgressValue> void notifyProgress(
            final G progress, final UseCase.Callback<G> progressCallback) {
        mUseCaseScheduler.notifyProgress(progress, progressCallback);
    }

    private static final class UiSuccessCallbackWrapper<P extends UseCase.ResponseValue>
            implements UseCase.Callback<P> {

        private final UseCase.Callback<P> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiSuccessCallbackWrapper(UseCase.Callback<P> mCallback,
                                        UseCaseHandler mUseCaseHandler) {
            this.mCallback = mCallback;
            this.mUseCaseHandler = mUseCaseHandler;
        }

        @Override
        public void call(P response) {
            mUseCaseHandler.notifyResponse(response, mCallback);
        }
    }

    private static final class UiErrorCallbackWrapper<E extends UseCase.ErrorMessage>
            implements UseCase.Callback<E> {

        private final UseCase.Callback<E> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiErrorCallbackWrapper(UseCase.Callback<E> mCallback,
                                      UseCaseHandler mUseCaseHandler) {
            this.mCallback = mCallback;
            this.mUseCaseHandler = mUseCaseHandler;
        }

        @Override
        public void call(E errorMessage) {
            mUseCaseHandler.notifyError(errorMessage, mCallback);
        }
    }

    private static final class UiProgressCallbackWrapper<G extends UseCase.ProgressValue>
            implements UseCase.Callback<G> {

        private final UseCase.Callback<G> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiProgressCallbackWrapper(UseCase.Callback<G> mCallback,
                                         UseCaseHandler mUseCaseHandler) {
            this.mCallback = mCallback;
            this.mUseCaseHandler = mUseCaseHandler;
        }

        @Override
        public void call(G progressValue) {
            mUseCaseHandler.notifyProgress(progressValue, mCallback);
        }
    }

    public static UseCaseHandler getInstance() {
        if (instance == null) {
            synchronized (UseCaseHandler.class) {
                if (instance == null) {
                    instance = new UseCaseHandler(new UseCaseThreadPoolScheduler());
                }
            }
        }
        return instance;
    }
}
