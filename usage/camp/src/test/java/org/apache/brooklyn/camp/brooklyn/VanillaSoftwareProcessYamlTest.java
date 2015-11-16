/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.brooklyn.camp.brooklyn;

import static org.apache.brooklyn.test.Asserts.assertNotNull;
import static org.apache.brooklyn.test.Asserts.assertTrue;
import static org.testng.Assert.fail;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.EntityAsserts;
import org.apache.brooklyn.core.location.Machines;
import org.apache.brooklyn.entity.software.base.SoftwareProcess;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.test.support.TestResourceUnavailableException;
import org.apache.brooklyn.util.core.ResourceUtils;
import org.apache.brooklyn.util.core.task.ssh.SshTasks;
import org.apache.brooklyn.util.os.Os;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class VanillaSoftwareProcessYamlTest extends AbstractYamlTest {

    @Test(groups = "Integration")
    public void testResourceFromBundleIsLoaded() throws Exception {
        try {
            new ResourceUtils(VanillaSoftwareProcessYamlTest.class).getResourceAsString("classpath://org/apache/brooklyn/test/osgi/resources/message.txt");
            fail("classpath://org/apache/brooklyn/test/osgi/resources/message.txt should not be on classpath");
        } catch (Exception e) {/* expected */}
        TestResourceUnavailableException.throwIfResourceUnavailable(getClass(), "/brooklyn/osgi/brooklyn-test-osgi-entities.jar");
        addCatalogItems(getLocalResource("vanilla-software-process-with-resource.yaml"));

        Entity app = createAndStartApplication(
                "location: localhost",
                "services: [ { type: 'vanilla-software-resource-test:1.0' } ]");
        Entity vsp = Iterables.getOnlyElement(app.getChildren());

        EntityAsserts.assertAttributeEqualsEventually(vsp, SoftwareProcess.SERVICE_PROCESS_IS_RUNNING, true);

        // And check it really copied the file!
        SshMachineLocation machine = Machines.findUniqueMachineLocation(vsp.getLocations(), SshMachineLocation.class).get();
        String file = Os.mergePaths(vsp.sensors().get(SoftwareProcess.RUN_DIR), "message.txt");
        String message = Entities.submit(vsp, SshTasks.newSshFetchTaskFactory(machine, file).newTask()).get();
        assertNotNull(message);
        assertTrue(message.startsWith("Licensed to the Apache Software Foundation"), "expected ASF license header, found: " + message);
    }

    private static String getLocalResource(String filename) {
        return ResourceUtils.create(VanillaSoftwareProcessYamlTest.class).getResourceAsString(
            "classpath:/"+VanillaSoftwareProcessYamlTest.class.getPackage().getName().replace('.', '/')+"/"+filename);
    }
}
