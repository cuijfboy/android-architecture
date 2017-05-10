package com.example.android.architecture.blueprints.todoapp;

/**
 * Created by cuijfboy on 2017/5/10.
 */

public abstract class BasicUseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue, E extends UseCase.ErrorMessage>
        extends UseCase<Q, P, E, UseCase.Void> {
}
