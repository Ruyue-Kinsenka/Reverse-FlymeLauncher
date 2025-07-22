package com.android.quickstep;

public class RemoteTargetGluer<TaskViewSimulator> {


    public TaskViewSimulator getTaskViewSimulator() {
        return null;
    }
    public static class RemoteTargetHandle<TaskViewSimulator, TransformParams> {
        public RemoteTargetHandle(TaskViewSimulator taskViewSimulator, TransformParams transformParams, TaskViewSimulator mTaskViewSimulator) {
            this.mTaskViewSimulator = mTaskViewSimulator;
            return;
        }
        private final TaskViewSimulator mTaskViewSimulator;


        public TaskViewSimulator getTaskViewSimulator() {
            return this.mTaskViewSimulator;
        }

    }

}
