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

    public <Q extends UseCase.RequestValues, P extends UseCase.ResponseValue, E extends UseCase.ErrorMessage> void execute(
            final UseCase<Q, P, E> useCase, Q values, UseCase.SuccessCallback<P> successCallback, UseCase.ErrorCallback<E> errorCallback) {

        useCase.setRequestValues(values);
        useCase.setSuccessCallback(new UiSuccessCallbackWrapper<P>(successCallback, this));
        useCase.setErrorCallback(new UiErrorCallbackWrapper<E>(errorCallback, this));

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
            final P response, final UseCase.SuccessCallback<P> successCallback) {
        mUseCaseScheduler.notifyResponse(response, successCallback);
    }

    public <E extends UseCase.ErrorMessage> void notifyError(
            final E errorMessage, final UseCase.ErrorCallback<E> errorCallback) {
        mUseCaseScheduler.onError(errorMessage, errorCallback);
    }

    private static final class UiSuccessCallbackWrapper<P extends UseCase.ResponseValue>
            implements UseCase.SuccessCallback<P> {

        private final UseCase.SuccessCallback<P> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiSuccessCallbackWrapper(UseCase.SuccessCallback<P> mCallback,
                                        UseCaseHandler mUseCaseHandler) {
            this.mCallback = mCallback;
            this.mUseCaseHandler = mUseCaseHandler;
        }

        @Override
        public void onSuccess(P response) {
            mUseCaseHandler.notifyResponse(response, mCallback);
        }
    }

    private static final class UiErrorCallbackWrapper<E extends UseCase.ErrorMessage>
            implements UseCase.ErrorCallback<E> {

        private final UseCase.ErrorCallback<E> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiErrorCallbackWrapper(UseCase.ErrorCallback<E> mCallback,
                                      UseCaseHandler mUseCaseHandler) {
            this.mCallback = mCallback;
            this.mUseCaseHandler = mUseCaseHandler;
        }

        @Override
        public void onError(E errorMessage) {
            mUseCaseHandler.notifyError(errorMessage, mCallback);
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
