/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples.cluster;

import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

public final class ClusterBootstrap {
    private ClusterBootstrap() {
    }

    public static void main(String[] args) {
        String address = System.getProperty("cluster.address", "127.0.0.1:8700");

        AtomixReplica.builder(new Address(address))
            .withTransport(new NettyTransport())
            .withStorage(Storage.builder()
                .withStorageLevel(StorageLevel.MEMORY)
                .build())
            .build()
            .bootstrap()
            .join();
    }
}
