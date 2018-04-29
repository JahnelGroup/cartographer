package com.jahnelgroup.cartographer.core.execute;

import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ExecuteContext {

    @NonNull
    private String eventGroup;

    @NonNull
    private ExecuteService.Execute work;

    @Setter
    private Migration migration;

    @Setter
    private ExecuteService.FailedExecute onFailure;

    public static ExecuteContext C(String eventGroup, ExecuteService.Execute work){
        return new ExecuteContext(eventGroup, work);
    }

}
