package com.example.android.architecture.blueprints.todoapp;

/**
 * Created by cuijfboy on 2017/5/11.
 */

public abstract class SimpleUseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue>
        extends UseCase<Q, P, UseCase.Void, UseCase.Void> {
}
