/*
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
package org.apache.camel.component.hdfs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.URISupport;
import org.apache.hadoop.io.SequenceFile;

import static org.apache.camel.util.ObjectHelper.isNotEmpty;

@UriParams
public class HdfsConfiguration {

    private URI uri;
    private boolean wantAppend;
    private List<HdfsProducer.SplitStrategy> splitStrategies;

    @UriPath @Metadata(required = true)
    private String hostName;
    @UriPath(defaultValue = "" + HdfsConstants.DEFAULT_PORT)
    private int port = HdfsConstants.DEFAULT_PORT;
    @UriPath @Metadata(required = true)
    private String path;
    @UriParam(label = "producer", defaultValue = "true")
    private boolean overwrite = true;
    @UriParam(label = "producer")
    private boolean append;
    @UriParam(label = "advanced")
    private String splitStrategy;
    @UriParam(label = "advanced", defaultValue = "" + HdfsConstants.DEFAULT_BUFFERSIZE)
    private int bufferSize = HdfsConstants.DEFAULT_BUFFERSIZE;
    @UriParam(label = "advanced", defaultValue = "" + HdfsConstants.DEFAULT_REPLICATION)
    private short replication = HdfsConstants.DEFAULT_REPLICATION;
    @UriParam(label = "advanced", defaultValue = "" + HdfsConstants.DEFAULT_BLOCKSIZE)
    private long blockSize = HdfsConstants.DEFAULT_BLOCKSIZE;
    @UriParam(label = "advanced", defaultValue = "NONE")
    private SequenceFile.CompressionType compressionType = HdfsConstants.DEFAULT_COMPRESSIONTYPE;
    @UriParam(label = "advanced", defaultValue = "DEFAULT")
    private HdfsCompressionCodec compressionCodec = HdfsConstants.DEFAULT_CODEC;
    @UriParam(defaultValue = "NORMAL_FILE")
    private HdfsFileType fileType = HdfsFileType.NORMAL_FILE;
    @UriParam(defaultValue = "HDFS")
    private HdfsFileSystemType fileSystemType = HdfsFileSystemType.HDFS;
    @UriParam(defaultValue = "NULL")
    private WritableType keyType = WritableType.NULL;
    @UriParam(defaultValue = "BYTES")
    private WritableType valueType = WritableType.BYTES;
    @UriParam(label = "advanced", defaultValue = HdfsConstants.DEFAULT_OPENED_SUFFIX)
    private String openedSuffix = HdfsConstants.DEFAULT_OPENED_SUFFIX;
    @UriParam(label = "advanced", defaultValue = HdfsConstants.DEFAULT_READ_SUFFIX)
    private String readSuffix = HdfsConstants.DEFAULT_READ_SUFFIX;
    @UriParam(label = "consumer", defaultValue = HdfsConstants.DEFAULT_PATTERN)
    private String pattern = HdfsConstants.DEFAULT_PATTERN;
    @UriParam(label = "advanced", defaultValue = "" + HdfsConstants.DEFAULT_BUFFERSIZE)
    private int chunkSize = HdfsConstants.DEFAULT_BUFFERSIZE;
    @UriParam(label = "advanced", defaultValue = "" + HdfsConstants.DEFAULT_CHECK_IDLE_INTERVAL)
    private int checkIdleInterval = HdfsConstants.DEFAULT_CHECK_IDLE_INTERVAL;
    @UriParam(defaultValue = "true")
    private boolean connectOnStartup = true;
    @UriParam
    private String owner;

    @UriParam
    private String kerberosNamedNodes;
    private List<String> kerberosNamedNodeList;

    @UriParam
    private String kerberosConfigFileLocation;
    @UriParam
    private String kerberosUsername;
    @UriParam
    private String kerberosKeytabLocation;

    public HdfsConfiguration() {
    }

    private Boolean getBoolean(Map<String, Object> hdfsSettings, String param, Boolean dflt) {
        if (hdfsSettings.containsKey(param)) {
            return Boolean.valueOf((String) hdfsSettings.get(param));
        } else {
            return dflt;
        }
    }

    private Integer getInteger(Map<String, Object> hdfsSettings, String param, Integer dflt) {
        if (hdfsSettings.containsKey(param)) {
            return Integer.valueOf((String) hdfsSettings.get(param));
        } else {
            return dflt;
        }
    }

    private Short getShort(Map<String, Object> hdfsSettings, String param, Short dflt) {
        if (hdfsSettings.containsKey(param)) {
            return Short.valueOf((String) hdfsSettings.get(param));
        } else {
            return dflt;
        }
    }

    private Long getLong(Map<String, Object> hdfsSettings, String param, Long dflt) {
        if (hdfsSettings.containsKey(param)) {
            return Long.valueOf((String) hdfsSettings.get(param));
        } else {
            return dflt;
        }
    }

    private HdfsFileType getFileType(Map<String, Object> hdfsSettings, String param, HdfsFileType dflt) {
        String eit = (String) hdfsSettings.get(param);
        if (eit != null) {
            return HdfsFileType.valueOf(eit);
        } else {
            return dflt;
        }
    }

    private HdfsFileSystemType getFileSystemType(Map<String, Object> hdfsSettings, String param, HdfsFileSystemType dflt) {
        String eit = (String) hdfsSettings.get(param);
        if (eit != null) {
            return HdfsFileSystemType.valueOf(eit);
        } else {
            return dflt;
        }
    }

    private WritableType getWritableType(Map<String, Object> hdfsSettings, String param, WritableType dflt) {
        String eit = (String) hdfsSettings.get(param);
        if (eit != null) {
            return WritableType.valueOf(eit);
        } else {
            return dflt;
        }
    }

    private SequenceFile.CompressionType getCompressionType(Map<String, Object> hdfsSettings, String param, SequenceFile.CompressionType ct) {
        String eit = (String) hdfsSettings.get(param);
        if (eit != null) {
            return SequenceFile.CompressionType.valueOf(eit);
        } else {
            return ct;
        }
    }

    private HdfsCompressionCodec getCompressionCodec(Map<String, Object> hdfsSettings, String param, HdfsCompressionCodec cd) {
        String eit = (String) hdfsSettings.get(param);
        if (eit != null) {
            return HdfsCompressionCodec.valueOf(eit);
        } else {
            return cd;
        }
    }

    private String getString(Map<String, Object> hdfsSettings, String param, String dflt) {
        if (hdfsSettings.containsKey(param)) {
            return (String) hdfsSettings.get(param);
        } else {
            return dflt;
        }
    }

    private List<HdfsProducer.SplitStrategy> getSplitStrategies(Map<String, Object> hdfsSettings) {
        List<HdfsProducer.SplitStrategy> strategies = new ArrayList<>();

        splitStrategy = getString(hdfsSettings, "splitStrategy", kerberosNamedNodes);

        if (isNotEmpty(splitStrategy)) {
            String[] strategyElements = splitStrategy.split(",");
            for (String strategyElement : strategyElements) {
                String[] tokens = strategyElement.split(":");
                if (tokens.length != 2) {
                    throw new IllegalArgumentException("Wrong Split Strategy [splitStrategy" + "=" + splitStrategy + "]");
                }
                HdfsProducer.SplitStrategyType strategyType = HdfsProducer.SplitStrategyType.valueOf(tokens[0]);
                long strategyValue = Long.parseLong(tokens[1]);
                strategies.add(new HdfsProducer.SplitStrategy(strategyType, strategyValue));
            }
        }
        return strategies;
    }

    private List<String> getKerberosNamedNodeList(Map<String, Object> hdfsSettings) {
        kerberosNamedNodes = getString(hdfsSettings, "kerberosNamedNodes", kerberosNamedNodes);
        
        if (isNotEmpty(kerberosNamedNodes)) {
            return Arrays.stream(kerberosNamedNodes.split(",")).distinct().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public void checkConsumerOptions() {
        // no validation required
    }

    public void checkProducerOptions() {
        if (isAppend()) {
            if (hasSplitStrategies()) {
                throw new IllegalArgumentException("Split Strategies incompatible with append=true");
            }
            if (getFileType() != HdfsFileType.NORMAL_FILE) {
                throw new IllegalArgumentException("append=true works only with NORMAL_FILEs");
            }
        }
    }

    public void parseURI(URI uri) throws URISyntaxException {
        String protocol = uri.getScheme();
        if (!protocol.equalsIgnoreCase("hdfs")) {
            throw new IllegalArgumentException("Unrecognized protocol: " + protocol + " for uri: " + uri);
        }
        hostName = uri.getHost();
        if (hostName == null) {
            hostName = "localhost";
        }
        port = uri.getPort() == -1 ? HdfsConstants.DEFAULT_PORT : uri.getPort();
        path = uri.getPath();
        Map<String, Object> hdfsSettings = URISupport.parseParameters(uri);

        overwrite = getBoolean(hdfsSettings, "overwrite", overwrite);
        append = getBoolean(hdfsSettings, "append", append);
        wantAppend = append;
        bufferSize = getInteger(hdfsSettings, "bufferSize", bufferSize);
        replication = getShort(hdfsSettings, "replication", replication);
        blockSize = getLong(hdfsSettings, "blockSize", blockSize);
        compressionType = getCompressionType(hdfsSettings, "compressionType", compressionType);
        compressionCodec = getCompressionCodec(hdfsSettings, "compressionCodec", compressionCodec);
        fileType = getFileType(hdfsSettings, "fileType", fileType);
        fileSystemType = getFileSystemType(hdfsSettings, "fileSystemType", fileSystemType);
        keyType = getWritableType(hdfsSettings, "keyType", keyType);
        valueType = getWritableType(hdfsSettings, "valueType", valueType);
        openedSuffix = getString(hdfsSettings, "openedSuffix", openedSuffix);
        readSuffix = getString(hdfsSettings, "readSuffix", readSuffix);
        pattern = getString(hdfsSettings, "pattern", pattern);
        chunkSize = getInteger(hdfsSettings, "chunkSize", chunkSize);
        splitStrategies = getSplitStrategies(hdfsSettings);

        kerberosNamedNodeList = getKerberosNamedNodeList(hdfsSettings);
        kerberosConfigFileLocation = getString(hdfsSettings, "kerberosConfigFileLocation", kerberosConfigFileLocation);
        kerberosUsername = getString(hdfsSettings, "kerberosUsername", kerberosUsername);
        kerberosKeytabLocation = getString(hdfsSettings, "kerberosKeytabLocation", kerberosKeytabLocation);
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getHostName() {
        return hostName;
    }

    /**
     * HDFS host to use
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    /**
     * HDFS port to use
     */
    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    /**
     * The directory path to use
     */
    public void setPath(String path) {
        this.path = path;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Whether to overwrite existing files with the same name
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isAppend() {
        return append;
    }

    public boolean isWantAppend() {
        return wantAppend;
    }

    /**
     * Append to existing file. Notice that not all HDFS file systems support the append option.
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * The buffer size used by HDFS
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public short getReplication() {
        return replication;
    }

    /**
     * The HDFS replication factor
     */
    public void setReplication(short replication) {
        this.replication = replication;
    }

    public long getBlockSize() {
        return blockSize;
    }

    /**
     * The size of the HDFS blocks
     */
    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public HdfsFileType getFileType() {
        return fileType;
    }

    /**
     * The file type to use. For more details see Hadoop HDFS documentation about the various files types.
     */
    public void setFileType(HdfsFileType fileType) {
        this.fileType = fileType;
    }

    public SequenceFile.CompressionType getCompressionType() {
        return compressionType;
    }

    /**
     * The compression type to use (is default not in use)
     */
    public void setCompressionType(SequenceFile.CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public HdfsCompressionCodec getCompressionCodec() {
        return compressionCodec;
    }

    /**
     * The compression codec to use
     */
    public void setCompressionCodec(HdfsCompressionCodec compressionCodec) {
        this.compressionCodec = compressionCodec;
    }

    /**
     * Set to LOCAL to not use HDFS but local java.io.File instead.
     */
    public void setFileSystemType(HdfsFileSystemType fileSystemType) {
        this.fileSystemType = fileSystemType;
    }

    public HdfsFileSystemType getFileSystemType() {
        return fileSystemType;
    }

    public WritableType getKeyType() {
        return keyType;
    }

    /**
     * The type for the key in case of sequence or map files.
     */
    public void setKeyType(WritableType keyType) {
        this.keyType = keyType;
    }

    public WritableType getValueType() {
        return valueType;
    }

    /**
     * The type for the key in case of sequence or map files
     */
    public void setValueType(WritableType valueType) {
        this.valueType = valueType;
    }

    /**
     * When a file is opened for reading/writing the file is renamed with this suffix to avoid to read it during the writing phase.
     */
    public void setOpenedSuffix(String openedSuffix) {
        this.openedSuffix = openedSuffix;
    }

    public String getOpenedSuffix() {
        return openedSuffix;
    }

    /**
     * Once the file has been read is renamed with this suffix to avoid to read it again.
     */
    public void setReadSuffix(String readSuffix) {
        this.readSuffix = readSuffix;
    }

    public String getReadSuffix() {
        return readSuffix;
    }

    /**
     * The pattern used for scanning the directory
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * When reading a normal file, this is split into chunks producing a message per chunk.
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * How often (time in millis) in to run the idle checker background task. This option is only in use if the splitter strategy is IDLE.
     */
    public void setCheckIdleInterval(int checkIdleInterval) {
        this.checkIdleInterval = checkIdleInterval;
    }

    public int getCheckIdleInterval() {
        return checkIdleInterval;
    }

    public List<HdfsProducer.SplitStrategy> getSplitStrategies() {
        return splitStrategies;
    }

    public boolean hasSplitStrategies() {
        return !splitStrategies.isEmpty();
    }

    public String getSplitStrategy() {
        return splitStrategy;
    }

    /**
     * In the current version of Hadoop opening a file in append mode is disabled since it's not very reliable. So, for the moment,
     * it's only possible to create new files. The Camel HDFS endpoint tries to solve this problem in this way:
     * <ul>
     * <li>If the split strategy option has been defined, the hdfs path will be used as a directory and files will be created using the configured UuidGenerator.</li>
     * <li>Every time a splitting condition is met, a new file is created.</li>
     * </ul>
     * The splitStrategy option is defined as a string with the following syntax:
     * <br/><tt>splitStrategy=ST:value,ST:value,...</tt>
     * <br/>where ST can be:
     * <ul>
     * <li>BYTES a new file is created, and the old is closed when the number of written bytes is more than value</li>
     * <li>MESSAGES a new file is created, and the old is closed when the number of written messages is more than value</li>
     * <li>IDLE a new file is created, and the old is closed when no writing happened in the last value milliseconds</li>
     * </ul>
     */
    public void setSplitStrategy(String splitStrategy) {
        this.splitStrategy = splitStrategy;
    }

    public boolean isConnectOnStartup() {
        return connectOnStartup;
    }

    /**
     * Whether to connect to the HDFS file system on starting the producer/consumer.
     * If false then the connection is created on-demand. Notice that HDFS may take up till 15 minutes to establish
     * a connection, as it has hardcoded 45 x 20 sec redelivery. By setting this option to false allows your
     * application to startup, and not block for up till 15 minutes.
     */
    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    public String getOwner() {
        return owner;
    }

    /**
     * The file owner must match this owner for the consumer to pickup the file. Otherwise the file is skipped.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKerberosNamedNodes() {
        return kerberosNamedNodes;
    }

    /**
     * A comma separated list of kerberos nodes
     * (e.g. srv11.example.com:8021,srv12.example.com:8021) - see kerb5.conf file (https://web.mit.edu/kerberos/krb5-1.12/doc/admin/conf_files/krb5_conf.html)
     */
    public void setKerberosNamedNodes(String kerberosNamedNodes) {
        this.kerberosNamedNodes = kerberosNamedNodes;
    }

    public List<String> getKerberosNamedNodeList() {
        return kerberosNamedNodeList;
    }

    public String getKerberosConfigFileLocation() {
        return kerberosConfigFileLocation;
    }

    /**
     * The location of the kerb5.conf file (https://web.mit.edu/kerberos/krb5-1.12/doc/admin/conf_files/krb5_conf.html)
     */
    public void setKerberosConfigFileLocation(String kerberosConfigFileLocation) {
        this.kerberosConfigFileLocation = kerberosConfigFileLocation;
    }

    public String getKerberosUsername() {
        return kerberosUsername;
    }

    /**
     * The username used to authenticate with the kerberos nodes
     */
    public void setKerberosUsername(String kerberosUsername) {
        this.kerberosUsername = kerberosUsername;
    }

    public String getKerberosKeytabLocation() {
        return kerberosKeytabLocation;
    }

    /**
     * The location of the keytab file used to authenticate with the kerberos nodes
     * (contains pairs of kerberos principals and encrypted keys (which are derived from the Kerberos password))
     */
    public void setKerberosKeytabLocation(String kerberosKeytabLocation) {
        this.kerberosKeytabLocation = kerberosKeytabLocation;
    }

    public boolean isKerberosAuthentication() {
        return isNotEmpty(kerberosNamedNodes) && isNotEmpty(kerberosConfigFileLocation) && isNotEmpty(kerberosUsername) && isNotEmpty(kerberosKeytabLocation);
    }

}
