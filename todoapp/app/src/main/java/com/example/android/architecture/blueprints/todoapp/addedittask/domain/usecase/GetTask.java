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

package com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.UseCase;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.model.Task;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves a {@link Task} from the {@link TasksRepository}.
 */
public class GetTask extends UseCase<GetTask.RequestValues, GetTask.ResponseValue, UseCase.Void> {

    private final TasksRepository mTasksRepository;

    public GetTask(@NonNull TasksRepository tasksRepository) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mTasksRepository.getTask(values.getTaskId(), new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                if (task != null) {
                    ResponseValue responseValue = new ResponseValue(task);
                    getSuccessCallback().onSuccess(responseValue);
                } else {
                    getErrorCallback().onError(EMPTY_RESPONSE_VALUE);
                }
            }

            @Override
            public void onDataNotAvailable() {
                getErrorCallback().onError(EMPTY_RESPONSE_VALUE);
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mTaskId;

        public RequestValues(@NonNull String taskId) {
            mTaskId = checkNotNull(taskId, "taskId cannot be null!");
        }

        public String getTaskId() {
            return mTaskId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Task mTask;

        public ResponseValue(@NonNull Task task) {
            mTask = checkNotNull(task, "task cannot be null!");
        }

        public Task getTask() {
            return mTask;
        }
    }
}
