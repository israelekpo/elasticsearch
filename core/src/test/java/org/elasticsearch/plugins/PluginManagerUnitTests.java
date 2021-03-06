/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.plugins;

import com.google.common.io.Files;
import org.elasticsearch.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 *
 */
public class PluginManagerUnitTests extends ElasticsearchTestCase {

    @Test
    public void testThatConfigDirectoryCanBeOutsideOfElasticsearchHomeDirectory() throws IOException {
        String pluginName = randomAsciiOfLength(10);
        Path homeFolder = createTempDir();
        Path genericConfigFolder = createTempDir();

        Settings settings = settingsBuilder()
                .put("path.conf", genericConfigFolder)
                .put("path.home", homeFolder)
                .build();
        Environment environment = new Environment(settings);

        PluginManager.PluginHandle pluginHandle = new PluginManager.PluginHandle(pluginName, "version", "user", "repo");
        String configDirPath = Files.simplifyPath(pluginHandle.configDir(environment).normalize().toString());
        String expectedDirPath = Files.simplifyPath(genericConfigFolder.resolve(pluginName).normalize().toString());

        assertThat(configDirPath, is(expectedDirPath));
    }

    @Test
    public void testSimplifiedNaming() throws IOException {
        String pluginName = randomAsciiOfLength(10);
        PluginManager.PluginHandle handle = PluginManager.PluginHandle.parse(pluginName);
        assertThat(handle.urls(), hasSize(1));
        URL expected = new URL("http", "download.elastic.co", "/org.elasticsearch.plugins/" + pluginName + "/" +
            pluginName + "-" + Version.CURRENT.number() + ".zip");
        assertThat(handle.urls().get(0), is(expected));
    }
}
