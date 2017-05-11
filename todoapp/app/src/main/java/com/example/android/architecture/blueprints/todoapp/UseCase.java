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

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 * @param <E> the error message type
 * @param <G> the progress type
 */
public abstract class UseCase<
        Q extends UseCase.RequestValues,
        P extends UseCase.ResponseValue,
        E extends UseCase.ErrorMessage,
        G extends UseCase.ProgressValue> {

    private Q mRequestValues;

    private Callback<P> mSuccessCallback;
    private Callback<E> mErrorCallback;
    private Callback<G> mProgressCallback;

    public void setRequestValues(Q requestValues) {
        mRequestValues = requestValues;
    }

    public Q getRequestValues() {
        return mRequestValues;
    }

    public Callback<P> getSuccessCallback() {
        return mSuccessCallback;
    }

    public void setSuccessCallback(Callback<P> successCallback) {
        this.mSuccessCallback = successCallback;
    }

    public Callback<E> getErrorCallback() {
        return mErrorCallback;
    }

    public void setErrorCallback(Callback<E> errorCallback) {
        this.mErrorCallback = errorCallback;
    }

    public Callback<G> getProgressCallback() {
        return mProgressCallback;
    }

    public void setProgressCallback(Callback<G> progressCallback) {
        this.mProgressCallback = progressCallback;
    }

    void run() {
        executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(Q requestValues);

    /**
     * Data passed to a request.
     */
    public interface RequestValues {
    }

    /**
     * Data received from a request.
     */
    public interface ResponseValue {
    }

    /**
     * Data received in progress.
     */
    public interface ProgressValue {
    }

    /**
     * Data received from an error.
     */
    public interface ErrorMessage {
    }

    public interface Callback<V> {
        void call(V value);
    }

    public static final Callback EMPTY_CALLBACK = new Callback() {
        @Override
        public void call(Object value) {
        }
    };

    @SuppressWarnings("unchecked")
    public static <V> Callback<V> emptyCallback() {
        return (Callback<V>) EMPTY_CALLBACK;
    }

    public static class Void implements RequestValues, ResponseValue, ErrorMessage, ProgressValue {
    }

    public static final Void EMPTY_REQUEST_VALUES = new Void() {
        @Override
        public String toString() {
            return "EMPTY_REQUEST_VALUES";
        }
    };

    public static final Void EMPTY_RESPONSE_VALUE = new Void() {
        @Override
        public String toString() {
            return "EMPTY_RESPONSE_VALUE";
        }
    };

    public static final Void EMPTY_ERROR_MESSAGE = new Void() {
        @Override
        public String toString() {
            return "EMPTY_ERROR_MESSAGE";
        }
    };

    public static final Void EMPTY_PROGRESS_VALUE = new Void() {
        @Override
        public String toString() {
            return "EMPTY_PROGRESS_VALUE";
        }
    };

}

