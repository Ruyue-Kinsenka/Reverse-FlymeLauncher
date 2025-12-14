package com.android.launcher3;

public class LauncherAtom {
    public static class ContainerInfo {
        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            public Builder setWorkspace(WorkspaceContainer workspace) {
                return this;
            }

            public Object build() {
                return new Object();
            }
        }
    }

    public static class WorkspaceContainer {
        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            public Builder setPageIndex(int pageIndex) {
                return this;
            }

            public WorkspaceContainer build() {
                return new WorkspaceContainer();
            }
        }
    }
}