/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oodt.cas.workflow.engine;

//JDK imports
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

//OODT imports
import org.apache.oodt.cas.workflow.lifecycle.WorkflowLifecycleManager;
import org.apache.oodt.cas.workflow.structs.Graph;
import org.apache.oodt.cas.workflow.structs.ParentChildWorkflow;
import org.apache.oodt.cas.workflow.structs.Priority;
import org.apache.oodt.cas.workflow.structs.WorkflowInstance;
import org.apache.oodt.cas.workflow.structs.WorkflowTask;
import org.apache.oodt.cas.workflow.structs.WorkflowTaskConfiguration;

/**
 * 
 * Utilities for testing the {@link TaskQuerier} and {@link TaskRunner} thread
 * classes.
 * 
 * @author mattmann
 * @version $Revision$
 * 
 */
public class QuerierAndRunnerUtils {

  private int dateGen;
  
  public QuerierAndRunnerUtils() {
    this.dateGen = 0;
  }
  
  public WorkflowTask getTask(File testDir){
    WorkflowTask task = new WorkflowTask();
    task.setConditions(Collections.emptyList());
    task.setRequiredMetFields(Collections.emptyList());
    task.setTaskId("urn:cas:workflow:tester");
    task.setTaskInstanceClassName(SimpleTester.class.getName());
    task.setTaskName("Tester");
    WorkflowTaskConfiguration config = new WorkflowTaskConfiguration();
    config.addConfigProperty("TestDirPath",
        testDir.getAbsolutePath().endsWith("/") ? testDir.getAbsolutePath()
            : testDir.getAbsolutePath() + "/");
    task.setTaskConfig(config);
    return task;
  }

  public WorkflowProcessor getProcessor(double priority, String stateName,
      String categoryName) throws InstantiationException,
      IllegalAccessException, IOException {
    WorkflowLifecycleManager lifecycleManager = new WorkflowLifecycleManager(
        "./src/main/resources/examples/wengine/wengine-lifecycle.xml");
    WorkflowInstance inst = new WorkflowInstance();
    Date sd = new Date();
    sd.setTime(sd.getTime() + (this.dateGen * 5000));
    this.dateGen++;
    inst.setStartDate(sd);
    inst.setId("winst-" + priority);
    ParentChildWorkflow workflow = new ParentChildWorkflow(new Graph());
    workflow.setTasks(Collections.EMPTY_LIST);
    inst.setWorkflow(workflow);
    inst.setPriority(Priority.getPriority(priority));
    WorkflowProcessorBuilder builder = WorkflowProcessorBuilder
        .aWorkflowProcessor().withLifecycleManager(lifecycleManager)
        .withPriority(priority);
    SequentialProcessor processor = (SequentialProcessor) builder
        .build(SequentialProcessor.class);
    processor.setWorkflowInstance(inst);
    processor.setState(lifecycleManager.getDefaultLifecycle().createState(
        stateName, categoryName, ""));
    List<WorkflowProcessor> runnables = new Vector<WorkflowProcessor>();
    TaskProcessor taskProcessor = (TaskProcessor) builder
        .build(TaskProcessor.class);
    taskProcessor.setState(lifecycleManager.getDefaultLifecycle().createState(
        "Queued", "waiting", ""));
    ParentChildWorkflow taskWorkflow = new ParentChildWorkflow(new Graph());    
    taskWorkflow.getTasks().add(getTask(getTmpPath()));
    WorkflowInstance taskWorkflowInst = new WorkflowInstance();
    taskWorkflowInst.setWorkflow(taskWorkflow);
    taskWorkflowInst.setCurrentTaskId(taskWorkflow.getTasks().get(0).getTaskId());
    taskProcessor.setWorkflowInstance(taskWorkflowInst);
    runnables.add(taskProcessor);
    processor.setSubProcessors(runnables);
    return processor;
  }
  
  private File getTmpPath() throws IOException{
    File testDir = null;
    String parentPath = File.createTempFile("test", "txt").getParentFile().getAbsolutePath();
    parentPath = parentPath.endsWith("/") ? parentPath:parentPath + "/";
    String testJobDirPath = parentPath + "jobs";
    testDir = new File(testJobDirPath);
    testDir.mkdirs();
    return testDir;
  }

}
