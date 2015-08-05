/**
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver;

import java.io.File;
import java.util.logging.Level;

import org.neo4j.driver.internal.logging.JULogging;
import org.neo4j.driver.internal.spi.Logging;

/**
 * A configuration class to config driver properties.
 * <p>
 * To create a config:
 * <pre>
 * {@code
 * Config config = Config
 *                  .build()
 *                  .withLogging(new MyLogging())
 *                  .toConfig();
 * }
 * </pre>
 */
public class Config
{
    public static final String SCHEME = "neo4j";
    public static final int DEFAULT_PORT = 7687;

    /** User defined logging */
    private final Logging logging;

    /** The size of connection pool for each database url */
    private final int connectionPoolSize;

    /** Connections that have been idle longer than this threshold will have a ping test performed on them. */
    private final long idleTimeBeforeConnectionTest;

    private final boolean isTLSEnabled;
    private final File trustedCertificate;

    private Config( ConfigBuilder builder )
    {
        this.logging = builder.logging;

        this.connectionPoolSize = builder.connectionPoolSize;
        this.idleTimeBeforeConnectionTest = builder.idleTimeBeforeConnectionTest;

        this.isTLSEnabled = builder.isTLSEnabled;
        this.trustedCertificate = builder.trustedCertificate;
    }

    /**
     * Logging provider
     * @return the logging provider to use
     */
    public Logging logging()
    {
        return logging;
    }

    /**
     * Max number of connections per URL for this driver.
     * @return the max number of connections
     */
    public int connectionPoolSize()
    {
        return connectionPoolSize;
    }

    /**
     * Pooled connections that have been unused for longer than this timeout will be tested before they are
     * used again, to ensure they are still live.
     * @return idle time in milliseconds
     */
    public long idleTimeBeforeConnectionTest()
    {
        return idleTimeBeforeConnectionTest;
    }

    /**
     * If TLS is enabled in all socket connections
     * @return
     */
    public boolean isTLSEnabled()
    {
        return isTLSEnabled;
    }

    /**
     * Return the trusted certificate file. If it is not specified, then this method will return null.
     * @return
     */
    public File trustedCertificate()
    {
        return trustedCertificate;
    }

    public static ConfigBuilder build()
    {
        return new ConfigBuilder();
    }

    /**
     * @return A config with all default settings
     */
    public static Config defaultConfig()
    {
        return Config.build().toConfig();
    }

    /**
     * Used to build new config instances
     */
    public static class ConfigBuilder
    {
        private Logging logging = new JULogging( Level.INFO );
        private int connectionPoolSize = 10;
        private long idleTimeBeforeConnectionTest = 200;
        private boolean isTLSEnabled = false;
        private File trustedCertificate = null;

        private ConfigBuilder()
        {

        }

        /**
         * Provide an alternative logging implementation for the driver to use. By default we use
         * java util logging.
         * @param logging the logging instance to use
         * @return this builder
         */
        public ConfigBuilder withLogging( Logging logging )
        {
            this.logging = logging;
            return this;
        }

        /**
         * The max number of connections to open at any given time per Neo4j instance.
         * @param size
         * @return this builder
         */
        public ConfigBuilder withConnectionPoolSize( int size )
        {
            this.connectionPoolSize = size;
            return this;
        }

        /**
         * Pooled connections that have been unused for longer than this timeout will be tested before they are
         * used again, to ensure they are still live.
         * @param milliSecond minimum idle time in milliseconds
         * @return this builder
         */
        public ConfigBuilder withMinIdleTimeBeforeConnectionTest( long milliSecond )
        {
            this.idleTimeBeforeConnectionTest = milliSecond;
            return this;
        }

        /**
         * Enable TLS in all connections with the server. When TLS enabled, if a trusted certificate is provided by
         * invoking {@code withTrustedCertificate}, then only the connections with the trusted certificate will be
         * accepted; if no certificated is provided, then we will trust the first certificate received from the server.
         * @param value
         * @return this builder
         */
        public ConfigBuilder withTLSEnabled( boolean value )
        {
            this.isTLSEnabled = value;
            return this;
        }

        /**
         * Specify the certificate file where a trusted certificate is stored. (The file could contain multiple
         * certificates. )
         * If the certificate file is not provided, then default to trust the first certificate received from the
         * server.
         * @param cert
         * @return this builder
         */
        public ConfigBuilder withTrustedCertificate( File cert )
        {
            this.trustedCertificate = cert;
            return this;
        }

        /**
         * Create a config instance from this builder.
         * @return a {@link Config} instance
         */
        public Config toConfig()
        {
            return new Config( this );
        }
    }
}
