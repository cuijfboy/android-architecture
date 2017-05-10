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
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue, E extends UseCase.ErrorMessage> {

    private Q mRequestValues;

    private SuccessCallback<P> mSuccessCallback;
    private ErrorCallback<E> mErrorCallback;

    public void setRequestValues(Q requestValues) {
        mRequestValues = requestValues;
    }

    public Q getRequestValues() {
        return mRequestValues;
    }

    public SuccessCallback<P> getSuccessCallback() {
        return mSuccessCallback;
    }

    public void setSuccessCallback(SuccessCallback<P> mSuccessCallback) {
        this.mSuccessCallback = mSuccessCallback;
    }

    public ErrorCallback<E> getErrorCallback() {
        return mErrorCallback;
    }

    public void setErrorCallback(ErrorCallback<E> mErrorCallback) {
        this.mErrorCallback = mErrorCallback;
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
     * Data received from a error.
     */
    public interface ErrorMessage {
    }

    public interface SuccessCallback<P extends UseCase.ResponseValue> {
        void onSuccess(P response);
    }

    public interface ErrorCallback<E extends UseCase.ErrorMessage> {
        void onError(E error);
    }

    public static final SuccessCallback<UseCase.ResponseValue> EMPTY_SUCCESS_CALLBACK =
            new SuccessCallback<ResponseValue>() {
                @Override
                public void onSuccess(ResponseValue response) {
                }
            };

    @SuppressWarnings("unchecked")
    public static <P extends UseCase.ResponseValue> SuccessCallback<P> emptySuccessCallback() {
        return (SuccessCallback<P>) EMPTY_SUCCESS_CALLBACK;
    }

    public static final ErrorCallback<UseCase.ErrorMessage> EMPTY_ERROR_CALLBACK =
            new ErrorCallback<ErrorMessage>() {
                @Override
                public void onError(ErrorMessage error) {
                }
            };

    @SuppressWarnings("unchecked")
    public static <E extends UseCase.ErrorMessage> ErrorCallback<E> emptyErrorMessage() {
        return (ErrorCallback<E>) EMPTY_ERROR_CALLBACK;
    }

    public static class Void implements RequestValues, ResponseValue, ErrorMessage {
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


}
